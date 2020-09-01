# BIcenter
This is a web-based platform that allows the building and management of ETL pipelines, by non-IT users, in a multi-institution environment.

## Development
<details>
  <summary>Manual</summary>

  ### Usage
  Run this using [sbt](http://www.scala-sbt.org/) (you need to have `sbt` installed for this to work).

  1. Run Pentaho server: `sh run.sh`. In case it fails due to permissions, either configure your docker to run without requiring *sudo* or run ```sudo sh run.sh``` instead.
  2. Create database with the specifications found in [application.conf](conf/application.conf)
  3. Run Web App: `sbt run`
  4. In the MySQL database, add the necessary items to User_Institution:
      ```mysql
      use teste;
      insert into User_Institution values (1, 1);
      insert into User_Institution values (1, 2);
      ```
  5. On the [web app](http://localhost:9000), preform the following operations:
      1. On the resources tab, add:
          * The **Data source(s)**. In here you should specify the databases you want to connect to.
          * The **Task(s)**. These are the tasks to which you can drag components into and define your flux.
      2. After having defined at least one task, choose it and add the components you desire into the graph editor and edit them as you see fit.

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
</details>

<details>
  <summary>Docker</summary>

  ### Usage
  Run: `sh launch-dev.sh`

</details>

## Penatho (Carte) Cluster
Run: `sh launch-pentaho-cluster.sh`


## Controllers
There are several demonstration files available in this template.

- HomeController.java:
  Shows how to handle simple HTTP requests.

- AsyncController.java:
  Shows how to do asynchronous programming when handling a request.

- CountController.java:
  Shows how to inject a component into a controller and use the component when
  handling requests.

## Components
- Module.java:
  Shows how to use Guice to bind all the components needed by your application.

- Counter.java:
  An example of a component that contains state, in this case a simple counter.

- ApplicationTimer.java:
  An example of a component that starts when the application starts and stops
  when the application stops.

## Filters
- ExampleFilter.java
  A simple filter that adds a header to every response.

## Bugs and features requests
Have a bug, or a feature request?
If your problem or idea is not yet addressed, please [open a new issue](https://github.com/bioinformatics-ua/BIcenter/issues/new).

## Support and consulting
[<img src="https://raw.githubusercontent.com/wiki/BMDSoftware/dicoogle/images/bmd.png" height="64" alt="BMD Software">](https://www.bmd-software.com)

Please contact [BMD Software](https://www.bmd-software.com) / info@bmd-software for professional support and consulting services.

## Copyright and license
Copyright (C) 2020 BMD Software
