package saga

import cats.implicits.*
import cats.{Applicative, Id}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import saga.FlatMapSpec.*
import saga.syntax.FlatMapSyntax.>>+

class FlatMapSpec extends AnyFunSuite with Matchers:
  test("Flat map") {
    def f = first[Id] >>+ second[Id]
    f(1) shouldBe 4
  }

object FlatMapSpec:
  def first[F[_] : Applicative]: Int => F[Int] = x => (x + 1).pure[F]

  def second[F[_] : Applicative]: Int => F[Int] = x => (x * 2).pure[F]