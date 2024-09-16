package saga.syntax

import cats.MonadThrow
import cats.implicits.*
import saga.types.*
import saga.applied.Applied

trait FinalSagaSyntax:
  extension[F[_] : MonadThrow, A, B, C] (finalSaga: FinalSaga[F, A, B, C])
    def <<<[T](a: A)(using applied: Applied[C, T]): F[T] =
      finalSaga match
        case (apply1, apply2, compensate1) =>
          for
            step1 <- apply1(a).attempt

            apply =
                (failed: Throwable, compensated: Either[Throwable, A]) =>
                  compensated.swap.toOption.fold(applied.fail(failed))(applied.inconsistent(failed, _))

            compensate = (failed: Throwable, b: B) => compensate1(b).attempt.map(apply(failed, _))

            step2 = (b: B) => apply2(b).map(applied.success).handleErrorWith(compensate(_, b))

            appliedSaga <- step1.fold(applied.fail(_).pure[F], step2)
          yield appliedSaga

object FinalSagaSyntax extends FinalSagaSyntax