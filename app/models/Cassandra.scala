package models

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ResultSetFuture
import com.datastax.driver.core.Session
import scala.collection.JavaConversions._
import play.api.Logger

/**
 * Simple cassandra client, following the datastax documentation
 * (http://www.datastax.com/documentation/developer/java-driver/2.0/java-driver/quick_start/qsSimpleClientCreate_t.html).
 */
class SimpleClient {

  var cluster: Cluster = _
  var session: Session = _

  def connect(node: String) {
    cluster = Cluster.builder()
      .addContactPoint(node)
      .build()
    val metadata = cluster.getMetadata()
    Logger.info(s"Connected to cluster: ${metadata.getClusterName}")
    for (host <- metadata.getAllHosts()) {
      Logger.info(s"Datatacenter: ${host.getDatacenter()}; Host: ${host.getAddress()}; Rack: ${host.getRack()}")
    }
    session = cluster.connect()
  }

  def createSchema(): Unit = {
    session.execute("CREATE KEYSPACE simplex WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};")

    //Execute statements to create two new tables, songs and playlists. Add to the createSchema method:
    session.execute(
      """CREATE TABLE simplex.songs (
        id uuid PRIMARY KEY,
        title text,
        album text,
        artist text,
        tags set<text>,
        data blob
        );""")
    session.execute(
      """CREATE TABLE simplex.playlists (
        id uuid,
        title text,
        album text, 
        artist text,
        song_id uuid,
        PRIMARY KEY (id, title, album, artist)
        );""")
  }

  def loadData() = {
    session.execute(
      """INSERT INTO simplex.songs (id, title, album, artist, tags) 
      VALUES (
          756716f7-2e54-4715-9f00-91dcbea6cf50,
          'La Petite Tonkinoise',
          'Bye Bye Blackbird',
          'Joséphine Baker',
          {'jazz', '2013'})
          ;""");
    session.execute(
      """INSERT INTO simplex.playlists (id, song_id, title, album, artist) 
      VALUES (
          2cc9ccb7-6221-4ccb-8387-f22b6a1b354d,
          756716f7-2e54-4715-9f00-91dcbea6cf50,
          'La Petite Tonkinoise',
          'Bye Bye Blackbird',
          'Joséphine Baker'
          );""");
  }

  def querySchema() = {
    val results = session.execute("SELECT * FROM simplex.playlists WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;")
    println(String.format("%-30s\t%-20s\t%-20s\n%s", "title", "album", "artist",
      "-------------------------------+-----------------------+--------------------"))
    for (row <- results) {
      println(String.format("%-30s\t%-20s\t%-20s", row.getString("title"),
        row.getString("album"), row.getString("artist")));
    }
  }

  def dropSchema() = {
    session.execute("DROP KEYSPACE simplex")
  }

  def getRows: ResultSetFuture = {
    val query = QueryBuilder.select().all().from("simplex", "songs")
    session.executeAsync(query)
  }

  def close() {
    session.close
    cluster.close
  }

}

object Cassandra extends App {
  val client = new SimpleClient()
  client.connect("192.168.33.11")
  client.createSchema
  client.loadData
  client.querySchema
  client.dropSchema
  client.close
}