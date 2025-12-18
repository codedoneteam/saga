package saga.std

import cats.{Applicative, ApplicativeThrow}
import cats.effect.IO
import cats.effect.kernel.Ref
import cats.implicits.*
import munit.CatsEffectSuite
import saga.std.RevertComposerSpec.*
import saga.test.applied.AppliedAlg.given
import _root_.saga.test.applied.Expect.*
import saga.test.applied.*
import saga.test.error.TestError
import saga.types.Saga

class RevertComposerSpec extends CatsEffectSuite:
  test("Revert composed two steps saga") {
    Ref[IO]
      .of(0L)
      .flatMap(
        ref =>
          Applier[IO, Long, Long, Expect]
            .apply(Composer[IO, Long, Long, Long].compose(saga1(ref), sagaError), 1)
            .flatMap(expect => ref.get.map(_ -> expect))
      )
      .assertEquals((-1L, Fail(TestError)))
  }

  test("Revert composed three steps saga") {
    Ref[IO]
      .of(1L)
      .flatMap(
        ref =>
          Applier[IO, Long, Long, Expect]
            .apply(Composer[IO, Long, Long, Long].compose(Composer[IO, Long, Long, Long].compose(saga1(ref), saga2(ref)), sagaError), 1)
            .flatMap(expect => ref.get.map(_ -> expect))
      )
      .assertEquals((-8L, Fail(TestError)))
  }

object RevertComposerSpec:
  def saga1[F[_] : Applicative](ref: Ref[F, Long]): Saga[F, Long, Long] = (apply1[F](ref), compensate1[F](ref))

  def saga2[F[_] : Applicative](ref: Ref[F, Long]): Saga[F, Long, Long] = (apply11[F](ref), compensate11[F](ref))

  def sagaError[F[_] : ApplicativeThrow]: Saga[F, Long, Long] = (error[F], compensate2[F])

  def apply1[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ + x)

  def compensate1[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ - 2 * x)

  def apply11[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ * x)

  def compensate11[F[_] : Applicative](ref: Ref[F, Long]): Long => F[Long] = (x: Long) => ref.updateAndGet(_ / 2 * x)

  def error[F[_] : ApplicativeThrow]: Long => F[Long] = (_: Long) => ApplicativeThrow[F].raiseError(TestError)

  def compensate2[F[_] : Applicative]: Long => F[Long] = (x: Long) => (x / 2).pure[F]