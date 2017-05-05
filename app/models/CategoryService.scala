package models

import javax.inject.Inject

import anorm.SqlParser._
import anorm._

import play.api.db.DBApi

case class Category(id: Option[Long] = None, name: String)

@javax.inject.Singleton
class CategoryService @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")

  /**
    * * Parse a Category from a ResultSet
    */
  val simple = {
    get[Option[Long]]("category.id") ~
      get[String]("category.name") map {
      case id ~ name => Category(id, name)
    }
  }

  /**
    * Construct the Map[String,String] needed to fill a select options set.
    */
  def options: Seq[(String, String)] = db.withConnection { implicit connection =>
    SQL("select * from category order by name").as(simple *).
      foldLeft[Seq[(String, String)]](Nil) { (cs, c) =>
      c.id.fold(cs) { id => cs :+ (id.toString -> c.name) }
    }
  }
}
