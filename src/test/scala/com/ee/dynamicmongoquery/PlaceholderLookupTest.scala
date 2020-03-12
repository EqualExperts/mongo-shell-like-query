package com.ee.dynamicmongoquery

import org.specs2.mutable.Specification
import com.mongodb.BasicDBObject
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PlaceholderLookupTest extends Specification {

  "should traverse Object to find placeholders" in {
    val params: Map[String, String] = Map("country" -> "India", "zip" -> "411037", "name" -> "leena")
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    val address = new BasicDBObject()
    address.put("lane", 5:Integer)
    address.put("city", "Pune")
    address.put("country", "country")
    address.put("zipcode", "zip#Integer")
    val person = new BasicDBObject()
    person.put("name", "name")
    person.put("address", address)
    person.put("dontreplace", "dontreplace#Long")

    val expectedAddress = new BasicDBObject()
    expectedAddress.put("lane", 5:Integer)
    expectedAddress.put("city", "Pune")
    expectedAddress.put("country", "India")
    expectedAddress.put("zipcode", 411037:Integer)
    val expectedPerson = new BasicDBObject()
    expectedPerson.put("name", "leena")
    expectedPerson.put("address", address)
    expectedPerson.put("dontreplace", "dontreplace#Long")

    val actualPerson: BasicDBObject = lookup.traverse(person)

    actualPerson == expectedPerson
  }

  "should find placehonder  in string value" should {
    val params: Map[String, String] = Map("count" -> "5")
    val lookup: PlaceholderLookup = new PlaceholderLookup(params)

    "parameter specified in single quote" in {
      val resolvedValue: Int = lookup.substitute("'count'")
      resolvedValue === 5
    }

    "parameter specified in double quote" in {
      val resolvedValue: Int = lookup.substitute("\"count\"")
      resolvedValue === 5
    }

    "parameter specified without quote" in {
      val resolvedValue: Int = lookup.substitute("count")
      resolvedValue === 5
    }

    "hardcoded value in single quote" in {
      val resolvedValue: Int = lookup.substitute("'5'")
      resolvedValue === 5
    }

    "hardcoded value in double quote" in {
      val resolvedValue: Int = lookup.substitute("\"5\"")
      resolvedValue === 5
    }

    "hardcoded value without quote" in {
      val resolvedValue: Int = lookup.substitute("5")
      resolvedValue === 5
    }

    "parameter specified in single quote and with datatype" in {
      val resolvedValue: Int = lookup.substitute("'count#Long'")
      resolvedValue === 5
    }

    "parameter specified in double quote and with datatype" in {
      val resolvedValue: Int = lookup.substitute("\"count#Long\"")
      resolvedValue === 5
    }

  }
}
