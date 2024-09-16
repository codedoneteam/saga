package saga

import cats.{Applicative, ApplicativeThrow}
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.SagaInconsistentStateSpec.*
import saga.extensions.*
import saga.types.*
import saga.test.Equals.===
import saga.test.applied.AppliedAlg.given_Applied_Int_Expect
import saga.test.applied.Inconsistent

class SagaInconsistentStateSpec extends CatsEffectSuite:
  test("Two steps saga inconsistent result in first step") {
    Inconsistent(ApplyException, CompensateException) === (step1[IO] <+> step2[IO]) <<< 0
  }

  test("Final saga inconsistent result in first step") {
    Inconsistent(ApplyException, CompensateException) === (step1[IO] <=> finalStep[IO]) <<< 0
  }

  test("Three steps saga inconsistent result in first step") {
    Inconsistent(ApplyException, CompensateException) === (step1[IO] <+> step0[IO] <+> step2[IO]) <<< 0
  }

  test("Three steps saga inconsistent result in second step") {
    Inconsistent(ApplyException, CompensateException) === (step0[IO] <+> step1[IO] <+> step2[IO]) <<< 0
  }

  test("Three steps final saga inconsistent result in second step") {
    Inconsistent(ApplyException, CompensateException) === (step0[IO] <+> step1[IO] <=> finalStep[IO]) <<< 0
  }

object SagaInconsistentStateSpec:
  object ApplyException extends RuntimeException

  object CompensateException extends RuntimeException

  def step0[F[_] : Applicative]: Step[F, Int, Int] = ((x: Int) => (x + 1).pure[F], (x: Int) => (x - 1).pure[F])

  def step1[F[_] : ApplicativeThrow]: Step[F, Int, Int] =
    ((x: Int) => (x + 1).pure[F], (_: Int) => ApplicativeThrow[F].raiseError[Int](CompensateException))

  def step2[F[_] : ApplicativeThrow]: Step[F, Int, Int] =
    ((_: Int) => ApplicativeThrow[F].raiseError[Int](ApplyException), (x: Int) => (x + 2).pure[F])

  def finalStep[F[_] : ApplicativeThrow]: FinalStep[F, Int, Int] = _ => ApplicativeThrow[F].raiseError[Int](ApplyException)