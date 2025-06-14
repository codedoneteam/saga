package saga.std

import cats.{Applicative, ApplicativeThrow}
import cats.effect.{IO, Ref}
import cats.implicits.*
import munit.CatsEffectSuite
import saga.test.applied.AppliedAlg.given
import _root_.saga.test.applied.Expect.*
import saga.test.applied.*
import saga.types.*
import saga.std.RevertFinalSagaApplierSpec.*
import _root_.saga.test.error.TestError

class RevertFinalSagaApplierSpec extends CatsEffectSuite:
  test("Revert final saga") {
    Ref
      .of[IO, Int](0)
      .flatMap(
        ref =>
          FinalApplier[IO, Int, Long, Long, Expect]
            .apply(FinalComposer[IO, Int, Long, Long].compose(saga[IO](ref), apply2[IO]), 0)
            .flatMap(expect => ref.get.map(_ -> expect))
      )
      .assertEquals((-1, Fail(TestError)))
  }

object RevertFinalSagaApplierSpec:
  def saga[F[_] : Applicative](ref: Ref[F, Int]): Saga[F, Int, Long] = (apply1[F], compensate1[F](ref))

  def apply1[F[_] : Applicative]: Int => F[Long] = (x: Int) => (x + 1).toLong.pure[F]

  def compensate1[F[_] : Applicative](ref: Ref[F, Int]): Long => F[Int] = (_: Long) => ref.updateAndGet(_ - 1)

  def apply2[F[_] : ApplicativeThrow]: Long => F[Long] = (_: Long) => ApplicativeThrow[F].raiseError(TestError)