
////////////////////////////////////////////////////////////////////////////
// Project settings
////////////////////////////////////////////////////////////////////////////
name := """SpoonWeb"""

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb, BuildInfoPlugin)

scalaVersion := "2.12.2"

////////////////////////////////////////////////////////////////////////////
// Resolvers
////////////////////////////////////////////////////////////////////////////
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers ++= Seq(
  Resolver.mavenLocal,
  "BMD Software Public" at "https://dev.bmd-software.com/nexus/content/groups/public/",
  "BMD Software Releases" at "https://dev.bmd-software.com/nexus/content/repositories/releases/",
  "BMD Software Snapshots" at "https://dev.bmd-software.com/nexus/content/repositories/snapshots/",
  "pentaho-repo" at "https://public.nexus.pentaho.org/content/groups/omni/"
)

////////////////////////////////////////////////////////////////////////////
// Dependencies
////////////////////////////////////////////////////////////////////////////
libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  javaWs,
  cache,
  // Multi-language
  "org.julienrf" %% "play-jsmessages" % "3.0.0",
  // RequireJS
  "org.webjars" % "requirejs" % "2.3.3",
  // jQuery
  "org.webjars" % "jquery" % "3.2.1",
  // Underscore
  "org.webjars" % "underscorejs" % "1.8.3",
  // Bootstrap
  "org.webjars" % "bootstrap" % "3.3.7" exclude("org.webjars", "jquery"),
  // Font awesome
  "org.webjars" % "font-awesome" % "4.7.0",
  // Handlebars
  "org.webjars" % "handlebars" % "4.0.5",

  // Pentaho SDK
  "pentaho-kettle" % "kettle-core" % "6.1.0.0-184",
  "pentaho-kettle" % "kettle-dbdialog" % "6.1.0.0-184",
  "pentaho-kettle" % "kettle-engine" % "6.1.0.0-184",
  "pentaho" % "pentaho-capability-manager" % "6.1.0.10-308",
  "pentaho-kettle" % "kettle-ui-swt" % "6.1.0.10-308",
  "pentaho-kettle" % "kettle-json-plugin" % "7.0.0.1-37",

  // Draw.io SDK
  "de.twentyeleven.skysail" % "jgraphx-osgi" % "1.10.3.1",

  // Jackson
  "com.fasterxml.jackson.core" % "jackson-core" % "2.3.3",
  "org.codehaus.jackson" % "jackson-core-asl" % "1.9.2",
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.2",

  //ESAPI
  "org.owasp.esapi" % "esapi" % "2.1.0"
)

// Testing libraries for dealing with CompletionStage...
libraryDependencies ++= Seq(
  "org.assertj" % "assertj-core" % "3.6.2" % Test,
  "org.awaitility" % "awaitility" % "2.0.0" % Test,
  // Test Database
  "com.h2database" % "h2" % "1.4.194"
)

////////////////////////////////////////////////////////////////////////////
// Test settings
////////////////////////////////////////////////////////////////////////////

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

////////////////////////////////////////////////////////////////////////////
// Build info settings
////////////////////////////////////////////////////////////////////////////
buildInfoKeys ++= Seq[BuildInfoKey](
  name,
  version,
  scalaVersion,
  sbtVersion,
  BuildInfoKey.action("buildTime") {
    System.currentTimeMillis
  }
)

buildInfoPackage := "com.bmd.heartteam"

////////////////////////////////////////////////////////////////////////////
// Play settings
////////////////////////////////////////////////////////////////////////////
PlayKeys.externalizeResources := true

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

pipelineStages := Seq(rjs)

////////////////////////////////////////////////////////////////////////////
// Exclude documentation from dist package
////////////////////////////////////////////////////////////////////////////
sources in(Compile, doc) := Seq.empty

publishArtifact in(Compile, packageDoc) := false

////////////////////////////////////////////////////////////////////////////
// Handlebars settings
////////////////////////////////////////////////////////////////////////////
HbsKeys.root := "templates/"

HbsKeys.amd := true

////////////////////////////////////////////////////////////////////////////
// RequireJS settings
////////////////////////////////////////////////////////////////////////////
RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:"))

RjsKeys.webJarCdns := Map()

RjsKeys.generateSourceMaps := false

excludeFilter in rjs := (excludeFilter in rjs).value || GlobFilter("*.css")// || GlobFilter("*.min.js")

////////////////////////////////////////////////////////////////////////////
// Less settings
////////////////////////////////////////////////////////////////////////////
LessKeys.compress in Assets := true

LessKeys.cleancss in Assets := true