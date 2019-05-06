package camp.task_1

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

class Task1 extends App {

  final def retry[A](block: () => Future[A],
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): Future[A] = {
    block().flatMap(result => {
      if (acceptResult(result) || retries.isEmpty) {
        Future.successful(result)
      } else {
        Thread.sleep(retries.head.toMillis)
        retry(block, acceptResult, retries.tail)
      }
    })
  }
}
