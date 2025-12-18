import Dependencies.*
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*

lazy val root = (project in file("."))
  .settings(
    name := "saga",
    organization := "codedone",
    description := "saga",
    scalaVersion := "3.5.0",
    resolvers ++= Seq(Resolver.mavenLocal, Resolver.jcenterRepo),
    publishTo := Some(Resolver.mavenLocal),
    publishMavenStyle := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion
    ),
    libraryDependencies ++= Cats.deps ++ Specs.deps
  )
