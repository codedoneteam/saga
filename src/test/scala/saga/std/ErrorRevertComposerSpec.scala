package saga.std

import cats.effect.IO
import cats.effect.kernel.Ref
import cats.implicits.*
import cats.{Applicative, ApplicativeThrow}
import munit.CatsEffectSuite
import saga.std.ErrorRevertComposerSpec.*
import saga.test.applied.AppliedAlg.given
import saga.test.applied.*
import saga.test.error.TestError
import saga.types.Saga
import saga.test.applied.Expect.*

class ErrorRevertComposerSpec extends CatsEffectSuite:
  test("Error revert composed two steps saga") {
    Ref[IO]
      .of(0L)
      .flatMap(
        ref =>
          Applier[IO, Long, Long, Expect]
            .apply(Composer[IO, Long, Long, Long].compose(sagaError(ref), saga2), 1)
            .flatMap(expect => ref.get.map(_ -> expect))
      )
      .assertEquals((0L, Inconsistent(TestError, TestError)))
  }

  test("Error revert composed three steps saga") {
    Ref[IO]
      .of(1L)
      .flatMap(
        ref =>
          Applier[IO, Long, Long, Expect]
            .apply(Composer[IO, Long, Long, Long].compose(Composer[IO, Long, Long, Long].compose(sagaError(ref), saga1(ref)), saga2), 1)
            .flatMap(expect => ref.get.map(_ -> expect))
      )
      .assertEquals((-2L, Inconsistent(TestError, TestError)))
  }

object ErrorRevertComposerSpec:
  def saga1[F[_] : Applicative](ref: Ref[F, Long]): Saga[F, Long, Long] = (apply1[F](ref), compensate1(ref))

  def sagaError[F[_] : ApplicativeThrow](ref: Ref[F, Long]): Saga[F, Long, Long] = (apply11[F](ref), error[F])

  def saga2[F[_] : ApplicativeThrow]: Saga[F, Long, Long] = (error[F], compensate2[F])

  def apply1[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ + x)

  def compensate1[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ - 2 * x)

  def apply11[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ * x)

  def compensate11[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ / 2 * x)

  def error[F[_] : ApplicativeThrow]: Long => F[Long] = (_: Long) => ApplicativeThrow[F].raiseError(TestError)

  def compensate2[F[_] : Applicative]: Long => F[Long] = (x: Long) => (x / 2).pure[F]