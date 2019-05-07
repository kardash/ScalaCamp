package camp

import cats.Monad

/**
  * Repository and Service implementation using tagless final pattern.
  * The idea is to make it easier to test our database layer, using Scala’s higher kinded types to abstract
  * the Future type constructor away from our traits under test.
  * Intro to tagless final: https://www.basementcrowd.com/2019/01/17/an-introduction-to-tagless-final-in-scala/.
  * The similar task example https://github.com/LvivScalaClub/cats-playground/blob/master/src/main/scala/BookRepository.scala
  */

case class User(id: Long, username: String)
case class IotDevice(id: Long, userId: Long, sn: String)

// NOTE: This import bring into the scope implicits that allow you to call .map and .flatMap on the type F[_]
// and also bring you typeclasses that know how to flatmap (Monad) and map (Functor) over your higher-kinded type.
import cats.implicits._

trait UserRepository[F[_]] {
  def registerUser(username: String): F[User]
  def getById(id: Long): F[Option[User]]
  def getByUsername(username: String): F[Option[User]]
}

trait IotDeviceRepository[F[_]] {
  def registerDevice(userId: Long, serialNumber: String): F[IotDevice]
  def getById(id: Long): F[Option[IotDevice]]
  def getBySn(sn: String): F[Option[IotDevice]]
  def getByUser(userId: Long): F[Seq[IotDevice]]
}

class UserService[F[_]](repository: UserRepository[F])
                       (implicit monad: Monad[F]) {

  def registerUser(username: String): F[Either[String, User]] = {
    // .flatMap syntax works because of import cats.implicits._
    // so flatMap function is added to F[_] through implicit conversions
    // The implicit monad param knows how to flatmap and map over your F.
    repository.getByUsername(username).flatMap({
      case Some(user) =>
        monad.pure(Left(s"User $user already exists"))
      case None =>
        // .map syntax works because of import cats.implicits._
        // so map function is added to F[_] through implicit conversions
        repository.registerUser(username).map(Right(_))
    })
  }

  def getByUsername(username: String): F[Option[String]] = ???
  def getById(id: Long): F[Option[String]] = ???
}

class IotDeviceService[F[_]](repository: IotDeviceRepository[F],
                             userRepository: UserRepository[F])
                            (implicit monad: Monad[F]) {

  // the register should fail with Left if the user doesn't exist or the sn already exists.
  def registerDevice(userId: Long, sn: String): F[Either[String, User]] = ???
}

// task1: implement in-memory Respository with Id monad.
// task2: implement in-memory Respository with Future monad
// example https://github.com/LvivScalaClub/cats-playground/blob/master/src/main/scala/BookRepository.scala

// task3: unit tests