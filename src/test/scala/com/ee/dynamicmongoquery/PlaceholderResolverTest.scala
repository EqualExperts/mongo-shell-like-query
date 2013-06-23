package com.ee.dynamicmongoquery
import org.specs2.mutable.Specification
import org.bson.types.ObjectId
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PlaceholderResolverTest extends Specification {

  "should resolve value for given placeholder" should {

    "hardcoded value" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("leena")
      resolvedValue === "leena"
    }

    "parameterized value" in {
      val params = Map[String, String]("name" -> "leena")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("name")
      resolvedValue === "leena"
    }

    "parameterized value with string datatype" in {
      val params = Map[String, String]("name" -> "leena")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("name#String")
      resolvedValue === "leena"
    }

    "parameterized value with boolean datatype" in {
      val params = Map[String, String]("isAvailable" -> "true")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("isAvailable#Boolean")
      resolvedValue === true
    }

    "parameterized value with double datatype" in {
      val params = Map[String, String]("weight" -> "2.5")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("weight#Double")
      resolvedValue === 2.5
    }

    "parameterized value with ObjectId datatype" in {
      val params = Map[String, String]("id" -> "51bc1b318b117d05212e5ee5")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("id#ObjectId")
      resolvedValue === new ObjectId("51bc1b318b117d05212e5ee5")
    }

    "parameterized value with Long datatype" in {
      val params = Map[String, String]("salary" -> "5000")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("salary#Long")
      resolvedValue === 5000
    }

    "parameterized value with Integer datatype" in {
      val params = Map[String, String]("salary" -> "5000")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("salary#Integer")
      resolvedValue === 5000
    }

    "value containing datatypes as its value" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("dontReplace#Integer")
      resolvedValue === "dontReplace#Integer"
    }

    "paramerized value array" in {
      val params = Map[String, String]("names" -> "leena,tina")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[names]")
      resolvedValue === List("leena", "tina")
    }

    "paramerized value with datatype in array" in {
      val params = Map[String, String]("salary" -> "1000,5000")
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[salary#Long]")
      resolvedValue === List(1000, 5000)
    }

    "hardcoded string value with quotes in array" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("['leena','mina','tina']")
      resolvedValue === List("leena","mina", "tina")
    }

   "hardcoded string value with quotes in array- only one array element" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("['leena']")
      resolvedValue === List("leena")
    }

    "hardcoded long value in array" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[1000,5000,   6000]")
      resolvedValue === List(1000, 5000,6000)
    }

    "hardcoded long value in array- only one array element" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[1000]")
      resolvedValue === List(1000)
    }

    "hardcoded boolean value in array" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[true,false, true]")
      resolvedValue === List(true, false,true)
    }

    "hardcoded boolean value in array- only one array element" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[true]")
      resolvedValue === List(true)
    }

    "hardcoded double value in array" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[30.5, 53.0,54.1]")
      resolvedValue === List(30.5, 53.0,54.1)
    }

    "hardcoded double value in array- only one array element" in {
      val params = Map[String, String]()
      val resolver: PlaceholderResolver = new PlaceholderResolver(params)
      val resolvedValue: Any = resolver.resolve("[50.7]")
      resolvedValue === List(50.7)
    }
  }

}
