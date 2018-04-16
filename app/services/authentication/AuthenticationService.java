package services.authentication;

import com.google.inject.Inject;
import models.Institution;
import models.authentication.Authentication;
import models.rbac.Role;
import models.rbac.User;
import play.Logger;
import play.db.jpa.JPAApi;
import repositories.JPAInstitutionRepository;
import repositories.authentication.JPAAuthenticationRepository;
import repositories.user.JPARBACRepository;
import repositories.user.JPAUserRepository;
import services.JPAService;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.security.Key;
import java.security.MessageDigest;
import java.util.*;

public class AuthenticationService extends JPAService {
    private static final String MEMBER_OF = "memberOf";

    private final JPAAuthenticationRepository authenticationRepository;
    private final JPAUserRepository userRepository;
    private final JPARBACRepository rbacRepository;
    private final JPAInstitutionRepository institutionRepository;

    @Inject
    public AuthenticationService(JPAApi jpaApi, JPAAuthenticationRepository authenticationRepository, JPAUserRepository userRepository, JPARBACRepository rbacRepository, JPAInstitutionRepository institutionRepository) {
        super(jpaApi);
        this.authenticationRepository = authenticationRepository;
        this.userRepository = userRepository;
        this.rbacRepository = rbacRepository;
        this.institutionRepository = institutionRepository;
    }

    public static String getCN(String cnName) {
        if (cnName != null && cnName.toUpperCase().startsWith("CN=")) {
            cnName = cnName.substring(3);
        }
        int position = cnName.indexOf(',');
        if (position == -1) {
            return cnName;
        } else {
            return cnName.substring(0, position);
        }
    }

    public static byte[] encryptPassword(String keyString, String password) throws Exception {
        // Generate key from Hostname
        byte[] key = keyString.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        // Create key and cipher
        Key aesKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");

        // encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        return cipher.doFinal(password.getBytes());
    }

    public static String decryptPassword(String keyString, byte[] password) throws Exception {
        // Generate key from Hostname
        byte[] key = keyString.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        // Create key and cipher
        Key aesKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");

        // decrypt the text
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return (new String(cipher.doFinal(password)));
    }

    public Authentication createAuthentication(Authentication authentication, List<String> roles, List<String> institutions) {
        return withTransaction(em -> {
            List<Role> _roles = new ArrayList<>();
            for (String r : roles) {
                Role role = rbacRepository.findRole(em, r);
                if (role == null) {
                    System.err.println("User role not found: " + r);
                    continue;
                }

                _roles.add(role);
            }

            List<Institution> _institutions = new ArrayList<>();
            for (String i : institutions) {
                Institution institution = institutionRepository.getByName(em, i);
                if (institution == null) {
                    System.err.println("User institution not found: " + i);
                    continue;
                }

                _institutions.add(institution);
            }

            authentication.getRoles().addAll(_roles);
            authentication.getInstitutions().addAll(_institutions);

            authenticationRepository.create(em, authentication);

            return authentication;
        });
    }

    public List<Authentication> findAll() {
        return authenticationRepository.findAll();
    }

    public User authenticate(final Authentication authentication, final String username, final String password) throws NamingException {
        switch (authentication.getType()) {
            case LDAP:
                return authenticateLDAP(authentication, username, password);
            case AD:
                return authenticateAD(authentication, username, password);
            default:
                return null;
        }
    }

    private User authenticateLDAP(final Authentication authentication, final String username, final String password) throws NamingException {
        Logger.debug("Trying to authenticate via LDAP (" + authentication.getHostname() + ")");
        Hashtable<String, String> env = new Hashtable<>();

        String ldapURL = "ldap://" + authentication.getHostname() + ":" + authentication.getPort();
        String filter = authentication.getUsernameAttributes() + "=" + username;

        // Set initial parameters without login
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "" + 3000);
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        boolean checkMember = false;
        String newBase = authentication.getBaseDN();
        if (authentication.getDefaultUser() != null && !authentication.getDefaultUser().equals("")) {
            String baseDN = authentication.getBaseDN();
            if (baseDN.toLowerCase().startsWith("cn=")) {
                int d = baseDN.indexOf("DC=");
                newBase = baseDN.substring(d, baseDN.length());
                checkMember = true;
            }
            String defaultDN = "cn=" + authentication.getDefaultUser() + "," + newBase;

            env.put(Context.SECURITY_PRINCIPAL, authentication.getDefaultUser());

            try {
                String _password = decryptPassword(authentication.getHostname(), authentication.getDefaultPassword());

                env.put(Context.SECURITY_CREDENTIALS, _password);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // Init context
        DirContext authContext = null;
        authContext = new InitialDirContext(env);

        // Create search
        SearchControls searchCtrls = new SearchControls();
        searchCtrls.setReturningAttributes(new String[]{});
        searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // Search for user
        NamingEnumeration<SearchResult> answer = authContext.search(newBase, filter, searchCtrls);

        String fullDN = null;
        if (answer.hasMore()) {
            // Get full DN for user
            fullDN = answer.next().getNameInNamespace();
            Logger.info("fullDN: " + fullDN);
            // CLose context
            authContext.close();
            authContext = null;

            // Add additional parameters to authenticate
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, fullDN);
            env.put(Context.SECURITY_CREDENTIALS, password);
            authContext = new InitialDirContext(env);


            // realname attributes separated
            String[] realnameAttrs = authentication.getRealnameAttributes().split(",");

            // Add returning attributes to search
            String[] retAttributes = Arrays.copyOf(realnameAttrs, realnameAttrs.length + 2);
            retAttributes[retAttributes.length - 2] = "mail";
            retAttributes[retAttributes.length - 1] = MEMBER_OF;
            searchCtrls.setReturningAttributes(retAttributes);

            // Search for user details
            String fullDnAttr = fullDN;
            if (checkMember) {
                fullDnAttr = newBase;
            }
            NamingEnumeration results = authContext.search(fullDnAttr, filter, searchCtrls);
            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();

                if (attributes != null) {
                    // Get email
                    Attribute attr = attributes.get("mail");
                    String mail = (String) attr.get();
                    Logger.info("mail: " + mail);
                    // Look for and process memberOf
                    if (checkMember) {
                        Attribute memberOf = attributes.get(MEMBER_OF);
                        if (memberOf != null) {
                            boolean found = false;
                            for (Enumeration e1 = memberOf.getAll(); e1.hasMoreElements(); ) {
                                String unprocessedGroupDN = e1.nextElement().toString();
                                if (unprocessedGroupDN.toLowerCase().equals(authentication.getBaseDN().toLowerCase())) {
                                    found = true;
                                    break;
                                }

                            }
                            if (!found)
                                return null;
                        }
                    }

                    // Get full name
                    String fullName = "";
                    for (String s : realnameAttrs) {
                        attr = attributes.get(s);

                        if (attr != null) {
                            fullName += attr.get() + " ";
                        }
                    }
                    fullName = fullName.trim();

                    Logger.debug("LDAP: Authenticated: " + fullName + " (" + mail + ")");


                    User user = userRepository.findByEmail(mail);
                    if (user == null) {
                        user = registerUser(fullName, mail, authentication.getRoles(), authentication.getInstitutions());
                    } else {
                        user.setName(fullName);
                        userRepository.update(user);
                    }

                    return user;
                }
            }

        }

        return null;
    }

    private User authenticateAD(final Authentication authentication, final String username, final String password) {
//        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + username + "))";
        String ldapURL = "ldap://" + authentication.getHostname() + ":" + authentication.getPort();
        String filter = authentication.getUsernameAttributes() + "=" + username;

        //Create the search controls
        SearchControls searchCtls = new SearchControls();

        // realname attributes separated
        String[] realnameAttrs = authentication.getRealnameAttributes().split(",");

        // Add returning attributes to search
        String[] retAttributes = Arrays.copyOf(realnameAttrs, realnameAttrs.length + 1);
        retAttributes[realnameAttrs.length] = "mail";
        searchCtls.setReturningAttributes(retAttributes);

        //Specify the search scope
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "" + 3000);
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username + "@" + authentication.getDomain());
        env.put(Context.SECURITY_CREDENTIALS, password);

        LdapContext ctxGC = null;

        try {
            ctxGC = new InitialLdapContext(env, null);
            //Search objects in GC using filters
            NamingEnumeration answer = ctxGC.search(authentication.getBaseDN(), filter, searchCtls);

            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    // Get email
                    Attribute attr = attrs.get("mail");
                    String mail = (String) attr.get();

                    // Get full name
                    String fullName = "";
                    for (String s : realnameAttrs) {
                        attr = attrs.get(s);

                        if (attr != null) {
                            fullName += attr.get() + " ";
                        }
                    }
                    fullName = fullName.trim();

                    Logger.debug("AD: Authenticated: " + fullName + " (" + mail + ")");

                    User user = userRepository.findByEmail(mail);
                    if (user == null) {
                        user = registerUser(fullName, mail, authentication.getRoles(), authentication.getInstitutions());
                    }

                    return user;
                }
            }
        } catch (NamingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private User registerUser(String fullName, String mail, List<Role> roles, List<Institution> institutions) {
        User user;

        user = new User(mail, fullName, "", User.UserType.LDAP);
        userRepository.add(user);

        // Roles
        user.getRoles().addAll(roles);

        // Institutions
        user.getInstitutions().addAll(institutions);

        user.setActive(true);
        user = userRepository.update(user);

        return user;
    }

    public boolean testConnection(Authentication authentication) {
        switch (authentication.getType()) {
            case LDAP:
                return testConnectionLDAP(authentication);
            case AD:
                return testConnectionAD(authentication);
            default:
                return false;
        }
    }

    private boolean testConnectionLDAP(Authentication authentication) {
        Logger.debug("[" + authentication.getHostname() + "] Testing LDAP connection");
        Hashtable<String, String> env = new Hashtable<>();

        String ldapURL = "ldap://" + authentication.getHostname() + ":" + authentication.getPort();

        // Set initial parameters without login
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "5000");
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        if (authentication.getDefaultUser() != null && !authentication.getDefaultUser().equals("")) {
            String defaultDN = "cn=" + authentication.getDefaultUser() + "," + authentication.getBaseDN();
            env.put(Context.SECURITY_PRINCIPAL, defaultDN);
        }
        if (authentication.getDefaultPassword() != null) {
            try {

                String password = decryptPassword(authentication.getHostname(), authentication.getDefaultPassword());

                env.put(Context.SECURITY_CREDENTIALS, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean result = false;
        try {
            // Init context
            DirContext authContext = new InitialDirContext(env);

            // Create search
            SearchControls searchCtrls = new SearchControls();
            searchCtrls.setReturningAttributes(new String[]{});
            searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String filter = authentication.getUsernameAttributes() + "=*";
            // Search for user
            NamingEnumeration<SearchResult> answer = authContext.search(authentication.getBaseDN(), filter, searchCtrls);

            if (answer.hasMore()) {
                result = true;
            }

            authContext.close();
        } catch (NamingException e) {
            Logger.error("Could not connect to LDAP host: " + authentication.getHostname(), e);
            return false;
        }

        if (result) {
            Logger.debug("[" + authentication.getHostname() + "] Connection successful");
        }
        return result;
    }

    private boolean testConnectionAD(Authentication authentication) {
        Logger.debug("[" + authentication.getHostname() + "] Testing AD connection");

        String ldapURL = "ldap://" + authentication.getHostname() + ":" + authentication.getPort();
        String filter = authentication.getUsernameAttributes() + "=*";

        //Create the search controls
        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(new String[]{});
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "5000");
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        if (authentication.getDefaultUser() != null && !authentication.getDefaultUser().equals("")) {
            env.put(Context.SECURITY_PRINCIPAL, authentication.getDefaultUser() + "@" + authentication.getDomain());
        }

        if (authentication.getDefaultPassword() != null) {
            try {
                String password = decryptPassword(authentication.getHostname(), authentication.getDefaultPassword());

                env.put(Context.SECURITY_CREDENTIALS, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LdapContext ctxGC = null;

        boolean result = false;
        try {
            ctxGC = new InitialLdapContext(env, null);

            //Search objects in GC using filters
            NamingEnumeration answer = ctxGC.search(authentication.getBaseDN(), filter, searchCtls);

            if (answer.hasMore()) {
                result = true;
            }
        } catch (NamingException e) {
            Logger.error("Could not connect to AD host: " + authentication.getHostname(), e);
            return false;
        }


        if (result) {
            Logger.debug("[" + authentication.getHostname() + "] Connection successful");
        } else {
            Logger.error("Could not connect to AD host: " + authentication.getHostname());
        }
        return result;
    }
}