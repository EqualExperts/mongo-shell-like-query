package com.ee.dynamicmongoquery

import org.bson.types.ObjectId

class PlaceholderResolver(params: Map[String, String]) {

  private val supportedDataTypes = "String|Boolean|Integer|Double|ObjectId"

  def resolve(placeholder: String): Any = {
    val placeholderStringWithType = "^.*?#(?:String|Boolean|Integer|Long|Double|ObjectId)$".r
    val placeholderArrayWithHardCodedValuesOrOneParam = "^\\[(.+?(?:,.+?)*)\\]$".r
    val placeholderArrayWithType = "^\\[.*?#(?:String|Boolean|Integer|Long|Double|ObjectId)\\]$".r

    val resolvedValue = placeholder match {

      case placeholderArrayWithType() => {
        val (placeholderKey, placeholderType) = placeholderKeyWithType(arrayPlaceholder(placeholder))
        if (params.contains(placeholderKey)) {
          placeholderType.arrayValueOf(params(placeholderKey))
        }
        else {
          placeholder
        }
      }
      case placeholderArrayWithHardCodedValuesOrOneParam(values) => {
        arrayValues(values)
      }
      case placeholderStringWithType() => {
        val (placeholderKey, placeholderType) = placeholderKeyWithType(placeholder)
        if (params.contains(placeholderKey)) {
          placeholderType.valueOf(params(placeholderKey))
        }
        else {
          placeholder
        }
      }
      case _ => {
        if (params.contains(placeholder))
          params(placeholder)
        else
          placeholder
      }
    }
    resolvedValue
  }

  private def arrayValues(arrayValuesString: String): Any = {
    val stringArrayPattern = "(?:(?:\"|\'|')(?:.+?)(?:\"|\'|')(?:,(?:\"|\'|')(?:.+?)(?:\"|\'|'))*)".r
    val numberArrayPattern = "(?:\\d+?(?: *, *\\d+? *)*)".r
    val doubleArrayPattern = "(?:(?:\\d+?\\.\\d+?)(?: *, *\\d+?\\.\\d+? *)*)".r
    val booleanArrayPattern = "(?:(?:true|false)(?: *, *(?:true|false) *)*)".r

    val arrayVal = arrayValuesString match {
      case stringArrayPattern() => StringType().arrayValueOf(arrayValuesString.replaceAll("'", "").replaceAll("\\\"", "").replaceAll("\\'", ""))
      case numberArrayPattern() => LongType().arrayValueOf(arrayValuesString)
      case doubleArrayPattern() => DoubleType().arrayValueOf(arrayValuesString)
      case booleanArrayPattern() => BooleanType().arrayValueOf(arrayValuesString)
      case _ => if (params.contains(arrayValuesString)) {
        params(arrayValuesString).split(",").toList
      }
      else {
        arrayValuesString
      }

    }
    arrayVal
  }

  private def arrayPlaceholder(stringValue: String): String = {
    stringValue.substring(1, stringValue.length - 1)
  }

  private def placeholderKeyWithType(stringVal: String): (String, Type[_]) = {
    val hashIndex = stringVal.indexOf('#')
    val placeholderKey = stringVal.substring(0, hashIndex)
    val placeholderTypeString = stringVal.substring(hashIndex + 1, stringVal.length)

    val placeholderType: Type[_] = placeholderTypeString match {
      case "String" => StringType()
      case "Boolean" => BooleanType()
      case "Integer" => IntegerType()
      case "Long" => LongType()
      case "Double" => DoubleType()
      case "ObjectId" => ObjectIdType()
      case _ => throw new UnsupportedOperationException("datatype not supported. Supported datatypes are String|Boolean|Integer|Double|ObjectId")
    }
    (placeholderKey, placeholderType)


  }


  private trait Type[+T] {
    def valueOf(value: String): T

    def arrayValueOf(value: String): List[T]
  }

  private case class LongType() extends Type[Long] {

    def valueOf(value: String): Long = {
      value.toLong
    }

    def arrayValueOf(value: String): List[Long] = {
      value.split(",").map(v => v.trim.toLong).toList
    }
  }

  private case class ObjectIdType() extends Type[ObjectId] {

    def valueOf(value: String): ObjectId = {
      new ObjectId(value)
    }

    def arrayValueOf(value: String): List[ObjectId] = {
      value.split(",").map(v => new ObjectId(v.trim)).toList
    }
  }

  private case class IntegerType() extends Type[Int] {

    def valueOf(value: String): Int = {
      value.toInt
    }

    def arrayValueOf(value: String): List[Int] = {
      value.split(",").map(v => v.trim.toInt).toList
    }
  }

  private case class DoubleType() extends Type[Double] {

    def valueOf(value: String): Double = {
      value.toDouble
    }

    def arrayValueOf(value: String): List[Double] = {
      value.split(",").map(v => v.trim.toDouble).toList
    }
  }

  private case class StringType() extends Type[String] {

    def valueOf(value: String): String = {
      value
    }

    def arrayValueOf(value: String): List[String] = {
      value.split(",").toList
    }
  }

  private case class BooleanType() extends Type[Boolean] {

    def valueOf(value: String): Boolean = {
      value.toBoolean
    }

    def arrayValueOf(value: String): List[Boolean] = {
      value.split(",").toList.map(v => v.trim.toBoolean).toList
    }
  }

}
