package models

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

case class Todo(id: Option[Long] = None, text: String,
                deadline: Option[Date], createdAt: Option[Date], doneAt: Option[Date],
                categoryId: Option[Long])

@javax.inject.Singleton
class TodoService @Inject()(dbapi: DBApi, categoryService: CategoryService) {

  private val db = dbapi.database("default")

  // -- Parsers
  /**
    * Parse a Todo from a ResultSet
    */
  val simple = {
    get[Option[Long]]("todo.id") ~
      get[String]("todo.text") ~
      get[Option[Date]]("todo.deadline") ~
      get[Option[Date]]("todo.created_at") ~
      get[Option[Date]]("todo.done_at") ~
      get[Option[Long]]("todo.category_id") map {
      case id ~ text ~ deadline ~ createdAt ~ doneAt ~ categoryId =>
        Todo(id, text, deadline, createdAt, doneAt, categoryId)
    }
  }

  /**
    * Parse a (Todo,Category) from a ResultSet
    */
  val withCategory = simple ~ (categoryService.simple ?) map {
    case todo ~ category => (todo, category)
  }

  // -- Queries
  /**
    * Retrieve a todo from the id.
    */
  def findById(id: Long): Option[Todo] = {
    db.withConnection { implicit connection =>
      SQL("select * from todo where id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }

  /**
    * Return a page of (Todo,Category).
    *
    * @param page     Page to display
    * @param pageSize Number of computers per page
    * @param orderBy  Computer property used for sorting
    * @param filter   Filter applied on the name column
    */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): ListPage[(Todo, Option[Category])] = {

    val offest = pageSize * page

    db.withConnection { implicit connection =>

      val todos = SQL(
        """
          select * from todo
          left join category on todo.category_id = category.id
          where todo.text like {filter}
          order by {orderBy} nulls last, id
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize,
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(withCategory *)

      val totalRows = SQL(
        """
          select count(*) from todo
          left join category on todo.category_id = category.id
          where todo.text like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      ListPage(todos, page, offest, totalRows)
    }
  }

  /**
    * Update a todo.
    *
    * @param id   The todo id
    * @param todo The todo values.
    */
  def update(id: Long, todo: Todo) = {
    db.withConnection { implicit connection =>
      SQL(
        """
          update todo
          set text = {text}, deadline = {deadline}, category_id = {category_id}
          where id = {id}
        """
      ).on(
        'id -> id,
        'text -> todo.text,
        'deadline -> todo.deadline,
        'category_id -> todo.categoryId
      ).executeUpdate()
    }
  }

  /**
    * Insert a new todo.
    *
    * @param todo The todo values.
    */
  def insert(todo: Todo) = {
    db.withConnection { implicit connection =>
      SQL(
        """
          insert into todo values (
            (select next value for todo_seq),
            {text}, {deadline}, CURRENT_TIMESTAMP(), null, {category_id}
          )
        """
      ).on(
        'text -> todo.text,
        'deadline -> todo.deadline,
        'category_id -> todo.categoryId
      ).executeUpdate()
    }
  }

  /**
    * Delete a todo.
    *
    * @param id Id of the todo to delete.
    */
  def delete(id: Long) = {
    db.withConnection { implicit connection =>
      SQL("delete from todo where id = {id}").on('id -> id).executeUpdate()
    }
  }

  /**
    * Close a todo.
    *
    * @param id Id of the todo to close.
    */
  def close(id: Long) = {
    db.withConnection { implicit connection =>
      SQL("update todo set done_at = CURRENT_TIMESTAMP() where id = {id}")
        .on('id -> id).executeUpdate()
    }
  }

  /**
    * Open a todo.
    *
    * @param id Id of the todo to open.
    */
  def open(id: Long) = {
    db.withConnection { implicit connection =>
      SQL("update todo set done_at = NULL where id = {id}")
        .on('id -> id).executeUpdate()
    }
  }
}
