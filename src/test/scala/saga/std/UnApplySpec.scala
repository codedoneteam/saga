package saga.std

import cats.Applicative
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.test.unapplied.UnAppliedAlg.given
import saga.types.Saga
import saga.std.UnApplySpec.*
import _root_.saga.test.unapplied.Expect.*
import _root_.saga.test.unapplied.Expect

class UnApplySpec extends CatsEffectSuite {
  test("UnApply saga") {
    UnApplier[IO, Int, Long, Expect].unApply(saga, 1).assertEquals(Success(0))
  }
}

object UnApplySpec {
  def saga[F[_] : Applicative]: Saga[F, Int, Long] = (apply[F], compensate[F])

  def apply[F[_] : Applicative]: Int => F[Long] = (x: Int) => (x + 1).toLong.pure[F]

  def compensate[F[_] : Applicative]: Long => F[Int] = (x: Long) => (x - 1).toInt.pure[F]
}
