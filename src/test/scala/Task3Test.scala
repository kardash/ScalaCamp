import cats.instances.future._
import camp._
import cats.Id
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.higherKinds
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class Task3Test extends FlatSpec with Matchers with ScalaFutures{

  val userRep = new UserRepositoryInMemory
  val userService = new UserService[Id](userRep)

  val deviceRep = new IotDeviceRepositoryInMemory
  val deviceService = new IotDeviceService[Id](repository = deviceRep, userRepository  = userRep)

    it should "return valid result for in memory repositories" in {

      userService.registerUser("user1")
        .map(userDb=>
          userDb.username shouldEqual "user1"
      )
      userService.getByUsername("user1").get shouldEqual User(1,"user1")
      userService.registerUser("user1") shouldBe Left("User User(1,user1) already exists")
      deviceService.registerDevice(1,"dd")
        .map( d =>
          (d.sn, d.userId) shouldEqual ("dd",1)
        )
      deviceService.registerDevice(1,"dd") shouldBe Left("Device IotDevice(1,1,dd) already exists")
      deviceService.registerDevice(2,"dd") shouldBe Left("User with id 2  doesn't exists")

    }

  val userRepF = new UserRepositoryFuture
  val userServiceF = new UserService[Future](userRepF)

  val deviceRepF = new IotDeviceRepositoryFuture
  val deviceServiceF = new IotDeviceService[Future](repository = deviceRepF, userRepository  = userRepF)
  def await[T](f: Future[T]) = Await.result(f, 2.seconds)

  it should "return valid result for future repositories" in {
      await(userServiceF.registerUser("user1")) shouldBe Right(User(1,"user1"))
      await(userServiceF.registerUser("user1")) shouldBe Left("User User(1,user1) already exists")
      await(deviceServiceF.registerDevice(1,"dd")) shouldBe Right(IotDevice(1,1,"dd"))
      await(deviceServiceF.registerDevice(1,"dd")) shouldBe Left("Device IotDevice(1,1,dd) already exists")
      await(deviceServiceF.registerDevice(2,"dd")) shouldBe Left("User with id 2  doesn't exists")
    }
}
