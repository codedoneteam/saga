package saga

import cats.{Applicative, ApplicativeThrow, Monad, MonadThrow}
import cats.effect.{IO, Ref}
import cats.implicits.*
import munit.CatsEffectSuite
import saga.SagaCompensateSpec.*
import saga.extensions.*
import saga.types.*
import saga.test.applied.AppliedAlg.given_Applied_Int_Expect
import saga.test.applied.Expect

class SagaCompensateSpec extends CatsEffectSuite:
  test("Saga first step compensate effect") {
    Ref
      .of[IO, Int](0)
      .flatMap(ref => applied(saga1(ref)) *> ref.get)
      .assertEquals(1)
  }

  test("Final saga first step compensate effect") {
    Ref
      .of[IO, Int](0)
      .flatMap(ref => applied(saga3(ref)) *> ref.get)
      .assertEquals(1)
  }

  test("Saga second step compensates effect") {
    (Ref.of[IO, Int](0), Ref.of[IO, Boolean](false)).tupled
      .flatMap { case (ref, ref2) => applied(saga2(ref, ref2)) *> result(ref, ref2) }
      .assertEquals(true -> -11)
  }

  test("Final saga second step compensate effect") {
    (Ref.of[IO, Int](0), Ref.of[IO, Boolean](false)).tupled
      .flatMap { case (ref, ref2) => applied(saga4(ref, ref2)) *> result(ref, ref2) }
      .assertEquals(true -> -11)
  }

object SagaCompensateSpec:
  object TestSagaException extends RuntimeException

  def applied[F[_] : MonadThrow](saga: Saga[F, Int, Int, Int]): F[Expect] = saga <<< 1

  def applied[F[_] : MonadThrow](saga: FinalSaga[F, Int, Int, Int]): F[Expect] = saga <<< 1

  def step1[F[_]](ref: Ref[F, Int]): Step[F, Int, Int] =
    ((x: Int) => ref.getAndUpdate(_ => x), (x: Int) => ref.updateAndGet(y => y - x))

  def step2[F[_] : Applicative](ref: Ref[F, Int], ref2: Ref[F, Boolean]): Step[F, Int, Int] =
    ((x: Int) => ref.getAndUpdate(_ => x + 1), (_: Int) => ref2.update(_ => true) *> ref.updateAndGet(_ => -1).as(10))

  def step3[F[_] : ApplicativeThrow]: Step[F, Int, Int] =
    ((_: Int) => ApplicativeThrow[F].raiseError[Int](TestSagaException), (x: Int) => x.pure[F])

  def saga1[F[_] : ApplicativeThrow](ref: Ref[F, Int]): Saga[F, Int, Int, Int] = step1[F](ref) <+> step3[F]

  def saga2[F[_] : MonadThrow](ref: Ref[F, Int], ref2: Ref[F, Boolean]): Saga[F, Int, Int, Int] =
    step1[F](ref) <+> step2[F](ref, ref2) <+> step3[F]

  def saga3[F[_] : MonadThrow](ref: Ref[F, Int]): FinalSaga[F, Int, Int, Int] =
    step1[F](ref) <=> finalStep[F]

  def saga4[F[_] : MonadThrow](ref: Ref[F, Int], ref2: Ref[F, Boolean]): FinalSaga[F, Int, Int, Int] =
    step1[F](ref) <+> step2[F](ref, ref2) <=> finalStep[F]

  def result[F[_] : Monad](ref: Ref[F, Int], ref2: Ref[F, Boolean]): F[(Boolean, Int)] = ref.get.flatMap(x => ref2.get.map(_ -> x))

  def finalStep[F[_] : ApplicativeThrow]: FinalStep[F, Int, Int] = _ => ApplicativeThrow[F].raiseError[Int](TestSagaException)