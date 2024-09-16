package saga.applied

trait Applied[C, T]:
  def success(c: C): T

  def fail(e: Throwable): T

  def inconsistent(failed: Throwable, compensateFail: Throwable): T