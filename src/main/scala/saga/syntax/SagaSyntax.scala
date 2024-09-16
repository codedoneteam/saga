package saga.syntax

import cats.MonadThrow
import cats.implicits.*
import saga.syntax.FlatMapSyntax.>>+
import saga.syntax.FinalSagaSyntax.{<<< => apply}
import saga.applied.Applied
import saga.unapplied.UnApplied
import saga.types.*

trait SagaSyntax:
  extension[F[_] : MonadThrow, A, B, C] (saga: Saga[F, A, B, C])
    def <<<[T](a: A)(using applied: Applied[C, T]): F[T] =
      saga match
        case (apply1, apply2, compensate1, _) => apply((apply1, apply2, compensate1))(a)

    def >>>[T](c: C)(using unApplied: UnApplied[A, T]): F[T] =
      saga match
        case (_, _, compensate1, compensate2) =>
          compensate2(c).flatMap(compensate1).attempt.map(_.fold(unApplied.inconsistent, unApplied.success))

    def <+>[D](step: Step[F, C, D]): Saga[F, A, C, D] =
      (saga, step) match
        case ((apply1, apply2, compensate1, compensate2), (stepApply, stepCompensate)) =>
          (apply1 >>+ apply2, stepApply, compensate2 >>+ compensate1, stepCompensate)

    def <=>[D](apply3: FinalStep[F, C, D]): FinalSaga[F, A, C, D] =
      saga match
        case (apply1, apply2, compensate1, compensate2) =>
          (apply1 >>+ apply2, apply3, compensate2 >>+ compensate1)

object SagaSyntax extends SagaSyntax
