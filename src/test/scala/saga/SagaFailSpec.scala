package saga

import cats.{Applicative, ApplicativeThrow}
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.SagaFailSpec.*
import saga.extensions.*
import saga.types.*
import saga.test.Equals.===
import saga.test.applied.AppliedAlg.given_Applied_Int_Expect
import saga.test.applied.Fail

class SagaFailSpec extends CatsEffectSuite:
  test("Two steps saga failed apply") {
    Fail(TestException) === (step1[IO] <+> step2[IO]) <<< 1
  }

  test("Final saga failed apply") {
    Fail(TestException) === (step1[IO] <=> finalStep[IO]) <<< 1
  }

  test("Three steps saga failed on first step") {
    Fail(TestException) === (step1[IO] <+> step2[IO] <+> step3[IO]) <<< 1
  }

  test("Three steps saga failed on last step") {
    Fail(TestException) === (step1[IO] <+> step3[IO] <+> step2[IO]) <<< 1
  }

  test("Three steps final saga failed") {
    Fail(TestException) === (step1[IO] <+> step3[IO] <=> finalStep[IO]) <<< 1
  }

object SagaFailSpec:
  object TestException extends RuntimeException

  def step1[F[_] : Applicative]: Step[F, Int, Int] = ((x: Int) => (x + 1).pure[F], (x: Int) => (x - 1).pure[F])

  def step2[F[_] : ApplicativeThrow]: Step[F, Int, Int] =
    ((_: Int) => ApplicativeThrow[F].raiseError[Int](TestException), (x: Int) => x.pure[F])

  def step3[F[_] : Applicative]: Step[F, Int, Int] = ((x: Int) => (x + 2).pure[F], (x: Int) => (x - 2).pure[F])

  def finalStep[F[_] : ApplicativeThrow]: FinalStep[F, Int, Int] = _ => ApplicativeThrow[F].raiseError[Int](TestException)