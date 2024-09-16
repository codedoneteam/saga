package saga

import saga.syntax.*

trait extensions extends StepSyntax with SagaSyntax with FinalSagaSyntax

object extensions extends extensions