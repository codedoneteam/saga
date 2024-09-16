package saga.syntax

import saga.types.*

trait StepSyntax:
  extension[F[_], A, B] (step1: Step[F, A, B])
    def <+>[C](step2: Step[F, B, C]): Saga[F, A, B, C] =
      (step1, step2) match
        case ((apply1, compensate1), (apply2, compensate2)) => (apply1, apply2, compensate1, compensate2)

    def <=>[C](apply2: FinalStep[F, B, C]): FinalSaga[F, A, B, C] =
      step1 match
        case (apply1, compensate1) => (apply1, apply2, compensate1)

object StepSyntax extends StepSyntax
