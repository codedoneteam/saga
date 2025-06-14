package saga.std

import cats.implicits.*
import cats.MonadThrow
import saga.applied.Applied
import saga.types.*

trait FinalApplier[F[_], A, B, C, R]:
  def apply(finalSaga: FinalSaga[F, A, B, C], a: A): F[R]

object FinalApplier:
  def apply[F[_], A, B, C, R](using finalApplier: FinalApplier[F, A, B, C, R]): FinalApplier[F, A, B, C, R] = finalApplier

  given makeFinalApplier[F[_] : MonadThrow, A, B, C, R](using applied: Applied[C, R]): FinalApplier[F, A, B, C, R] =
    (finalSaga: FinalSaga[F, A, B, C], a: A) =>
      finalSaga match
        case ((forward1, compensate1), forward2) =>
          for
            b <- forward1(a)
            r <- forward2(b)
              .map(applied.success)
              .handleErrorWith(
                error => compensate1(b).map(_ => applied.fail(error)).handleError(fail => applied.inconsistent(error, fail))
              )
          yield r
