package saga.syntax

import cats.Monad
import cats.implicits.*

trait ComposeSyntax:
  extension[F[_] : Monad, A, B](fa: A => F[B])
    @inline def >>+[C](fb: B => F[C]): A => F[C] = fa(_).flatMap(fb)

object ComposeSyntax extends ComposeSyntax
