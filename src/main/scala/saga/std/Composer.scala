package saga.std

import cats.implicits.*
import cats.{Monad, MonadThrow}
import saga.error.*
import saga.syntax.ComposeSyntax.*
import saga.types.Saga

trait Composer[F[_], A, B, C]:
  def compose(saga: Saga[F, A, B], saga2: Saga[F, B, C]): Saga[F, A, C]

object Composer:
  def apply[F[_], A, B, C](using composer: Composer[F, A, B, C]): Composer[F, A, B, C] = composer

  given makeComposer[F[_] : MonadThrow, A, B, C]: Composer[F, A, B, C] =
    (saga: (A => F[B], B => F[A]), saga2: (B => F[C], C => F[B])) =>
      (saga, saga2) match
        case ((forward1, compensate1), (forward2, compensate2)) => (forward(forward1, forward2)(compensate1), compensate(compensate1, compensate2))

  private[std] def forward[F[_] : MonadThrow, A, B, C](forward1: A => F[B], forward2: B => F[C])(compensate1: B => F[A]): A => F[C] =
    (a: A) =>
      for
        b <- forward1(a)
        c <- forward2(b).onError(
          failed => compensate1(b).onError(error => MonadThrow[F].raiseError(SagaError(failed, error))) *> MonadThrow[F].raiseError(failed)
        )
      yield c

  private[std] def compensate[F[_] : Monad, A, B, C](compensate1: B => F[A], compensate2: C => F[B]): C => F[A] = compensate2 >>+ compensate1
