package saga.test.applied

sealed trait Expect

final case class Success(x: Int) extends Expect

final case class Fail(e: Throwable) extends Expect

final case class Inconsistent(failed: Throwable, compensateFail: Throwable) extends Expect
