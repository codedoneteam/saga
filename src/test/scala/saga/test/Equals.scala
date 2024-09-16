package saga.test

import cats.effect.IO
import munit.CatsEffectAssertions.assertIO
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers

trait Equals extends Matchers:
  extension[A] (expect: A)
    def ===(io: IO[A]): IO[Unit] = assertIO[A, A](io, expect)

object Equals extends Equals