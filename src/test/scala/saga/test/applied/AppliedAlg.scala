package saga.test.applied

import saga.applied.Applied
import Expect.*

object AppliedAlg:
  given Applied[Long, Expect] with
    def success(x: Long): Expect = Success(x)

    def fail(e: Throwable): Expect = Fail(e)

    def inconsistent(failed: Throwable, compensateFail: Throwable): Expect = Inconsistent(failed, compensateFail)