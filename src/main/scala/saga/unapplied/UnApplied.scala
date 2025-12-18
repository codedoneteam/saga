package saga.unapplied

trait UnApplied[T, R]:
  def success(a: T): R

  def inconsistent(failed: Throwable): R
