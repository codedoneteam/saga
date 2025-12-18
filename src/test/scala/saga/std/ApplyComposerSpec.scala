package saga.std

import cats.Applicative
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.test.applied.AppliedAlg.given
import saga.test.applied.*
import saga.types.Saga
import ApplyComposerSpec.*
import saga.test.applied.Expect.*

class ApplyComposerSpec extends CatsEffectSuite:
  test("Apply composed saga") {
    Applier[IO, Int, Long, Expect]
      .apply(Composer[IO, Int, Long, Long].compose(saga1, saga2), 0)
      .assertEquals(Success(2))
  }

object ApplyComposerSpec:
  def saga1[F[_] : Applicative]: Saga[F, Int, Long] = (apply1[F], compensate1[F])

  def saga2[F[_] : Applicative]: Saga[F, Long, Long] = (apply2[F], compensate2[F])

  def apply1[F[_] : Applicative]: Int => F[Long] = (x: Int) => (x + 1).toLong.pure[F]

  def compensate1[F[_] : Applicative]: Long => F[Int] = (x: Long) => (x - 1).toInt.pure[F]

  def apply2[F[_] : Applicative]: Long => F[Long] = (x: Long) => (x * 2).pure[F]

  def compensate2[F[_] : Applicative]: Long => F[Long] = (x: Long) => (x / 2).pure[F]
