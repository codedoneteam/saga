package saga.std

import cats.MonadThrow
import saga.types.*

trait FinalComposer[F[_], A, B, C]:
  def compose(saga: Saga[F, A, B], forward: Apply[F, B, C]): FinalSaga[F, A, B, C]

object FinalComposer:
  def apply[F[_], A, B, C](using finalComposer: FinalComposer[F, A, B, C]): FinalComposer[F, A, B, C] = finalComposer

  given makeFinalComposer[F[_] : MonadThrow, A, B, C]: FinalComposer[F, A, B, C] =
    (saga: (Apply[F, A, B], Compensate[F, A, B]), forward: Apply[F, B, C]) => saga -> forward
