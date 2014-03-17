import play.api.GlobalSettings
import play.api.Application
import models.SimpleClient
import models.SongsRepository

object Global extends GlobalSettings {
  
  private var cassandra: SimpleClient = _
  private var controller: controllers.Application = _
  
  override def onStart(app: Application) {
    cassandra = new SimpleClient(app.configuration.getString("cassandra.node").getOrElse(throw new IllegalArgumentException("No 'cassandra.node' config found.")))
    cassandra.createSchema
    if(cassandra.countFrom("songs") == 0)
      cassandra.loadData

    controller = new controllers.Application(new SongsRepository(cassandra))
  }

  override def getControllerInstance[A](clazz: Class[A]): A = {
    // as simple as possible, nothing else needed for now...
    if(clazz == classOf[controllers.Application])
      controller.asInstanceOf[A]
    else
      throw new IllegalArgumentException(s"Controller of class $clazz not yet supported")
  }
  
  override def onStop(app: Application) {
    cassandra.close
  }

}