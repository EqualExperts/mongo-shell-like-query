package com.ee.dynamicmongoquery

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MongoQueryIntegrationTest extends IntegrationTest {

  "find queries" should {

    "simple find query with no criteria and no projection" in {
      //Given
      val query: String = getMongoQuery("find-wo-criteria")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 6)
    }

    "find query with criteria and projection" in {

      //Given
      val query: String = getMongoQuery("find-with-criteria-projection")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 4)
      assertNameForFirstDocument(results, "ina")

      assertKeySetSizeForFirstDocument(results, 2)
      assertKeyNameForFirstDocument(results, "name")
    }

    "parametarized find query" in {
      //Given
      val query: String = getMongoQuery("find-parametarized")
      val params = parameters("salary" -> "1000", "name" -> "leena", "consultant" -> "true", "wt" -> "30.5")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, params)
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 1)
      assertNameForFirstDocument(results, "leena")

    }


    "find query with nested criteria property" in {
      //Given
      val query: String = getMongoQuery("nested-query")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 1)
      assertNameForFirstDocument(results, "leena")
    }


    "find query with multiple sort and limit" in {
      //Given
      val query: String = getMongoQuery("sort-limit-chaining")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 2)

      assertNameForFirstDocument(results, "ina")
      assertNameForSecondDocument(results, "rian")
    }

    "find query with multiple sort and skip chaining" in {
      //Given
      val query: String = getMongoQuery("sort-skip-chaining")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 2)
      assertNameForFirstDocument(results, "rina")
      assertNameForSecondDocument(results, "vina")
    }

    "find query without criteria and with skip and limit chaining" in {
      //Given
      val query: String = getMongoQuery("find-wo-criteria-skip-limit")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 1)
      assertNameForFirstDocument(results, "tina")
    }

    "find query with parametarized limit" in {
      //Given
      val query: String = getMongoQuery("limit-parametarized")
      val params = parameters("skip" -> "2", "limit" -> "1")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, params)
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 1)
      assertNameForFirstDocument(results, "tina")
    }

    "find query with no results" in {
      //Given
      val query: String = getMongoQuery("no-result-found")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 0)
    }

    "unformatted find query" in {
      //Given
      val query: String = getMongoQuery("unformatted-find-query")

      //When
      val mongoQuery: MongoQuery = parser.parse(query, noParameters())
      val results = mongoQuery.execute(mongoDB)

      //Then
      assertResultSize(results, 2)
      assertNameForFirstDocument(results, "rina")
      assertNameForSecondDocument(results, "vina")

    }

  }

  "aggregate queries" should {

    "aggregate query specified in array format (using $match,$lte,$gthe,$group,$sum,$sort and $limit operator)" >> {
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

    "aggregate query without array format (using $match,$lte,$gthe,$group,$sum,$sort and $limit operator)" in {
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

    "unformatted aggregate query" in {
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

  "in queries" should {

    "parametarized in query with array of long values" in {
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

    "parametarized in query with array of string values" in {
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

    "in query with hardcoded boolean array" in {
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

    "in query with hardcoded double array" in {
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

    "in query with hardcoded long array" in {
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

    "in query with hardcoded string array where each value of array is in double-quote" in {
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
