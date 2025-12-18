package saga

import saga.syntax.*

trait all extends SagaSyntax with FinalSagaSyntax with ComposeSyntax

object all extends all
