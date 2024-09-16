package saga

import cats.Applicative
import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import saga.extensions.*
import saga.types.*
import saga.test.Equals.===
import saga.applied.Applied
import saga.test.applied.AppliedAlg.given_Applied_Int_Expect
import SagaApplySpec.*
import saga.test.applied.{Expect, Success}

class SagaApplySpec extends CatsEffectSuite:
  test("Apply saga two steps") {
    Success(3) === (step1[IO] <+> step2[IO]) <<< 0
  }

  test("Apply final saga two steps") {
    Success(6) === (step1[IO] <+> step2[IO] <=> finalStep[IO]) <<< 0
  }

  test("Apply saga three steps") {
    Success(6) === (step1[IO] <+> step2[IO] <+> step3[IO]) <<< 0
  }

  test("Apply final saga three steps") {
    Success(12) === (step1[IO] <+> step2[IO] <+> step3[IO] <=> finalStep[IO]) <<< 0
  }

object SagaApplySpec:
  def step1[F[_] : Applicative]: Step[F, Int, Int] = (plus[F](_, 1), minus[F](_, 1))

  def step2[F[_] : Applicative]: Step[F, Int, Int] = (plus[F](_, 2), minus[F](_, 2))

  def step3[F[_] : Applicative]: Step[F, Int, Int] = (plus[F](_, 3), minus[F](_, 3))

  def finalStep[F[_] : Applicative](x: Int): F[Int] = (x * 2).pure[F]

  private def plus[F[_] : Applicative](x: Int, y: Int) = (x + y).pure[F]

  private def minus[F[_] : Applicative](x: Int, y: Int) = (x - y).pure[F]