package pillar

import java.io.File

import com.streamsend.pillar.DataStore
import com.streamsend.pillar.Migrator
import com.streamsend.pillar.Registry

import play.api.Application

object Pillar {

  /**
   * Initialize and migrate the dataStore configured in application.conf.
   *
   * @param dataStoreName the name used in the application.conf after "pillar.", e.g. for
   * 		"pillar.faker { ... }" the dataStoreName is "faker".
   * @param app the app to read configuration from (for the mode the app is currently running).
   */
  def migrate(dataStoreName: String, app: Application): Unit = {
    val registry = Registry.fromDirectory(new File(getClass.getResource("/pillar/migrations").toURI))
    val dataStore = DataStore.fromConfiguration(dataStoreName, app.mode.toString.toLowerCase, app.configuration.underlying)
    val migrator = Migrator(registry, new LoggerReporter)
    migrator.initialize(dataStore)
    migrator.migrate(dataStore)
  }

}