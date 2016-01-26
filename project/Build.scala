/*
 * Copyright (c) 2016 by its authors. Some rights reserved.
 * See the project homepage at: https://sincron.org
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

import com.typesafe.sbt.SbtSite._
import com.typesafe.sbt.pgp.PgpKeys
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt.{Build => SbtBuild, _}
import sbtrelease.ReleasePlugin.autoImport._
import sbtunidoc.Plugin.UnidocKeys._
import sbtunidoc.Plugin.{ScalaUnidoc, unidocSettings => baseUnidocSettings}
import scoverage.ScoverageSbtPlugin.autoImport._
import tut.Plugin._
import com.typesafe.sbt.site.PreprocessSupport.preprocessVars
import com.typesafe.sbt.SbtSite.SiteKeys._
import java.text.SimpleDateFormat
import java.util.Date

object Build extends SbtBuild {
  lazy val doNotPublishArtifact = Seq(
    publishArtifact := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    publishArtifact in (Compile, packageBin) := false
  )

  lazy val sharedSettings = Seq(
    organization := "org.sincron",
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

    // Force building with Java 8
    initialize := {
      val required = "1.8"
      val current  = sys.props("java.specification.version")
      assert(current == required, s"Unsupported build JDK: java.specification.version $current != $required")
    },

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

    libraryDependencies += "io.monix" %%% "minitest" % "0.16" % "test",
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
      <url>https://github.com/monixio/sincron/</url>
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>https://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:monixio/sincron.git</url>
          <connection>scm:git:git@github.com:monixio/sincron.git</connection>
        </scm>
        <developers>
          <developer>
            <id>alex_ndc</id>
            <name>Alexandru Nedelcu</name>
            <url>https://www.bionicspirit.com/</url>
          </developer>
        </developers>
  )

  lazy val unidocSettings = baseUnidocSettings ++ Seq(
    autoAPIMappings := true,
    unidocProjectFilter in (ScalaUnidoc, unidoc) :=
      inProjects(atomicJVM),

    scalacOptions in (ScalaUnidoc, unidoc) +=
      "-Xfatal-warnings",
    scalacOptions in (ScalaUnidoc, unidoc) +=
      "-Ymacro-expand:none",
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.title(s"Monix"),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.sourceUrl(s"https://github.com/monifu/monix/tree/v${version.value}€{FILE_PATH}.scala"),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Seq("-doc-root-content", file("docs/rootdoc.txt").getAbsolutePath),
    scalacOptions in (ScalaUnidoc, unidoc) ++=
      Opts.doc.version(s"${version.value}")
  )

  lazy val docsSettings =
    site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api") ++
    site.addMappingsToSiteDir(tut, "_tut") ++
    Seq(
      (test in Test) <<= (test in Test).dependsOn(tut),
      coverageExcludedFiles := ".*",
      siteMappings += file("CONTRIBUTING.md") -> "contributing.md",
      includeFilter in makeSite :=
        "*.html" | "*.css" | "*.scss" | "*.png" | "*.jpg" | "*.jpeg" |
        "*.gif" | "*.svg" | "*.js" | "*.swf" | "*.yml" | "*.md" | "*.xml",

      preprocessVars := {
        val now = new Date()
        val dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = new SimpleDateFormat("HH:mm:ss")

        Map(
          "VERSION" -> version.value,
          "DATE" -> dayFormat.format(now),
          "TIME" -> timeFormat.format(now)
        )
      }
    )

  lazy val crossSettings = sharedSettings ++ Seq(
    unmanagedSourceDirectories in Compile <+= baseDirectory(_.getParentFile / "shared" / "src" / "main" / "scala"),
    unmanagedSourceDirectories in Test <+= baseDirectory(_.getParentFile / "shared" / "src" / "test" / "scala")
  )

  lazy val scalaMacroDependencies = Seq(
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
        case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq.empty
        // in Scala 2.10, quasiquotes are provided by macro paradise
        case Some((2, 10)) =>
          Seq(
            compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full),
            "org.scalamacros" %% "quasiquotes" % "2.0.1" cross CrossVersion.binary
          )
      }
    })

  lazy val crossVersionSharedSources =
    Seq(Compile, Test).map { sc =>
      (unmanagedSourceDirectories in sc) ++= {
        (unmanagedSourceDirectories in sc ).value.map {
          dir:File => new File(dir.getPath + "_" + scalaBinaryVersion.value)
        }
      }
    }

  lazy val sincron = project.in(file("."))
    .aggregate(
      atomicJVM, atomicJS,
      sincronJVM, sincronJS,
      docs)
    .settings(unidocSettings: _*)
    .settings(sharedSettings: _*)
    .settings(doNotPublishArtifact: _*)
    .settings(
      unidocProjectFilter in (ScalaUnidoc, unidoc) :=
        inProjects(atomicJVM)
    )

  lazy val atomicJVM = project.in(file("sincron-atomic/jvm"))
    .settings(crossSettings)
    .settings(name := "sincron-atomic")
    .settings(crossVersionSharedSources)
    .settings(scalaMacroDependencies)

  lazy val atomicJS = project.in(file("sincron-atomic/js"))
    .settings(crossSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .settings(crossVersionSharedSources)
    .settings(scalaMacroDependencies)
    .settings(
      name := "sincron-atomic",
      scalaJSStage in Test := FastOptStage,
      coverageExcludedFiles := ".*")

  lazy val sincronJVM = project.in(file("sincron/jvm"))
    .settings(crossSettings: _*)
    .aggregate(atomicJVM)
    .dependsOn(atomicJVM)
    .settings(name := "sincron")

  lazy val sincronJS = project.in(file("sincron/js"))
    .settings(crossSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .aggregate(atomicJS)
    .dependsOn(atomicJS)
    .settings(name := "sincron")

  lazy val docs = project.in(file("docs"))
    .dependsOn(atomicJVM)
    .settings(sharedSettings)
    .settings(doNotPublishArtifact)
    .settings(site.settings)
    .settings(tutSettings)
    .settings(unidocSettings)
    .settings(docsSettings)
}
