package saga.syntax

import cats.MonadThrow
import saga.applied.Applied
import saga.std.{Applier, Composer, FinalComposer, UnApplier}
import saga.unapplied.UnApplied
import saga.types.*

trait SagaSyntax:
  extension [F[_] : MonadThrow, A, B](saga: Saga[F, A, B])
    infix def <+>[C](saga2: Saga[F, B, C]): Saga[F, A, C] = Composer[F, A, B, C].compose(saga, saga2)

    infix def <=>[C](f: B => F[C]): FinalSaga[F, A, B, C] = FinalComposer[F, A, B, C].compose(saga, f)

    infix def <<<[R](a: A)(using applied: Applied[B, R]): F[R] = Applier[F, A, B, R].apply(saga, a)

    infix def >>>[R](b: B)(using unApplied: UnApplied[A, R]): F[R] = UnApplier[F, A, B, R].unApply(saga, b)

object SagaSyntax extends SagaSyntax
