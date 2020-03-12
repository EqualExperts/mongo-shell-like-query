package com.ee.dynamicmongoquery

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MongoQueryParserTest extends Specification {
  val parser: MongoQueryParser = new MongoQueryParser()


  "It should parse query " should {
    "simple find query" in {
      //Given
      val query: String = "db.users.find()"
      val params: Map[String, Nothing] = Map()
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val actualMongoQuery: MongoQuery = parser.parse(query, params)

      //Then
      val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](FindQuery("", lookup)), "users")

      actualMongoQuery === expectedMongoQuery
    }

    "find query with criteria" in {
      //Given
      val query: String = "db.users.find({'name':'leena'})"
      val params: Map[String, Nothing] = Map()
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val actualMongoQuery: MongoQuery = parser.parse(query, params)

      //Then
      val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](FindQuery("{'name' :'leena'}", lookup)), "users")

      actualMongoQuery === expectedMongoQuery
    }

    "find query with params" in {
      //Given
      val query: String = "db.users.find({'name':'name'})"
      val params: Map[String, String] = Map("name" -> "leena")
      val lookup: PlaceholderLookup = new PlaceholderLookup(params)

      //When
      val actualMongoQuery: MongoQuery = parser.parse(query, params)

      //Then
      val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](FindQuery("{'name' :'leena'}", lookup)), "users")

      actualMongoQuery === expectedMongoQuery
    }
  }

  "find query with params and datatypes" in {
    //Given
    val query: String = "db.users.find({'name':'name#String', 'rank' : 'givenRank#Integer' , 'employed':'isEmployed#Boolean', 'salary':'sal#Integer','weight' :'weight#Double'})"
    val params: Map[String, String] = Map("name" -> "leena", "givenRank" -> "5", "isEmployed" -> "true", "sal" -> "1000", "weight" -> "2.5")
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](FindQuery("{'name':'leena' , 'rank':5,'employed': true, 'salary':1000, 'weight':2.5}", lookup)), "users")

    actualMongoQuery === expectedMongoQuery
  }

  "find query with sort skip limit chaining" in {
    //Given
    val query: String = "db.users.find({'name':'leena'}).skip(5).sort({'role':1}).skip(1).limit(1).sort({'salary':1}).skip(3)"
    val params: Map[String, Nothing] = Map()
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val queryParts = List[Query](FindQuery("{'name' :'leena'}", lookup), SortQuery("{'role' :1}", lookup), SortQuery("{'salary' :1}", lookup), SkipQuery("5", lookup), SkipQuery("1", lookup), SkipQuery("3", lookup), LimitQuery("1", lookup))
    val expectedMongoQuery: MongoQuery = new MongoQuery(queryParts, "users")

    actualMongoQuery === expectedMongoQuery
  }

  "find query with sort skip limit with parameterized values" in {
    //Given
    val query: String = "db.users.find({'name':'leena'}).skip('skipVal').sort({'role':'order#Integer'}).limit('limit#Integer')"
    val params: Map[String, String] = Map("skipVal" -> "1","order" -> "-1", "limit" -> "5" )
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val queryParts = List[Query](FindQuery("{'name' :'leena'}", lookup), SortQuery("{'role' :-1}", lookup), SkipQuery("1", lookup), LimitQuery("5", lookup))
    val expectedMongoQuery: MongoQuery = new MongoQuery(queryParts, "users")

    actualMongoQuery === expectedMongoQuery
  }

  "find query with projection" in {
    //Given
    val query: String = "db.users.find({'name':'leena'}, {'role' :1})"
    val params: Map[String, Nothing] = Map()
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](FindQuery("{'name' :'leena'},{'role':1}", lookup)), "users")

    actualMongoQuery === expectedMongoQuery
  }

  "unformatted find query" in {
    //Given
    val query: String = "db   .users.   find   (  {'name':'leena l bora'}) .   skip(5). sort({'role':1}).   skip(1).limit(1). sort({'salary':1}).skip(3)"
    val params: Map[String, Nothing] = Map()
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val queryParts = List[Query](FindQuery("{'name' :'leena l bora'}", lookup), SortQuery("{'role' :1}", lookup), SortQuery("{'salary' :1}", lookup), SkipQuery("5", lookup), SkipQuery("1", lookup), SkipQuery("3", lookup), LimitQuery("1", lookup))
    val expectedMongoQuery: MongoQuery = new MongoQuery(queryParts, "users")

    actualMongoQuery === expectedMongoQuery
  }

  "collection name having '.' " in {
    //Given
    val query: String = "db.admin.users.find({'name':'leena'}, {'role' :1})"
    val params: Map[String, Nothing] = Map()
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](FindQuery("{'name' :'leena'},{'role':1}", lookup)), "admin.users")

    actualMongoQuery === expectedMongoQuery
  }

  "aggregation query " in {
    //Given
    val query: String = "db.users.aggregate([{'$match':{'salary' : {$gte:'from#Integer',$lte:'to#Integer'}}}, { $group: {_id:'$role', 'role': {$sum:  '$age' }  }  }, {  '$sort': {  'age': -1 } },   {  '$limit': 5 }])"
    val params: Map[String, String] = Map("from"->"1000","to" -> "2000")
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](AggregateQuery("{'$match':{'salary' : {$gte:1000 ,$lte:2000}}},  { $group: {_id:'$role', 'role': {$sum:  '$age' }  }  }, {  '$sort': {  'age': -1 } },   {  '$limit': 5 }", lookup)), "users")

    actualMongoQuery === expectedMongoQuery
  }

  "aggregation query without array " in {
    //Given
    val query: String = "db.users.aggregate(  {  '$match':{'salary' : {$gte:'from#Integer',$lte:'to#Integer'}}}, { $group: {_id:'$role', 'role': {$sum:  '$age' }  }  }, {  '$sort': {  'age': -1 } },   {  '$limit': 5  }  )"
    val params: Map[String, String] = Map("from"->"1000","to" -> "2000")
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    //When
    val actualMongoQuery: MongoQuery = parser.parse(query, params)

    //Then
    val expectedMongoQuery: MongoQuery = new MongoQuery(List[Query](AggregateQuery("{'$match':{'salary' : {$gte:1000 ,$lte:2000}}},  { $group: {_id:'$role', 'role': {$sum:  '$age' }  }  }, {  '$sort': {  'age': -1 } },   {  '$limit': 5 }", lookup)), "users")

    actualMongoQuery === expectedMongoQuery
  }
}
