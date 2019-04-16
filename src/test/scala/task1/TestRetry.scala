package task1

import camp.task_1.Task1
import org.scalatest.Matchers
import org.scalatest.FlatSpec

import scala.concurrent.duration._

class TestRetry extends FlatSpec with Matchers {
  val test = new Task1()

    it should "return retries result" in {
    test.retry[Int](
      block = () => 1 + 1,
      acceptResult = res => res % 2 == 0,
      retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
    ) shouldEqual 2
  }
   it should "return default result" in {
    test.retry[Int](
      block = () => 1 + 1,
      acceptResult = res => res % 2 == 0,
      retries = List.empty
    ) shouldEqual 2
  }
}
