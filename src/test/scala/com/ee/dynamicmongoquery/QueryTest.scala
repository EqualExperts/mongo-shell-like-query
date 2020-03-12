package com.ee.dynamicmongoquery

import org.specs2.mutable.Specification
import com.mongodb.BasicDBObject

import collection.JavaConversions._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import scala.collection.immutable.SortedMap
import scala.collection.mutable

@RunWith(classOf[JUnitRunner])
class QueryTest extends Specification {
  "Find Queries" should {

    "simple find query" in {
      //Given
      val query: String = ""
      val params: Map[String, Nothing] = Map()
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val findQuery = FindQuery(query, lookup)

      //Then
      findQuery.criteria === new BasicDBObject()
      findQuery.projection === new BasicDBObject()
    }

    "find query with criteria" in {
      //Given
      val query: String = "{'name':'leena'}"
      val params: Map[String, Nothing] = Map()
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val findQuery = FindQuery(query, lookup)

      //Then
      findQuery.criteria === new BasicDBObject("name", "leena")
      findQuery.projection === new BasicDBObject()
    }

    "find query with params" in {
      //Given
      val query: String = "{'name':'name'}"
      val params: Map[String, String] = Map("name" -> "leena")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val findQuery = FindQuery(query, lookup)

      //Then
      findQuery.criteria === new BasicDBObject("name", "leena")
      findQuery.projection === new BasicDBObject()
    }

    "find query with params and datatypes" in {
      //Given
      val query: String = "{'name':'name#String', 'rank' : 'givenRank#Integer' , 'employed':'isEmployed#Boolean', 'salary':'sal#Long','weight' :'weight#Double'}"
      val params: Map[String, String] = Map("name" -> "leena", "givenRank" -> "5", "isEmployed" -> "true", "sal" -> "1000", "weight" -> "2.5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val findQuery = FindQuery(query, lookup)

      //Then
      val criteriaMap = mutable.LinkedHashMap("name" -> "leena", "rank" -> 5, "employed" -> true, "salary" -> 1000l, "weight" -> 2.5)
      findQuery.criteria === new BasicDBObject(criteriaMap)
      findQuery.projection === new BasicDBObject()
    }
  }

  "Sort Queries" should {

    "simple sort query" in {
      //Given
      val query: String = "{'order' :1}"
      val params: Map[String, Nothing] = Map()
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val sortQuery = SortQuery(query, lookup)

      //Then
      sortQuery.criteria === new BasicDBObject("order", 1)
    }

    "parameterized sort query with datatype" in {
      //Given
      val query: String = "{'order' :'order#Integer'}"
      val params: Map[String, String] = Map("order" -> "1")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val sortQuery = SortQuery(query, lookup)

      //Then
      sortQuery.criteria === new BasicDBObject("order", 1)
    }
  }


  "Skip Queries" should {

    "simple skip query" in {
      //Given
      val query: String = "5"
      val params: Map[String, Nothing] = Map()
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val skipQuery = SkipQuery(query, lookup)

      //Then
      skipQuery.criteria === 5
    }

    "parameterized skip query without quotes" in {
      //Given
      val query: String = "skipVal"
      val params: Map[String, String] = Map("skipVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val skipQuery = SkipQuery(query, lookup)

      //Then
      skipQuery.criteria === 5
    }

    "parameterized skip query with quotes" in {
      //Given
      val query: String = "'skipVal'"
      val params: Map[String, String] = Map("skipVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val skipQuery = SkipQuery(query, lookup)

      //Then
      skipQuery.criteria === 5
    }

    "parameterized skip query with datatype without quotes" in {
      //Given
      val query: String = "skipVal#Integer"
      val params: Map[String, String] = Map("skipVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val skipQuery = SkipQuery(query, lookup)

      //Then
      skipQuery.criteria === 5
    }

    "parameterized skip query with datatype  with quotes" in {
      //Given
      val query: String = "'skipVal#Integer'"
      val params: Map[String, String] = Map("skipVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val skipQuery = SkipQuery(query, lookup)

      //Then
      skipQuery.criteria === 5
    }
  }
  
  "Limit Queries" should {

    "simple limit query" in {
      //Given
      val query: String = "5"
      val params: Map[String, Nothing] = Map()
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val limitQuery = LimitQuery(query, lookup)

      //Then
      limitQuery.criteria === 5
    }

    "parameterized limit query without quotes" in {
      //Given
      val query: String = "limitVal"
      val params: Map[String, String] = Map("limitVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val limitQuery = LimitQuery(query, lookup)

      //Then
      limitQuery.criteria === 5
    }

    "parameterized limit query with quotes" in {
      //Given
      val query: String = "'limitVal'"
      val params: Map[String, String] = Map("limitVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val limitQuery = LimitQuery(query, lookup)

      //Then
      limitQuery.criteria === 5
    }

    "parameterized limit query with datatype without quotes" in {
      //Given
      val query: String = "limitVal#Integer"
      val params: Map[String, String] = Map("limitVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val limitQuery = LimitQuery(query, lookup)

      //Then
      limitQuery.criteria === 5
    }

    "parameterized limit query with datatype  with quotes" in {
      //Given
      val query: String = "'limitVal#Integer'"
      val params: Map[String, String] = Map("limitVal" -> "5")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val limitQuery = LimitQuery(query, lookup)

      //Then
      limitQuery.criteria === 5
    }
  }
}
