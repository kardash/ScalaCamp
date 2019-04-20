package camp.task_1

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration

class Task1 extends App {

  @tailrec
  final def retry[A](block: () => A,
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): A = {
    val a = block.apply()
    if (acceptResult.apply(a) || retries.isEmpty) return a
    Thread.sleep(retries.head.toSeconds)
    retry(block, acceptResult, retries.tail)
  }

}
