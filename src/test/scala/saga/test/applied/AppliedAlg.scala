package saga.test.applied

import saga.applied.Applied
import saga.test.applied.{Expect, Fail, Inconsistent, Success}

trait AppliedAlg:
  given Applied[Int, Expect] with
    def success(x: Int): Expect = Success(x)

    def fail(e: Throwable): Expect = Fail(e)

    def inconsistent(failed: Throwable, compensateFail: Throwable): Expect = Inconsistent(failed, compensateFail)


object AppliedAlg extends AppliedAlg