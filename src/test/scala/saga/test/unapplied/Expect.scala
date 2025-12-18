package saga.test.unapplied

enum Expect:
 case Success(x: Long)
 
 case  UnApplyInconsistent(e: Throwable)
