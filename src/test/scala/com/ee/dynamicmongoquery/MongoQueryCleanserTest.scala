package com.ee.dynamicmongoquery

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MongoQueryCleanserTest extends Specification {
  val cleanser: MongoQueryCleanser = new MongoQueryCleanser

  "It should cleanse query " >> {
    "query conataining single quotes" >> {

      val query: String = "db.  users .  find  (  {  'acd' : '1 234 56'  }  )"
      val formattedQuery = cleanser.clean(query)

      formattedQuery === "db.users.find({'acd':'1 234 56'})"

    }

    "query conataining single quotes more complex version" in {

      val query: String = "db.  users .  find  (  {  'acd' : '1 234 56'  }   .sort   ({'name.some'   :1}) )"
      val formattedQuery = cleanser.clean(query)

      formattedQuery === "db.users.find({'acd':'1 234 56'}.sort({'name.some':1}))"
    }

    "query conataining double quotes " in {

      val query: String = "db.  users .  find  (  {  \"acd\" : \"1 234 56\"  }   .sort   ({\"name.some\"   :1}) )"
      val formattedQuery = cleanser.clean(query)

      formattedQuery === "db.users.find({\"acd\":\"1 234 56\"}.sort({\"name.some\":1}))"
    }

    "query conataining double quotes and single quotes" in {

      val query: String = "db.  users .  find  (  {  \"acd\" : \"1 234 56\"  }   .sort   ({'name.some'   :1}) )"
      val formattedQuery = cleanser.clean(query)
      formattedQuery === "db.users.find({\"acd\":\"1 234 56\"}.sort({'name.some':1}))"
    }

    "query conataining double quotes and single quotes and new lines characters" in {

      val query: String = "db.  users .  find \n ( \n {  \"acd\" : \"1 234 56\"  }   .sort \n  ({'name.some' \n  :1}) )"
      val formattedQuery = cleanser.clean(query)
      formattedQuery === "db.users.find({\"acd\":\"1 234 56\"}.sort({'name.some':1}))"
    }

    "query conataining double quotes and single quotes and value itself contains double quotes" in {

      val query: String = "db.  users .  find \n ( \n {  \"acd\" : \"1 '234' 56\"  }   .sort \n  ({'name.some' \n  :1}) )"
      val formattedQuery = cleanser.clean(query)
      formattedQuery === "db.users.find({\"acd\":\"1 '234' 56\"}.sort({'name.some':1}))"
    }
  }
}
