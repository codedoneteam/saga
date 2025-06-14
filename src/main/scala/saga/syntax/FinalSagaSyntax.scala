package saga.syntax

import cats.MonadThrow
import saga.types.*
import saga.applied.Applied
import saga.std.FinalApplier

trait FinalSagaSyntax:
  extension [F[_] : MonadThrow, A, B, C, R](finalSaga: FinalSaga[F, A, B, C])
    infix def <<=[T](a: A)(using applied: Applied[C, R]): F[R] = FinalApplier[F, A, B, C, R].apply(finalSaga, a)

object FinalSagaSyntax extends FinalSagaSyntax
