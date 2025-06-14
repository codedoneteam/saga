## Saga

The Saga pattern uses a sequence of steps. Each of these steps has apply and compensate functions. 
If apply fails saga will perform compensating functions.

### Example

````
import cats.implicits.*
import munit.CatsEffectSuite
import saga.all.*
import ApplySagaSpec.*
import cats.Applicative
import cats.effect.IO
import saga.types.Saga

 
 def saga = step1[IO] <+> step2[IO] <=> finalStep[IO]
 saga <<< 0
    

 def step1[F[_] : Applicative]: Step[F, Int, Int] = (plus[F](_, 1), minus[F](_, 1))

 def step2[F[_] : Applicative]: Step[F, Int, Int] = (plus[F](_, 2), minus[F](_, 2))

 def finalStep[F[_] : Applicative](x: Int): F[Boolean] = (x > 0).pure[F]

 def plus[F[_] : Applicative](x: Int, y: Int) = (x + y).pure[F]

 def minus[F[_] : Applicative](x: Int, y: Int) = (x - y).pure[F]


 given Applied[Boolean, Either[Throwable, Boolean]] with
    def success(x: Boolean): Either[Throwable, Boolean] = x.asRight

    def fail(e: Throwable): Either[Throwable, Boolean] = e.asLeft

    def inconsistent(failed: Throwable, compensateFail: Throwable): Either[Throwable, Boolean] = Throwable(compensateFail.getMessage, failed).asLeft
````

See more examples in tests

### Publish snapshot library locally

sbt clean test publishLocal

### Release library

sbt release