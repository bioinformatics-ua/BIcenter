logLevel := Level.Warn

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers ++= Seq(
	Resolver.mavenLocal,
	Resolver.sbtPluginRepo("releases"),
	"Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
	"Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
	"Pentaho Repository" at "https://public.nexus.pentaho.org/content/groups/omni/"
)

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.2.1")

addSbtPlugin("com.bmdsoftware" % "sbt-rjs" % "1.0.9")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")

addSbtPlugin("com.bicou.sbt" % "sbt-hbs" % "1.0.8")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

// Play enhancer - this automatically generates getters/setters for public fields
// and rewrites accessors of these fields to use the getters/setters. Remove this
// plugin if you prefer not to have this feature, or disable on a per project
// basis using disablePlugins(PlayEnhancer) in your build.sbt
//addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")

