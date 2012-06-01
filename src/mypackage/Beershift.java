/*
 * Beershift Webservices with Resteasy for Jboss
 * 
 * 
 * Author						Modified
 * Hilay Khatri					06-01-2012
 */

package mypackage;
import java.net.UnknownHostException;
import java.util.Date;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@Path("/beershift")
public class Beershift {
	
	
@GET
@Path("/insert/{userId}/{beer}")	
public void insert(@PathParam("userId") String userID, @PathParam("beer") String beer) {
	
/*	Takes in userID and the beer as parameter and inserts into
 * 	MongoDB with the current Time 
 */

	 try {

	 Mongo mongo = new Mongo("localhost", 27017);
	 DB db = mongo.getDB("beershift");
	 DBCollection collection = db.getCollection("data");
	 BasicDBObject document = new BasicDBObject();

	 document.put("username", userID);
	 document.put("beer", beer);
	 document.put("date", new Date().toString());

	 collection.insert(document);
	 
	 } catch (UnknownHostException e) {
	 e.printStackTrace();
	 } catch (MongoException e) {
	 e.printStackTrace();
	 }

	 }
		 
@GET
@Path("/beers")
@Produces("application/json")
public String display() {
	
/*
 * 	Displays all the information present in MongoDB
 */

	 String msg ="";

	 try {
		 
	 
	 Mongo mongo = new Mongo("localhost", 27017);
	 DB db = mongo.getDB("beershift");
	 DBCollection collection = db.getCollection("data");
	
	 BasicDBObject searchQuery = new BasicDBObject();
//	 searchQuery.put("username", "ravi");
	 DBCursor cursor = collection.find();
	 
	 while (cursor.hasNext()) {
	 msg += cursor.next();
	 }
	
	 } catch (UnknownHostException e) {
	 e.printStackTrace();
	 } catch (MongoException e) {
	 e.printStackTrace();
	 }
	 
	 return msg;
	
	 }
	

}
