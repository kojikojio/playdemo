package controllers

import javax.inject.Inject

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import views._

/**
  * Manage a database of todo
  */
class TodoController @Inject()(todoService: TodoService, categoryService: CategoryService,
                               val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  /**
    * This result directly redirect to the application home.
    */
  val Home = Redirect(routes.TodoController.list(0, 3, ""))

  /**
    * Describe the todo form (used in both edit and create screens).
    */
  val todoForm = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "text" -> nonEmptyText,
      "deadline" -> optional(date("yyyy-MM-dd")),
      "created_at" -> optional(date("yyyy-MM-dd")),
      "done_at" -> optional(date("yyyy-MM-dd")),
      "category" -> optional(longNumber)
    )(Todo.apply)(Todo.unapply)
  )

  // -- Actions
  /**
    * Handle default path requests, redirect to todos list
    */
  def index = Action {
    Home
  }

  /**
    * Display the paginated list of todos.
    *
    * @param page    Current page number (starts from 0)
    * @param orderBy Column to be sorted
    * @param filter  Filter applied on computer names
    */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.todo.list(
      todoService.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")),
      orderBy, filter
    ))
  }

  /**
    * Display the 'edit form' of a existing Todo.
    *
    * @param id Id of the todo to edit
    */
  def edit(id: Long) = Action {
    todoService.findById(id).map { todo =>
      Ok(html.todo.editForm(id, todoForm.fill(todo), categoryService.options))
    }.getOrElse(NotFound)
  }

  /**
    * Handle the 'edit form' submission
    *
    * @param id Id of the todo to edit
    */
  def update(id: Long) = Action { implicit request =>
    todoForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.todo.editForm(id, formWithErrors, categoryService.options)),
      todo => {
        todoService.update(id, todo)
        Home.flashing("success" -> "Todo %s has been updated".format(todo.text))
      }
    )
  }

  /**
    * Make a todo close
    *
    * @param id Id of the todo to update
    */
  def close(id: Long) = Action {
    todoService.close(id)
    Home.flashing("success" -> "Todo has been closed")
  }

  /**
    * Make a todo open
    *
    * @param id Id of the todo to update
    */
  def open(id: Long) = Action {
    todoService.open(id)
    Home.flashing("success" -> "Todo has been opened")
  }

  /**
    * Display the 'new todo form'.
    */
  def create = Action {
    Ok(html.todo.createForm(todoForm, categoryService.options))
  }

  /**
    * Handle the 'new todo form' submission.
    */
  def save = Action { implicit request =>
    todoForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.todo.createForm(formWithErrors, categoryService.options)),
      todo => {
        todoService.insert(todo)
        Home.flashing("success" -> "Todo %s has been created".format(todo.text))
      }
    )
  }

  /**
    * Handle todo deletion.
    */
  def delete(id: Long) = Action {
    todoService.delete(id)
    Home.flashing("success" -> "Todo has been deleted")
  }

}
