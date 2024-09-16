package saga.unapplied

trait UnApplied[A, T]:
  def success(a: A): T

  def inconsistent(failed: Throwable): T