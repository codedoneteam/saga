package saga.std

import cats.implicits.*
import cats.ApplicativeThrow
import saga.types.Saga
import saga.unapplied.UnApplied

trait UnApplier[F[_], A, B, R]:
  def unApply(saga: Saga[F, A, B], b: B): F[R]

object UnApplier:
  def apply[F[_], A, B, R](using unApplier: UnApplier[F, A, B, R]): UnApplier[F, A, B, R] = unApplier

  given [F[_] : ApplicativeThrow, A, B, R](using unApplier: UnApplied[A, R]): UnApplier[F, A, B, R] =
    (saga: (A => F[B], B => F[A]), b: B) =>
      saga match
        case (_, compensate) => compensate(b).map(unApplier.success).handleError(unApplier.inconsistent)
