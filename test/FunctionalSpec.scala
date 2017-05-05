
import controllers.TodoController
import org.scalatest.concurrent.ScalaFutures
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class FunctionalSpec extends PlaySpec with OneAppPerSuite with ScalaFutures {

  def dateIs(date: java.util.Date, str: String) = {
    new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  }

  def todoController = app.injector.instanceOf(classOf[TodoController])

  "TodoController" should {

    "redirect to the todo list on /" in {
      val result = todoController.index(FakeRequest())

      status(result) must equal(SEE_OTHER)
      redirectLocation(result) mustBe Some("/todos")
    }

    "list todos on the the first page" in {
      val result = todoController.list(0, 3, "")(FakeRequest())

      status(result) must equal(OK)
      contentAsString(result) must include("12 todos found")
    }

    "filter todo by name" in {
      val result = todoController.list(0, 3, "日")(FakeRequest())

      status(result) must equal(OK)
      contentAsString(result) must include("9 todos found")
    }

    //running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

    "create new todo" in {
      val badResult = todoController.save(FakeRequest())

      status(badResult) must equal(BAD_REQUEST)

      val badDateFormat = todoController.save(
        FakeRequest().withFormUrlEncodedBody("text" -> "FooBar", "deadline" -> "badbadbad", "category" -> "1")
      )

      status(badDateFormat) must equal(BAD_REQUEST)
      contentAsString(badDateFormat) must include("""<option value="1" selected >Find jobs</option>""")
      contentAsString(badDateFormat) must include("""<input type="date" id="deadline" name="deadline" value="badbadbad" """)
      contentAsString(badDateFormat) must include("""<input type="text" id="text" name="text" value="FooBar" """)


      val result = todoController.save(
        FakeRequest().withFormUrlEncodedBody("text" -> "FooBar", "deadline" -> "2011-12-24", "category" -> "1")
      )

      status(result) must equal(SEE_OTHER)
      redirectLocation(result) mustBe Some("/todos")
      flash(result).get("success") mustBe Some("Todo FooBar has been created")

      val list = todoController.list(0, 3, "FooBar")(FakeRequest())

      status(list) must equal(OK)
      contentAsString(list) must include("One todo found")
    }
  }
}