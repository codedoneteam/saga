package saga.std

import cats.implicits.*
import cats.{ApplicativeThrow, MonadThrow}
import saga.applied.Applied
import saga.error.*
import saga.types.Saga

trait Applier[F[_], A, B, R]:
  def apply(saga: Saga[F, A, B], a: A): F[R]

object Applier:
  def apply[F[_], A, B, R](using applier: Applier[F, A, B, R]): Applier[F, A, B, R] = applier

  given [F[_] : ApplicativeThrow, A, B, R](using applied: Applied[B, R]): Applier[F, A, B, R] =
    (saga: (A => F[B], B => F[A]), a: A) =>
      saga match
        case (forward, _) =>
          forward(a).map(applied.success).handleError {
            case SagaError(failed, compensationFail) => applied.inconsistent(failed, compensationFail)
            case e                                   => applied.fail(e)
      }
