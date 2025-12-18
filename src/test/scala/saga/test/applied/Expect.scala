package saga.test.applied

enum Expect:
  case Success(x: Long)

  case Fail(e: Throwable)

  case Inconsistent(failed: Throwable, compensateFail: Throwable)