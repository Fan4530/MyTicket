Synopsis
At the top of the file there should be a short introduction and/ or overview that explains what the project is. This description should match descriptions added for package managers (Gemspec, package.json, etc.)
The main goal of this project is to recommend tickets for users according to Geo-location and like. 
The architecture of this project is as the following shows:  
      
src/
•	rpc/ 
  o	The entry point of the project, handles rpc request/response, parsing, etc.
  o	SearchItem.java
  o	RecommendItem.java
  o	ItemHistory.java
  o	RpcHelper.java
  o	Login_in.java
  
•	db/ 
  o	The ‘backend’ of the project, connects to database.
  o	DBConnection.java is an interface. (implement it in mysql and mongodb)
  o	DbConnectionFactory.java is a factory class.
  o	mysql/
  o	mongodb/
•	externel/ 
  o	Another ‘backend’ of theiq project, connects to public APIs.
  o	ExternalAPI.java is an interface. All supported backends should implement it.
  o	ExternalAPIFactory.java is a factory class.
  o	TicketMasterAPI.java. ‘Backend’ of our project, connects to TicketMaster API.
•	entity/ 
  o	Handles creation/conversion/etc of object instances. 
  o	Item.java
•	algorithm/ 
  o	event recommendation algorithms.
  o	GeoRecommendation.java
  o	Recommendation.java

Let me show how this files work: 

client --> call servlet : login.java   ---------|
                                                |
                                                |
                         ---------------------------------------------|
                         |                      |                     |
                         |                      |                     |
                         |                      |                     |
                      ItemHistory.java      	SearchItem.java        	RecommendItem.java        <-------          Recommendation.java   <---  GeoRecommendation.java   
                                                                                                                           ^
                                                                                                                           |
                                                     DbConnectionFactory       ---------------------------------------------
                                                /                      \
   Connection(implemented by)  --->        MysqlDBConnection             MongoDbDBConnection <-----------------
                                            ^                                     ^                            |
                                            |                                     |                            |
                                       mysql(creation)                    mongodb(creation)                    |
                                                                                                               |
                                                                                                               |
                                                                                                               |
                                                                        ExternalAPIFactory(create db)   -------|
                                                                           ^          ^          ^
                                                                          /           |           \
                ExternalAPI interface  -->(implement by)  TicketMasterAPI            ....         ...
                   


