import models.{TodoService}
import org.scalatestplus.play._

class TodoModelSpec extends PlaySpec with OneAppPerSuite {

  var todoService: TodoService = app.injector.instanceOf(classOf[TodoService])

  import models._

  // -- Date helpers

  def dateIs(date: java.util.Date, str: String) = {
    new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  }

  // --

  "Todo model" should {

    "be retrieved by id" in {
      val javaone = todoService.findById(2).get
      javaone.text must equal("Java One Tokyo 2017")
      javaone.deadline.value must matchPattern {
        case date: java.util.Date if dateIs(date, "2017-05-17") =>
      }
    }

    "be listed along its todos" in {
      val todos = todoService.list()
      todos.total must equal(12)
      todos.items must have length (10)
    }

    "be updated if needed" in {
      todoService.update(2, Todo(text = "Java One Tokyo",
        deadline = None,
        createdAt = None,
        doneAt = None,
        categoryId = Some(1)))

      val javaone = todoService.findById(2).get
      javaone.text must equal("Java One Tokyo")
      javaone.deadline mustBe None
    }

  }

}
