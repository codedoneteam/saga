package saga

object types:
  type Apply[F[_], -A, B] = A => F[B]

  type Compensate[F[_], A, -B] = B => F[A]

  type Saga[F[_], A, B] = (A => F[B], B => F[A])

  type FinalSaga[F[_], A, B, C] = ((A => F[B], B => F[A]), Apply[F, B, C])
