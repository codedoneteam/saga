package saga.test.unapplied

import saga.unapplied.UnApplied
import Expect.*

object UnAppliedAlg:
  given UnApplied[Int, Expect] with
    def success(x: Int): Expect = Success(x)

    def inconsistent(error: Throwable): Expect = UnApplyInconsistent(error)