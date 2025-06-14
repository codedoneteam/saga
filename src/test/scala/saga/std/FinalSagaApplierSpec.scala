package saga.std

import cats.Applicative
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.test.applied.AppliedAlg.given
import saga.test.applied.*
import saga.types.*
import saga.std.FinalSagaApplierSpec.*
import _root_.saga.test.applied.Expect.*

class FinalSagaApplierSpec extends CatsEffectSuite:
  test("Apply final saga") {
    FinalApplier[IO, Int, Long, Long, Expect]
      .apply(FinalComposer[IO, Int, Long, Long].compose(saga[IO], apply2[IO]), 0)
      .assertEquals(Success(2))
  }

object FinalSagaApplierSpec:
  def saga[F[_] : Applicative]: Saga[F, Int, Long] = (apply1[F], compensate1[F])

  def apply1[F[_] : Applicative]: Int => F[Long] = (x: Int) => (x + 1).toLong.pure[F]

  def compensate1[F[_] : Applicative]: Long => F[Int] = (x: Long) => (x - 1).toInt.pure[F]

  def apply2[F[_] : Applicative]: Long => F[Long] = (x: Long) => (x * 2).pure[F]
