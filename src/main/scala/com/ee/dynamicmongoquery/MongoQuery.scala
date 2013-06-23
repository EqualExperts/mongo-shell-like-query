package com.ee.dynamicmongoquery

import com.mongodb._
import com.mongodb.casbah.MongoDB
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.Array
import java.util

class MongoQuery(val queryTypes: List[Query],val collectionName: String) {

  def execute(spectacleDB: MongoDB): BasicDBList = {
    val collection: DBCollection = spectacleDB.getCollection(collectionName)

    val results = queryTypes.head match {
      case findQuery: FindQuery => {
        val cursor = executeQuery(queryTypes, collection, null)
        convertToBasicDBList(cursor)
      }
      case aggregateQuery: AggregateQuery => {
        val resultArray = executeAggregate(aggregateQuery, collection)
        convertArrayToBasicDBList(resultArray)
      }
    }
    results
  }

  protected def executeQuery(queryTypes: List[Query], collection: DBCollection, dbCursor: DBCursor): DBCursor = {
    if (queryTypes.isEmpty)
      dbCursor
    else {
      val query = queryTypes.head
      val cursor = query match {
        case find: FindQuery => collection.find(find.criteria, find.projection)
        case sort: SortQuery => dbCursor.sort(sort.criteria)
        case skip: SkipQuery => dbCursor.skip(skip.criteria)
        case limit: LimitQuery => dbCursor.limit(limit.criteria)
      }
      executeQuery(queryTypes.tail, collection, cursor)
    }
  }

  protected def executeAggregate(aggregationQuery: AggregateQuery, collection: DBCollection): Array[DBObject] = {
    val aggregationPipeline = aggregationQuery.aggregationPipeline
    var aggregateOP = collection.aggregate(aggregationPipeline.head, aggregationPipeline.tail: _*)
    aggregateOP.results().toTraversable.toArray
  }


  protected def convertToBasicDBList(cursor: DBCursor): BasicDBList = {
    var basicDBList: BasicDBList = new BasicDBList
    var findResult: util.List[DBObject] = cursor.toArray
    findResult.asScala.toList.foreach(obj => basicDBList.add(obj))

    basicDBList
  }

  protected def convertArrayToBasicDBList(array: Array[DBObject]): BasicDBList = {
    var basicDBList: BasicDBList = new BasicDBList
    array.foreach(dataObj => basicDBList.add(dataObj))
    basicDBList
  }

  override def equals(that: Any): Boolean = {
    that match {
      case that: MongoQuery => {
        this.collectionName == that.collectionName    &&
        this.queryTypes == that.queryTypes
      }
      case _ => false
    }
  }
}

