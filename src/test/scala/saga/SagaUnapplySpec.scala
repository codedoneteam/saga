package saga

import cats.{Applicative, ApplicativeThrow}
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.SagaUnapplySpec.*
import saga.extensions.*
import saga.types.*
import saga.test.Equals.===
import saga.test.unapplied.*
import saga.test.unapplied.UnAppliedAlg.given_UnApplied_Int_Expect

class SagaUnapplySpec extends CatsEffectSuite:
  test("Unapply two steps saga") {
    Success(0) === (step1[IO] <+> step2[IO]) >>> 2
  }

  test("Unapply three steps saga") {
    Success(0) === (step1[IO] <+> step2[IO] <+> step3[IO]) >>> 6
  }

  test("Exception on first step unapply two steps saga") {
    UnApplyInconsistent(TestException) === (step0[IO] <+> step1[IO]) >>> 0
  }

  test("Exception on second step unapply two steps saga") {
    UnApplyInconsistent(TestException) === (step1[IO] <+> step0[IO]) >>> 0
  }

  test("Exception on first step unapply three steps saga") {
    UnApplyInconsistent(TestException) === (step0[IO] <+> step1[IO] <+> step2[IO]) >>> 0
  }

  test("Exception on second step unapply three steps saga") {
    UnApplyInconsistent(TestException) === (step1[IO] <+> step0[IO] <+> step2[IO]) >>> 0
  }

  test("Exception on second step unapply three steps saga") {
    UnApplyInconsistent(TestException) === (step1[IO] <+> step2[IO] <+> step0[IO]) >>> 0
  }

object SagaUnapplySpec:
  object TestException extends RuntimeException

  private def step0[F[_] : ApplicativeThrow]: Step[F, Int, Int] = ((x: Int) => x.pure[F], (_: Int) => ApplicativeThrow[F].raiseError(TestException))

  private def step1[F[_] : Applicative]: Step[F, Int, Int] = ((x: Int) => (x + 1).pure[F], (x: Int) => (x - 1).pure[F])

  private def step2[F[_] : Applicative]: Step[F, Int, Int] = ((x: Int) => (x * 2).pure[F], (x: Int) => (x / 2).pure[F])

  private def step3[F[_] : Applicative]: Step[F, Int, Int] = ((x: Int) => (x * 3).pure[F], (x: Int) => (x / 3).pure[F])