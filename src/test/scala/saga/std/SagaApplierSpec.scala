package saga.std

import cats.implicits.*
import cats.Applicative
import cats.effect.IO
import munit.CatsEffectSuite
import saga.test.applied.AppliedAlg.given
import _root_.saga.test.applied.Expect.*
import saga.test.applied.*
import saga.types.Saga
import SagaApplierSpec.*

class SagaApplierSpec extends CatsEffectSuite:
  test("Apply saga") {
    Applier[IO, Int, Long, Expect].apply(saga, 0).assertEquals(Success(1))
  }

object SagaApplierSpec:
  def saga[F[_] : Applicative]: Saga[F, Int, Long] = (apply[F], compensate[F])

  def apply[F[_] : Applicative]: Int => F[Long] = (x: Int) => (x + 1).toLong.pure[F]

  def compensate[F[_] : Applicative]: Long => F[Int] = (x: Long) => (x - 1).toInt.pure[F]
