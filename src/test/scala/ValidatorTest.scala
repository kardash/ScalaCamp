import camp.Person
import org.scalatest._
import camp.Validator._

class ValidatorTest extends FlatSpec with Matchers {

    it should "return positive result" in {
       333.validate shouldEqual Right(333)
    }
    it should "return exception msg" in {
      (-333).validate shouldEqual Left("isn't positive value")
    }

    it should "return fail msg" in {
      "".validate shouldEqual Left("value is empty")
    }
    it should "return result" in {
     "abc".validate shouldEqual Right("abc")
    }

    it should "if less return result" in {
     10 validate lessThan(20) shouldEqual Right(10)
    }
    it should "if not less return msg" in {
     10 validate lessThan(5) shouldEqual Left("isn't less than "+5)
    }

    it should "return valid value" in {
      val p = Person(name = "John", age = 25)
       p.validate shouldEqual Right(p)
      }
    it should "return msg about invalid value" in {
      val p = Person(name = "John", age = 120)
       p.validate shouldEqual Left("invalid value")
      }

}
