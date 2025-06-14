package saga.std

import cats.Applicative
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.std.UnApplyComposerSpec.*
import saga.test.unapplied.UnAppliedAlg.given
import _root_.saga.test.unapplied.Expect.*
import saga.test.unapplied.*
import saga.types.Saga

class UnApplyComposerSpec extends CatsEffectSuite:
  test("UnApply composed saga") {
    UnApplier[IO, Int, Long, Expect]
      .unApply(Composer[IO, Int, Long, Long].compose(saga1, saga2), 4)
      .assertEquals(Success(1))
  }

object UnApplyComposerSpec:
  def saga1[F[_] : Applicative]: Saga[F, Int, Long] = (apply1[F], compensate1[F])

  def saga2[F[_] : Applicative]: Saga[F, Long, Long] = (apply2[F], compensate2[F])

  def apply1[F[_] : Applicative]: Int => F[Long] = (x: Int) => (x + 1).toLong.pure[F]

  def compensate1[F[_] : Applicative]: Long => F[Int] = (x: Long) => (x - 1).toInt.pure[F]

  def apply2[F[_] : Applicative]: Long => F[Long] = (x: Long) => (x * 2).pure[F]

  def compensate2[F[_] : Applicative]: Long => F[Long] = (x: Long) => (x / 2).pure[F]