package com.ee.dynamicmongoquery

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MongoQueryIntegrationTest extends IntegrationTest {

  "aggregate queries" >> {

    "aggregate query specified in array format (using $match,$lte,$gthe,$group,$sum,$sort and $limit operator) should return matching results" >> {
      //Given
      val query: String = getMongoQuery("aggregate-with-array-format")
      val params = parameters("from" -> "1000", "to" -> "5000")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, params)
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 3)
      assertAgeForFirstDocument(results, 130)
      assertAgeForSecondDocument(results, 40)
      assertAgeForThirdDocument(results, 30)
    }

    "aggregate query without array format (using $match,$lte,$gthe,$group,$sum,$sort and $limit operator) should return matching results" >> {
      //Given
      val query: String = getMongoQuery("aggregate-without-array-format")
      val params = parameters("from" -> "1000", "to" -> "5000")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, params)
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 3)
      assertAgeForFirstDocument(results, 130)
      assertAgeForSecondDocument(results, 40)
      assertAgeForThirdDocument(results, 30)
    }

    "unformatted aggregate query should return matching results" >> {
      //Given
      val query: String = getMongoQuery("unformatted-aggregate-query")
      val params = parameters("from" -> "1000", "to" -> "5000")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, params)
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 3)
      assertAgeForFirstDocument(results, 130)
      assertAgeForSecondDocument(results, 40)
      assertAgeForThirdDocument(results, 30)
    }
  }

  "in queries" >> {

    "parametarized in-query with array of long values should return matching results" >> {
      //Given
      val query: String = getMongoQuery("parametarized-in-query-with-long-array")
      val params = parameters("from" -> "30", "to" -> "45", "salary" -> "1000,5000")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, params)
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 1)
      assertNameForFirstDocument(results, "leena")
    }

    "parametarized in-query with array of string values should return matching results" >> {
      //Given
      val query: String = getMongoQuery("parametarized-in-query-with-string-array")
      val params = parameters("name" -> "leena,tina")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, params)
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 2)
      assertNameForFirstDocument(results, "leena")
      assertNameForSecondDocument(results, "tina")
    }

    "in-query with hardcoded boolean array should return matching results" >> {
      //Given
      val query: String = getMongoQuery("in-with-hardcoded-boolean-array")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 3)
      assertNameForFirstDocument(results, "leena")
      assertNameForSecondDocument(results, "ina")
      assertNameForThirdDocument(results, "rina")
    }

    "in-query with hardcoded double array should return matching results" >> {
      //Given
      val query: String = getMongoQuery("in-with-hardcoded-double-array")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 2)
      assertNameForFirstDocument(results, "leena")
      assertNameForSecondDocument(results, "rina")
    }

    "in-query with hardcoded long array should return matching results" >> {
      //Given
      val query: String = getMongoQuery("in-with-hardcoded-long-array")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 2)
      assertNameForFirstDocument(results, "leena")
      assertNameForSecondDocument(results, "vina")
    }

    "in-query with hardcoded string array where each value of array is in double-quote should return matching results" >> {
      //Given
      val query: String = getMongoQuery("in-with-hardcoded-string-array-double-quote")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 2)
      assertNameForFirstDocument(results, "leena")
      assertNameForSecondDocument(results, "tina")
    }
  }

}
