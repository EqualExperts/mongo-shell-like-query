package com.ee.dynamicmongoquery

import com.mongodb.{BasicDBList, BasicDBObject}
import com.mongodb.util.JSON

class Query(placeholderLookup: PlaceholderLookup) {

  protected def substitute(queryObjects: List[BasicDBObject]): List[BasicDBObject] = {
    var substitutedQueryObjects =
      for (queryObject <- queryObjects) yield substitute(queryObject)
    substitutedQueryObjects
  }

  protected def substitute(queryObject: BasicDBObject): BasicDBObject = {
    placeholderLookup.traverse(queryObject)
  }

  protected def substitute(queryObject: String): Int = {
    placeholderLookup.substitute(queryObject)
  }

  protected def parseJsonToDBList(json: String): List[BasicDBObject] = {
    JSON.parse(json).asInstanceOf[BasicDBList].toArray.toList.map(obj => obj.asInstanceOf[BasicDBObject])
  }

  protected def parseJsonToDBObject(json: String): BasicDBObject = {
    JSON.parse(json).asInstanceOf[BasicDBObject]
  }

}


case class FindQuery(query: String, placeholderLookup: PlaceholderLookup) extends Query(placeholderLookup) {
  private val noCriteria = new BasicDBObject()
  private val findQuery = if (query.isEmpty) List((noCriteria)) else parseJsonToDBList("[" + query + "]")

  val criteria = substitute(findQuery(0))

  val projection = if (isProjectionDefined) findQuery(1) else noCriteria

  private def isProjectionDefined: Boolean = {
    findQuery.size == 2
  }

  override def equals(that: Any): Boolean = {
    that match {
      case that: FindQuery => {
        this.criteria == that.criteria &&
          this.projection == that.projection
      }
      case _ => false
    }
  }

  override def hashCode(): Int = {
    criteria.size()
  }
}


case class SortQuery(query: String, placeholderLookup: PlaceholderLookup) extends Query(placeholderLookup) {
  val criteria = substitute(parseJsonToDBObject(query))

  override def equals(that: Any): Boolean = {
    that match {
      case that: SortQuery => {
        this.criteria == that.criteria
      }
      case _ => false
    }
  }

  override def hashCode(): Int = {
    criteria.size()
  }

}

case class SkipQuery(query: String, placeholderLookup: PlaceholderLookup) extends Query(placeholderLookup) {
  val criteria = substitute(query)

  override def equals(that: Any): Boolean = {
    that match {
      case that: SkipQuery => {
        this.criteria == that.criteria
      }
      case _ => false
    }
  }

  override def hashCode(): Int = {
    criteria
  }

}

case class LimitQuery(query: String, placeholderLookup: PlaceholderLookup) extends Query(placeholderLookup) {
  val criteria = substitute(query)

  override def equals(that: Any): Boolean = {
    that match {
      case that: LimitQuery => {
        this.criteria == that.criteria
      }
      case _ => false
    }
  }

  override def hashCode(): Int = {
    criteria
  }
}

case class AggregateQuery(query: String, placeholderLookup: PlaceholderLookup) extends Query(placeholderLookup) {
  private val querySpecifiedInArray = "^\\[.*\\]$".r

  val aggregationPipeline = query match {
    case querySpecifiedInArray() => substitute(parseJsonToDBList(query))
    case _ => substitute(parseJsonToDBList("[" + query + "]"))
  }

  override def equals(that: Any): Boolean = {
    that match {
      case that: AggregateQuery => {
        this.aggregationPipeline == that.aggregationPipeline
      }
      case _ => false
    }
  }

  override def hashCode(): Int = {
    aggregationPipeline.length
  }

}




