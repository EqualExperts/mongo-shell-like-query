package com.ee.dynamicmongoquery

import com.mongodb.casbah.{MongoConnection, MongoCollection}
import com.mongodb.util.JSON
import com.mongodb.{BasicDBList, BasicDBObject}
import org.specs2.mutable.{BeforeAfter, Specification}
import org.specs2.matcher.MatchResult


class IntegrationTest extends Specification {

  lazy val mongoConn = MongoConnection()
  lazy val mongoDB = mongoConn("dynamic-query-test-db")
  lazy val userCollection: MongoCollection = mongoDB("users")

  lazy val parser: MongoQueryParser = new MongoQueryParser()

  implicit val myContext = new BeforeAfter {

    def clearCollections {
      userCollection.drop()
    }

    def populateData {
      val data = readJsonFile("data")
      JSON.parse(data).asInstanceOf[BasicDBList].toArray.toList.foreach(obj => userCollection.insert(obj.asInstanceOf[BasicDBObject]))
    }

    def before = {
      clearCollections
      populateData
    }

    def after = {
      clearCollections
    }
  }


  def readJsonFile(config: String): String = {
    val file: String = scala.io.Source.fromInputStream(getClass.getResourceAsStream(config + ".json")).mkString
    file
  }


  def getMongoQuery(config: String): String = {
    val configuration = JSON.parse(readJsonFile(config)).asInstanceOf[BasicDBObject]
    var mongoQueryString: String = configuration.get("query").asInstanceOf[String]
    mongoQueryString
  }

  def noParameters(): collection.immutable.Map[String, String] = {
    Map[String, String]().toMap
  }

  def parameters(keyValues: (String, String)*): Map[String, String] = {
    var paramMap: Map[String, String] = Map[String, String]()
    keyValues.foreach(pair => paramMap += (pair._1 -> pair._2))
    paramMap
  }

  def assertResultSize(results: BasicDBList, size: Int): MatchResult[Any] = {
    results.size() === size
  }


  def assertNameForFirstDocument(results: BasicDBList, name: String): MatchResult[Any] = {
    assertNameValue(results, 0, name)
  }

  def assertNameForSecondDocument(results: BasicDBList, name: String): MatchResult[Any] = {
    assertNameValue(results, 1, name)
  }

  def assertNameForThirdDocument(results: BasicDBList, name: String): MatchResult[Any] = {
    assertNameValue(results, 2, name)
  }

  def assertNameValue(results: BasicDBList, index: Int, name: String): MatchResult[Any] = {
    results.get(index).asInstanceOf[BasicDBObject].get("name") === name
  }

  def assertAgeForFirstDocument(results: BasicDBList, age: Int): MatchResult[Any] = {
    assertAgeValue(results, 0, age)
  }

  def assertAgeForSecondDocument(results: BasicDBList, age: Int): MatchResult[Any] = {
    assertAgeValue(results, 1, age)
  }

  def assertAgeForThirdDocument(results: BasicDBList, age: Int): MatchResult[Any] = {
    assertAgeValue(results, 2, age)
  }

  def assertAgeValue(results: BasicDBList, index: Int, age: Int): MatchResult[Any] = {
    results.get(index).asInstanceOf[BasicDBObject].get("age") === age
  }

  def assertKeySetSizeForFirstDocument(results: BasicDBList, size: Int): MatchResult[Any] = {
    results.get(0).asInstanceOf[BasicDBObject].keySet().size() === size
  }

  def assertKeyNameForFirstDocument(results: BasicDBList, name: String): MatchResult[Any] = {
    results.get(0).asInstanceOf[BasicDBObject].keySet().contains(name) === true
  }


}
