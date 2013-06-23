package com.ee.dynamicmongoquery

import scala.collection.JavaConversions._

class MongoQueryParser {

  private val findPattern = "\\.find\\(\\{.*?\\}\\)".r
  private val findContentPattern = "\\.find\\((\\{.*?\\})\\)".r

  private val plainFindPattern = "\\.find\\(\\)".r

  private val sortPattern = "\\.sort\\(\\{.*?\\}\\)".r
  private val sortContentPattern = "\\.sort\\((\\{.*?\\})\\)".r

  private val skipPattern = "\\.skip\\(.*?\\)".r
  private val skipContentPattern = "\\.skip\\((.*?)\\)".r

  private val limitPattern = "\\.limit\\(.*?\\)".r
  private val limitContentPattern = "\\.limit\\((.*?)\\)".r

  private val aggregatePattern = "\\.aggregate\\(\\[?\\{.*?\\}\\]?\\)".r
  private val aggregateContentPattern = "\\.aggregate\\((\\[?\\{.*?\\}\\]?)\\)".r

  private val collectionNamePattern = "db\\.(.*?)(?:\\.find\\(.*?\\))?(?:\\.aggregate\\(.*?\\))?".r

  def parse(queryString: java.lang.String, queryParameters: java.util.HashMap[java.lang.String, java.lang.String]): MongoQuery = {
    parse(queryString, queryParameters.toMap)
  }

  def parse(queryString: String, queryParameters: Map[String, String]): MongoQuery = {
    val placeholderLookup: PlaceholderLookup = new PlaceholderLookup(queryParameters)

    val formattedQueryString = new MongoQueryCleanser().clean(queryString)

    println("formatted query: " + formattedQueryString)

    val findQueries: List[Query] = findPattern.findAllIn(formattedQueryString).toList.map(q => {
      val findContentPattern(findQueryString) = q;
      FindQuery(findQueryString, placeholderLookup)
    })

    val plainFindQueries: List[Query] = plainFindPattern.findAllIn(formattedQueryString).toList.map(q => {
      FindQuery("", placeholderLookup)
    })

    val sortQueries: List[Query] = sortPattern.findAllIn(formattedQueryString).toList.map(q => {
      val sortContentPattern(sortQueryString) = q;
      SortQuery(sortQueryString, placeholderLookup)
    })

    val skipQueries: List[Query] = skipPattern.findAllIn(formattedQueryString).toList.map(q => {
      val skipContentPattern(skipQueryString) = q;
      SkipQuery(skipQueryString, placeholderLookup)
    })

    val limitQueries: List[Query] = limitPattern.findAllIn(formattedQueryString).toList.map(q => {
      val limitContentPattern(limitQueryString) = q;
      LimitQuery(limitQueryString, placeholderLookup)
    })

    val aggregationQueries: List[Query] = aggregatePattern.findAllIn(formattedQueryString).toList.map(q => {
      val aggregateContentPattern(aggregationQueryString) = q;
      AggregateQuery(aggregationQueryString, placeholderLookup)
    })

    val collectionNamePattern(collectionName) = formattedQueryString
    val queryParts: List[Query] = findQueries ::: plainFindQueries ::: sortQueries ::: skipQueries ::: limitQueries ::: aggregationQueries
    new MongoQuery(queryParts, collectionName)
  }

}


