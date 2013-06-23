package com.ee.dynamicmongoquery

import com.mongodb.BasicDBObject

class PlaceholderLookup(params: Map[String, String]) {
  val placeholderResolver: PlaceholderResolver = new PlaceholderResolver(params)

  def traverse(basicDBObject: BasicDBObject): BasicDBObject = {
    keySet(basicDBObject).foreach(key => {
      val value = basicDBObject.get(key)
      value match {
        case b: BasicDBObject => {
          traverse(b)
        }
        case s: String => {
          val resolvedValue = placeholderResolver.resolve(s)
          basicDBObject.put(key, resolvedValue)
        }
        case _ => {
          basicDBObject.put(key, value)
        }
      }
    }
    )
    basicDBObject
  }

  def substitute(placeholder: String): Int = {
    val placeholderPattern = "'?\"?(.*?)'?\"?".r
    val placeholderPattern(placeholderWOQuotes) = placeholder
    placeholderResolver.resolve(placeholderWOQuotes).toString.toInt
  }

  private def keySet(basicDBObject: BasicDBObject): List[String] = {
    basicDBObject.keySet().toArray.toList.asInstanceOf[List[String]]
  }
}

