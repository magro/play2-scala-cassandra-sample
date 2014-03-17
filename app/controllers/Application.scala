package controllers

import play.api._
import play.api.mvc._
import models.SimpleClient
import models.SongsRepository
import play.api.libs.json.Json
import com.datastax.driver.core.utils.UUIDs
import models.Song
import play.api.libs.json.JsError
import scala.concurrent.Future
import java.util.UUID
import scala.util.Try

class Application(songsRepo: SongsRepository) extends Controller {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import models.JsonFormats._

  def index = Action.async {
    songsRepo.getAll.map(songs => Ok(Json.toJson(songs)))
  }

  def createSong = Action.async(parse.json) { implicit request =>
    // Json Format defined in models.JsonFormats.songDataReads
    request.body.validate[(String, String, String)].map {
      case (title, album, artist) => {
        songsRepo.insert(title, album, artist).map( id =>
          Created.withHeaders("Location" -> routes.Application.songById(id.toString).absoluteURL(false))
        )
      }
    }.recoverTotal {
      e => Future.successful(BadRequest("Detected error:" + JsError.toFlatJson(e)))
    }
  }

  def songById(id: String) = Action.async {
    songsRepo.getById(UUID.fromString(id)).map(song => Ok(Json.toJson(song)))
  }

}