package task1

import camp.task_1.Task1
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class TestRetry extends FlatSpec with Matchers with ScalaFutures {
  val test = new Task1()

    it should "return retries result without wait" in {
      val start = System.currentTimeMillis()/1000
      whenReady(test.retry[Int](
        block = () => Future {1 + 1},
        acceptResult = res => res % 2 == 0,
        retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)),
        timeout(1 second)) { result =>
          result shouldEqual 2
        }
      val end = System.currentTimeMillis()/1000
      val duration = end - start
      duration shouldEqual 0
  }

   it should "return default result" in {
    whenReady(test.retry[Int](
      block = () => Future {1 + 1},
      acceptResult = res => res % 2 == 0,
      retries = List.empty),
      timeout(1 second)){result =>
      result shouldEqual 2
    }
  }

    it should "wait 3 seconds" in {
      val start = System.currentTimeMillis()/1000
        whenReady(test.retry[Int](
          block = () => Future { 1 + 1 },
          acceptResult = res => false,
          retries = List[FiniteDuration](0.seconds, 1.seconds, 2.seconds)
        ), timeout(5 seconds)){res =>
        res shouldEqual 2
      }
      val end = System.currentTimeMillis()/1000
      val duration = end - start
      duration shouldEqual 3
    }
}
