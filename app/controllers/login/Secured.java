/*
 * Copyright (c) 2014 BMD Software, Lda.
 * All Rights Reserved
 *
 * All information contained herein is, and remains the property of BMD Software, Lda. and its suppliers, if any.
 * The intellectual and technical concepts contained herein are proprietary to BMD Software, Lda. and its suppliers,
 * being protected by trade secret or copyright law. Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission is obtained from BMD Software, Lda.
 */

package controllers.login;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

/**
 * Created by David Campos on 21/02/14.
 */
public class Secured extends Security.Authenticator {
    private final Config config;

    @Inject
    public Secured(Config config) {
        this.config = config;
    }

    @Override
    public String getUsername(Http.Context ctx) {
        // Get and check user email
        String userEmail = ctx.session().get("userEmail");
        if (userEmail == null || userEmail.isEmpty()) {
            return null;
        }

        return userEmail;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        // Clean session and cookie
        Login.clearSession();

        // Ser URL that is coming from
        String baseUrl = config.getString("application.url");
        String url = baseUrl + ctx.request().uri();
//        ctx.session().put("previousURL", url);
        ctx.response().setCookie(Http.Cookie.builder("previousURL", url).build());

        // Check if there is username and password
        String email = ctx.request().getQueryString("email");
        String password = ctx.request().getQueryString("password");
        if (email != null && password != null) {
            ctx.session().put("email", email);
            ctx.session().put("password", password);
        }

        return Results.redirect(controllers.login.routes.Login.index());
    }

}
