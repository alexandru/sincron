logLevel := Level.Warn

resolvers ++= Seq(
  Classpaths.sbtPluginReleases,
  Resolver.url("scala-js-releases",
    url("http://dl.bintray.com/scala-js/scala-js-releases"))(
      Resolver.ivyStylePatterns)
)

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.5")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")

addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.3")

// SBT-Scoverage version must be compatible with SBT-coveralls version below
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.1")

// Upgrade when this issue is solved https://github.com/scoverage/sbt-coveralls/issues/73
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")
