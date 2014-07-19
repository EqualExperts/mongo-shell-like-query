                                              mongo-shell-like-query
                                              ======================


                                Executing mongo-shell-like-query with mongo Java driver

Describe your read queries the same as you do on the mongo client shell, and the library runs it, for both regular queries as well as use of the Mongo aggregation facility

[Download a jar for the current release (v1.0) here](https://github.com/EqualExperts/mongo-shell-like-query/blob/master/releases/1.0/mongo-shell-like-query.jar)

#Example
<pre><code>
    String query = ”db.users.find( { ‘name’ : ‘John’} )”;

    MongoQueryParser parser = new MongoQueryParser();
    MongoQuery mongoQuery = parser.parse(query, new HashMap());
    BasicDBList results = mongoQuery.execute(mongoDB);
<code></pre>
#Features

1. It supports all kinds of read queries (both find as well as aggregation) using various operators

2. It supports query chaining (chain the use of sort, skip, and limit in any order)

3. It tolerates whitespace as: ”db.users.find( { ‘name’ :   ‘John’} )” is of course, the same as 
   ”db   .   users  .  find  (  {   ‘name’   :   ‘John’   }   )”

4. It supports parameterized queries, wherein values can be specified at runtime

 <pre><code>
       String query = ”db.users.find( { ‘name’ : ‘givenName’} )”
       Map[String,String]  params= new HashMap()
       map.add(“givenName”, “John”)

       MongoQueryParser parser = new MongoQueryParser();
       MongoQuery mongoQuery = parser.parse(query, params);
       BaiscDBList results = mongoQuery.execute(mongoDB);
</code></pre>

5. It supports parameterized queries with strongly typed data (Most BSON types are supported)

 <pre><code>
       String query = ”db.users.find( { salary : ‘sal#Long’} )” 
       Map[String,String]  params= new HashMap()
       map.add(“sal”, “5000”)

       MongoQueryParser parser = new MongoQueryParser();
       MongoQuery mongoQuery = parser.parse(query, params);
       BaiscDBList results = mongoQuery.execute(mongoDB);
</code></pre>

 Query looks like mongo query string. Providing placeholders is very simple and have consistent pattern.
ParameterName#DataType (supported datatypes are String|Boolean|Integer|Double|ObjectId)

 For more examples checkout ‘src/test/resources’ folder and MongoQueryIntegrationTest

6. It supports both scala and java api 

#Usage

1. Download the .jar file and add it to your project 

2.Use library:

  a) Java API
<pre><code>
  String query = "db.users.find( { 'role' : 'manager'} )";
  DB mongoDB = new MongoClient().getDB("your-db-name");

  MongoQuery mongoQuery = new   MongoQueryParser().parse(query, new HashMap<String, String>());
  BasicDBList results = mongoQuery.execute(mongoDB);
  System.out.println(results.size());
</code></pre>
   b) Scala API
<pre><code>
  val query = "db.users.find( { 'role' : 'manager'} )"
  val mongoConn = MongoConnection()
  val mongoDB = mongoConn("your-db-name")
  
  val mongoQuery: MongoQuery = parser.parse(query, Map())
  val results = mongoQuery.execute(mongoDB)
  println(results.size)
</code></pre> 
END-OF-FILE 
