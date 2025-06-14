package saga

import cats.implicits.*
import munit.CatsEffectSuite
import saga.all.*
import ApplySagaSpec.*
import cats.Applicative
import cats.effect.IO
import saga.test.applied.AppliedAlg.given
import _root_.saga.test.applied.Expect.*
import saga.types.Saga

class ApplySagaSpec extends CatsEffectSuite:
  test("Apply saga") {
    ((step1[IO] <+> step2[IO]) <<< 0).assertEquals(Success(2))
  }

object ApplySagaSpec:
  def step1[F[_] : Applicative]: Saga[F, Int, Long] = (apply1[F](_), compensate1[F](_))

  def step2[F[_] : Applicative]: Saga[F, Long, Long] = (apply2[F](_), compensate2[F](_))

  def apply1[F[_] : Applicative](x: Int): F[Long] = (x + 1).toLong.pure[F]

  def compensate1[F[_] : Applicative](x: Long): F[Int] = (x - 1).toInt.pure[F]

  def apply2[F[_] : Applicative](x: Long): F[Long] = (x * 2).pure[F]

  def compensate2[F[_] : Applicative](x: Long): F[Long] = (x / 2).pure[F]