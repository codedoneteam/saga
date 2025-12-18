package saga.error

final case class SagaError(failed: Throwable, compensationFail: Throwable) extends Throwable
