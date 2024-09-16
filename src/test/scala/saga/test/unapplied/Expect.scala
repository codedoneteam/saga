package saga.test.unapplied

sealed trait Expect

final case class Success(x: Int) extends Expect

final case class UnApplyInconsistent(e: Throwable) extends Expect
