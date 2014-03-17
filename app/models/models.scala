package models

import java.util.UUID
import scala.collection.convert.WrapAsScala
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError
import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Row

case class Song(id: UUID, title: String, album: String, artist: String)

class SongsRepository(client: SimpleClient) {

  import Utils._

  def getAll(implicit ctxt: ExecutionContext): Future[List[Song]] = {

    import WrapAsScala.iterableAsScalaIterable

    client.getRows.toScalaFuture.map { rows =>
      rows.map(row => song(row)).toList
    }
  }

  private def song(row: Row): Song =
    Song(row.getUUID("id"), row.getString("title"), row.getString("album"), row.getString("artist"))

  def getById(id: UUID)(implicit ctxt: ExecutionContext): Future[Song] = {
    val stmt = new BoundStatement(client.session.prepare("SELECT * FROM simplex.songs WHERE id = ?;"))
    client.session.executeAsync(stmt.bind(id)).toScalaFuture.map(rs => song(rs.one))
  }

  def insert(title: String, album: String, artist: String)(implicit ctxt: ExecutionContext): Future[UUID] = {
    val stmt = new BoundStatement(client.session.prepare("INSERT INTO simplex.songs (id, title, album, artist) VALUES (?, ?, ?, ?);"))
    val id = UUIDs.timeBased
    client.session.executeAsync(stmt.bind(id, title, album, artist)).toScalaFuture.map(rs => id)
  }
}

object JsonFormats {

  /**
   * Deserializer for java.util.UUID, from latest play Reads (was added on 2014-03-01 to master,
   * see https://github.com/playframework/playframework/pull/2428)
   */
  private def uuidReader(checkUuuidValidity: Boolean = false): Reads[java.util.UUID] = new Reads[java.util.UUID] {
    import java.util.UUID
    import scala.util.Try
    def check(s: String)(u: UUID): Boolean = (u != null && s == u.toString())
    def parseUuid(s: String): Option[UUID] = {
      val uncheckedUuid = Try(UUID.fromString(s)).toOption

      if (checkUuuidValidity) {
        uncheckedUuid filter check(s)
      } else {
        uncheckedUuid
      }
    }

    def reads(json: JsValue) = json match {
      case JsString(s) => {
        parseUuid(s).map(JsSuccess(_)).getOrElse(JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.uuid")))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.uuid"))))
    }
  }

  private implicit val uuidReads: Reads[java.util.UUID] = uuidReader()
  private implicit val uuidWrites: Writes[UUID] = Writes { uuid => JsString(uuid.toString) }

  implicit val songFormat: Format[Song] = Json.format[Song]
  implicit val songDataReads = (
    (__ \ 'title).read[String] and
    (__ \ 'album).read[String] and
    (__ \ 'artist).read[String]) tupled
}