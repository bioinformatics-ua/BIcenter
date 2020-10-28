[<img src="https://img.shields.io/travis/playframework/play-java-starter-example.svg"/>](https://travis-ci.org/playframework/play-java-starter-example)

# play-java-starter-example

This is a starter application that shows how Play works.  Please see the documentation at https://www.playframework.com/documentation/latest/Home for more details.

## Running

Run this using [sbt](http://www.scala-sbt.org/).  If you downloaded this project from http://www.playframework.com/download then you'll find a prepackaged version of sbt in the project directory:

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