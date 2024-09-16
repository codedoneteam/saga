package saga

object types:
  type Step[F[_], A, B] = (A => F[B], B => F[A])

  type FinalStep[F[_], A, B] = A => F[B]

  type Saga[F[_], A, B, C] = (A => F[B], B => F[C], B => F[A], C => F[B])

  type FinalSaga[F[_], A, B, C] = (A => F[B], B => F[C], B => F[A])