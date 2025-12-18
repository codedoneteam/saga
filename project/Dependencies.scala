import sbt.*

object Dependencies {
  object Cats {
    private val catsVersion   = "2.12.0"
    private val effectVersion = "3.5.4"
    private val org           = "org.typelevel"

    private val core   = org %% "cats-core"   % catsVersion
    private val effect = org %% "cats-effect" % effectVersion

    val deps: Seq[ModuleID] = Seq(core, effect)
  }

  object Specs {
    private val scalaTest       = "org.scalatest" %% "scalatest"           % "3.2.14"
    private val munitCatsEffect = "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"

    val deps: Seq[ModuleID] = Seq(scalaTest, munitCatsEffect).map(_ % Test)
  }
}
