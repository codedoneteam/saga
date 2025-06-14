package saga.std

import cats.{Applicative, ApplicativeThrow}
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.test.applied.AppliedAlg.given
import saga.test.applied.*
import saga.types.Saga
import saga.std.ApplierErrorSpec.*
import _root_.saga.test.error.TestError
import _root_.saga.test.applied.Expect.*

class ApplierErrorSpec extends CatsEffectSuite:
  test("Error apply saga") {
    Applier[IO, Int, Long, Expect].apply(saga, 0).assertEquals(Fail(TestError))
  }

object ApplierErrorSpec:
  def saga[F[_] : ApplicativeThrow]: Saga[F, Int, Long] = (apply[F], compensate[F])

  def apply[F[_] : ApplicativeThrow]: Int => F[Long] = (_: Int) => ApplicativeThrow[F].raiseError(TestError)

  def compensate[F[_] : Applicative]: Long => F[Int] = (x: Long) => (x - 1).toInt.pure[F]
