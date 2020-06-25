# BI Center

This is a web-based platform that allows the building and management of ETL pipelines, by non-IT users, in a multi-institution environment.

## Running

Run this using [sbt](http://www.scala-sbt.org/) (you need to have `sbt` installed for this to work).

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

### Troubleshooting

If you run into any problems, please check the following suggestions:

-   In case of `sun.security.validator.ValidatorException` or `sun.security.provider.certpath.SunCertPathBuilderException`:
    1. Export the CA certificate from the specified URL
    2. Update java certificates with the specific certificate
        ```bash
        cd $JAVA_HOME/lib/security
        sudo keytool -import -trustcacerts -cacerts -alias sectigo -file path/to/file
        ```
- In the case of missing dependencies during the installation, such as:
    - sbt.ResolveException: unresolved dependency: com.bmdsoftware#sbt-rjs;1.0.9: not found
        ```bash
        git clone https://github.com/BMDSoftware/sbt-rjs.git
        cd sbt-rjs
        sbt publishLocal
        ```
    - sbt.ResolveException: unresolved dependency: com.bicou.sbt#sbt-hbs;1.0.8: not found
        ```bash
        git clone https://github.com/BMDSoftware/sbt-hbs.git
        cd sbt-hbs
        sbt publishLocal
        ```
    - sbt.ResolveException: unresolved dependency: jline#jline;2.11: not found
        - Add repositories into the file `~/.sbt/repositories` (This file may not exist) [more details](https://stackoverflow.com/questions/42438544/sbt-installation-error-module-not-found-org-scala-sbt-ivyivy2-3-0-sbt-2cf13e)
        ```ini
        [repositories]
        local
        sbt-releases-repo: https://repo.typesafe.com/typesafe/ivy-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]
        sbt-plugins-repo: https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]
        maven-central: https://repo1.maven.org/maven2/
        ```

## Bugs and features requests

Have a bug or a feature request?

If your problem or idea is not addressed yet, please [open a new issue](https://github.com/BMDSoftware/BIcenter/issues/new).

## Support and consulting

[<img src="https://raw.githubusercontent.com/wiki/BMDSoftware/dicoogle/images/bmd.png" height="64" alt="BMD Software">](https://www.bmd-software.com)

Please contact [BMD Software](https://www.bmd-software.com) / info@bmd-software for professional support and consulting services.

## Copyright and license

Copyright (C) 2020 BMD Software
