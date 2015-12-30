/*
 * Copyright (c) 2014-2015 by its authors. Some rights reserved.
 * See the project's home at: https://github.com/monifu/asterix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.typesafe.sbt.pgp.PgpKeys
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt.{Build => SbtBuild, _}
import sbtrelease.ReleasePlugin.autoImport._
import sbtunidoc.Plugin._
import sbtunidoc.Plugin.UnidocKeys._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._
import scoverage.ScoverageSbtPlugin

object Build extends SbtBuild {
  val doNotPublishArtifact = Seq(
    publishArtifact := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    publishArtifact in (Compile, packageBin) := false
  )

  val sharedSettings = Seq(
    organization := "org.monifu",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.6", "2.11.7"),

    scalacOptions ++= Seq(
      "-target:jvm-1.6", // generates code with the Java 6 class format
      "-optimise", // enables optimisations
      // "-Xfatal-warnings", // turns all warnings into errors ;-)
      // warnings
      "-unchecked", // able additional warnings where generated code depends on assumptions
      "-deprecation", // emit warning for usages of deprecated APIs
      "-feature", // emit warning usages of features that should be imported explicitly
      // possibly deprecated options
      "-Yinline-warnings",
      "-Ywarn-dead-code",
      "-Ywarn-inaccessible"
    ),

    // version specific compiler options
    scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, majorVersion)) if majorVersion >= 11 =>
        Seq(
          // enables linter options
          "-Xlint:adapted-args", // warn if an argument list is modified to match the receiver
          "-Xlint:nullary-unit", // warn when nullary methods return Unit
          "-Xlint:inaccessible", // warn about inaccessible types in method signatures
          "-Xlint:nullary-override", // warn when non-nullary `def f()' overrides nullary `def f'
          "-Xlint:infer-any", // warn when a type argument is inferred to be `Any`
          "-Xlint:missing-interpolator", // a string literal appears to be missing an interpolator id
          "-Xlint:doc-detached", // a ScalaDoc comment appears to be detached from its element
          "-Xlint:private-shadow", // a private field (or class parameter) shadows a superclass field
          "-Xlint:type-parameter-shadow", // a local type parameter shadows a type already in scope
          "-Xlint:poly-implicit-overload", // parameterized overloaded implicit methods are not visible as view bounds
          "-Xlint:option-implicit", // Option.apply used implicit view
          "-Xlint:delayedinit-select", // Selecting member of DelayedInit
          "-Xlint:by-name-right-associative", // By-name parameter of right associative operator
          "-Xlint:package-object-classes", // Class or object defined in package object
          "-Xlint:unsound-match" // Pattern match may not be typesafe
        )
      case _ =>
        Seq.empty
    }),

    // Turning off fatal warnings for ScalaDoc, otherwise we can't release.
    scalacOptions in (Compile, doc) ~= (_ filterNot (_ == "-Xfatal-warnings")),

    // ScalaDoc settings
    autoAPIMappings := true,
    scalacOptions in (ScalaUnidoc, unidoc) +=
      "-Xfatal-warnings",
    scalacOptions in (ScalaUnidoc, unidoc) +=
      "-Ymacro-expand:none",
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.title(s"Asterix"),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.sourceUrl(s"https://github.com/monifu/asterix/tree/v${version.value}€{FILE_PATH}.scala"),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Seq("-doc-root-content", file("./rootdoc.txt").getAbsolutePath),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.version(s"${version.value}"),
    scalacOptions in ThisBuild ++= Seq(
      // Note, this is used by the doc-source-url feature to determine the
      // relative path of a given source file. If it's not a prefix of a the
      // absolute path of the source file, the absolute path of that file
      // will be put into the FILE_SOURCE variable, which is
      // definitely not what we want.
      "-sourcepath", file(".").getAbsolutePath.replaceAll("[.]$", "")
    ),

    parallelExecution in Test := false,

    resolvers ++= Seq(
      "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      Resolver.sonatypeRepo("releases")
    ),

    // -- Testing

    libraryDependencies += "org.monifu" %%% "minitest" % "0.14" % "test",
    testFrameworks += new TestFramework("minitest.runner.Framework"),

    // -- Settings meant for deployment on oss.sonatype.org

    publishMavenStyle := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseCrossBuild := true,

    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },

    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false }, // removes optional dependencies

    pomExtra :=
      <url>https://github.com/monifu/asterix/</url>
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>https://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:monifu/asterix.git</url>
          <connection>scm:git:git@github.com:monifu/asterix.git</connection>
        </scm>
        <developers>
          <developer>
            <id>alex_ndc</id>
            <name>Alexandru Nedelcu</name>
            <url>https://www.bionicspirit.com/</url>
          </developer>
        </developers>
  )

  val crossSettings = sharedSettings ++ Seq(
    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _ % "compile"),
    unmanagedSourceDirectories in Compile <+= baseDirectory(_ / ".." / "shared" / "src" / "main" / "scala"),
    unmanagedSourceDirectories in Test <+= baseDirectory(_ / ".." / "shared" / "src" / "test" / "scala")
  )

  lazy val asterix = project.in(file("."))
    .aggregate(asterixAtomicJVM, asterixAtomicJS, asterixJVM, asterixJS)
    .settings(unidocSettings: _*)
    .settings(sharedSettings: _*)
    .settings(doNotPublishArtifact: _*)
    .settings(
      unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject --
        inProjects(asterixAtomicJS, asterixJS, asterixJVM)
    )

  lazy val asterixAtomicJVM = project.in(file("atomic/jvm"))
    .settings(crossSettings: _*)
    .settings(name := "asterix-atomic")

  lazy val asterixAtomicJS = project.in(file("atomic/js"))
    .settings(crossSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "asterix-atomic",
      scalaJSStage in Test := FastOptStage,
      coverageExcludedFiles := ".*")

  lazy val asterixJVM = project.in(file("asterix/jvm"))
    .settings(crossSettings: _*)
    .aggregate(asterixAtomicJVM)
    .dependsOn(asterixAtomicJVM)
    .settings(name := "asterix")

  lazy val asterixJS = project.in(file("asterix/js"))
    .settings(crossSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .aggregate(asterixAtomicJS)
    .dependsOn(asterixAtomicJS)
    .settings(name := "asterix")
}
