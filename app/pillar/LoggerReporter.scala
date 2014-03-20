package pillar

import java.util.Date

import com.streamsend.pillar.DataStore
import com.streamsend.pillar.Migration
import com.streamsend.pillar.ReplicationOptions
import com.streamsend.pillar.Reporter

import play.api.Logger

class LoggerReporter extends Reporter {
  def initializing(dataStore: DataStore, replicationOptions: ReplicationOptions) {
    Logger.info(s"Initializing ${dataStore.name} data store")
  }

  def migrating(dataStore: DataStore, dateRestriction: Option[Date]) {
    Logger.info(s"Migrating ${dataStore.name} data store")
  }

  def applying(migration: Migration) {
    Logger.info(s"Applying ${migration.authoredAt.getTime}: ${migration.description}")
  }

  def reversing(migration: Migration) {
    Logger.info(s"Reversing ${migration.authoredAt.getTime}: ${migration.description}")
  }

  def destroying(dataStore: DataStore) {
    Logger.info(s"Destroying ${dataStore.name} data store")
  }
}