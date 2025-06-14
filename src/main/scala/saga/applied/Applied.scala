package saga.applied

trait Applied[T, R]:
  def success(b: T): R

  def fail(e: Throwable): R

  def inconsistent(failed: Throwable, compensationFail: Throwable): R