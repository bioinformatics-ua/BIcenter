# BI Center

This is a web-based platform that allows the building and management of ETL pipelines, by non-IT users, in a multi-institution environment.

## Running

Run this using [sbt](http://www.scala-sbt.org/) (you need to have `sbt` installed for this to work).

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

If you run into any problems, please check the following suggestions:

-   In case of `sun.security.validator.ValidatorException` or `sun.security.provider.certpath.SunCertPathBuilderException`:
    1. Export the CA certificate from the specified URL
    2. Update java certificates with the specific certificate
        ```bash
        cd $JAVA_HOME/lib/security
        sudo keytool -import -trustcacerts -cacerts -alias sectigo -file path/to/file
        ```

## Bugs and features requests

Have a bug or a feature request?

If your problem or idea is not addressed yet, please [open a new issue](https://github.com/BMDSoftware/BIcenter/issues/new).

## Support and consulting

[<img src="https://raw.githubusercontent.com/wiki/BMDSoftware/dicoogle/images/bmd.png" height="64" alt="BMD Software">](https://www.bmd-software.com)

Please contact [BMD Software](https://www.bmd-software.com) / info@bmd-software for professional support and consulting services.

## Copyright and license

Copyright (C) 2020 BMD Software
