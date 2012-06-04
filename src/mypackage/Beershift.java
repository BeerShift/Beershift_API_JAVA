/*
 * Beershift Webservices with Resteasy for Jboss
 * 
 * 
 * Author					Comment														Modified
 * 
 * Hilay Khatri				Added show all beers, insert new beer drank					05-31-2012
 * 
 * Hilay Khatri				Added search beer, show beers drank by user					06-01-2012
 * 
 * Ramana Malladi			Added authentication, add a new user						06-01-2012
 * 
 * Hilay Khatri				Added Limit of 50 on result displaying beers, sort 			06-02-2012
 * 							by date
 * 
 */

package mypackage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import sun.misc.BASE64Encoder;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import java.io.*;
import java.net.*;

@Path("/api")
public class Beershift {
	
	String pint_url = "http://api.brewerydb.com/v2/search";
    String api_key= "2f1549c9d86ea5088379d278c0451822";
    String type ="beer";
	
	
	@GET
	@Path("/adduser/{userId}/{password}")
	@Produces("application/json")
	 
	public String adduser(@PathParam("userId") String userID, @PathParam("password") String password) {
		
	/*	
	 * 	Adds a new username-password tuple to the database.
	 */ 
		Boolean userExists = false;
		String msg ="failure-user already existes";

		 try {	
			 
			 
		
		 Mongo mongo = new Mongo("localhost", 27017);
		 DB db = mongo.getDB("beershift");
		 DBCollection collection = db.getCollection("users");
		 BasicDBObject document = new BasicDBObject();
		 
		 
		 BasicDBObject searchQuery = new BasicDBObject();
	 	 searchQuery.put("username", userID);

	 	 BasicDBObject keys = new BasicDBObject();
	 	 keys.put("username", 1);

	 	 
	 	 DBCursor cursor = collection.find(searchQuery,keys);
	 	 
	 	 while (cursor.hasNext()) {
	 	// msg += cursor.next();
	 	 BasicDBObject obj = (BasicDBObject) cursor.next();
	     String result="";
		result+= obj.getString("username");
		if(result.equals(userID))
		{
			userExists=true;
			
		}
		}
		
		 if(userExists==false){
			 msg ="Success-user created";
	 	 document.put("username", userID);
		 document.put("password", encryptPassword(password,"SHA-1","UTF-8"));
		 document.put("creation_date", new Date().toString());

		 collection.insert(document);
		 }
		
		 } catch (UnknownHostException e) {
		 e.printStackTrace();
		 } catch (MongoException e) {
		 e.printStackTrace();
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return msg;
		 }
	

	// TODO Auto-generated method stub
	/*This method encrypts the password using SHA-1 algorithm and UTF-8 encoding. */
	 private static  String encryptPassword(String plaintext,
	            String algorithm, String encoding) throws Exception {
	        MessageDigest msgDigest = null;
	        String hashValue = null;
	        try {
	            msgDigest = MessageDigest.getInstance(algorithm);
	            msgDigest.update(plaintext.getBytes(encoding));
	            byte rawByte[] = msgDigest.digest();
	            hashValue = (new BASE64Encoder()).encode(rawByte);
	 
	        } catch (NoSuchAlgorithmException e) {
	            System.out.println("No Such Algorithm Exists");
	        } catch (UnsupportedEncodingException e) {
	            System.out.println("The Encoding Is Not Supported");
	        }
	        return hashValue;
	    }

	 
	 @GET
	 @Path("/authenticate/{userId}/{password}")	
	 @Produces("application/json")
	 public String authenticate(@PathParam("userId") String userID, @PathParam("password") String password) {
	 	
	 /*
	  * This method authenticates the user
	  */

	 	 String msg ="false";

	 	 try {
	 		 
	 	 
	 	 Mongo mongo = new Mongo("localhost", 27017);
	 	 DB db = mongo.getDB("beershift");
	 	 DBCollection collection = db.getCollection("users");
	 	
	 	 BasicDBObject searchQuery = new BasicDBObject();
	 	 searchQuery.put("username", userID);

	 	 BasicDBObject keys = new BasicDBObject();
	 	 keys.put("password", 1);

	 	 
	 	 DBCursor cursor = collection.find(searchQuery,keys);
	 	 
	 	 while (cursor.hasNext()) {
	 	// msg += cursor.next();
	 	 BasicDBObject obj = (BasicDBObject) cursor.next();
	     String result="";
		result+= obj.getString("password");
		if(result.equals(encryptPassword(password,"SHA-1","UTF-8")))
		{
			msg="true";
		}

	 	 }
	 	
	 	 } catch (UnknownHostException e) {
	 	 e.printStackTrace();
	 	 } catch (MongoException e) {
	 	 e.printStackTrace();
	 	 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	 
	 	 return msg;
	 	
	 	 }
	 	
	 
	 
@GET
@Path("/insert/{userId}/{beer}/{date}")	
public String insert(@PathParam("userId") String userID, @PathParam("beer") String beer, @PathParam("date") String date) {
	
/*	Takes in userID and the beer as parameter and inserts into
 * 	MongoDB with the current Time 
 */
	
	 try {

	 Mongo mongo = new Mongo("localhost", 27017);
	 DB db = mongo.getDB("beershift");
	 DBCollection collection = db.getCollection("drank");
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
	 
	 return "Record Inserted";
	 

	 }
		 
@GET
@Path("/firehose")
@Produces("application/json")
public String firehose() {
	
/*
 * 	Displays all beers drank by all user
 */

	 String msg ="[";

	 try {
		 
	 
	 Mongo mongo = new Mongo("localhost", 27017);
	 DB db = mongo.getDB("beershift");
	 DBCollection collection = db.getCollection("drank");
	 
	 BasicDBObject sortOrder = new BasicDBObject();	 
	 sortOrder.put("date", -1);
	 
	 DBCursor cursor = collection.find().limit(50).sort(sortOrder);
	 
	 if (cursor == null)
		 return msg = "null";
	 
	 while (cursor.hasNext()) {
	 msg += cursor.next()+",";
	 }
	
	 } catch (UnknownHostException e) {
	 e.printStackTrace();
	 } catch (MongoException e) {
	 e.printStackTrace();
	 }
	 
	 msg = msg.substring(0, msg.length() -1);
	 msg += "]"; 
	
	 return msg;
	
	 }


@GET
@Path("/userbeers/{name}/openshift")
@Produces("application/json")
public String user_beer(@PathParam("name") String userID)
{
	
/*
 * 	Displays all the beers drank by user
 */

	 String msg ="[";

	 try {
		 
	 
	 Mongo mongo = new Mongo("localhost", 27017);
	 DB db = mongo.getDB("beershift");
	 DBCollection collection = db.getCollection("drank");
	 
	 BasicDBObject sortOrder = new BasicDBObject();	 
	 sortOrder.put("when", -1);
	
	 BasicDBObject searchQuery = new BasicDBObject();	 
	 searchQuery.put("username", userID);
	 
	 DBCursor cursor = collection.find(searchQuery).sort(sortOrder);
	 
	 if (cursor == null)
		 return msg = "null";
	 
	 while (cursor.hasNext()) {
	 msg += cursor.next()+",";
	 }
	
	 } catch (UnknownHostException e) {
	 e.printStackTrace();
	 } catch (MongoException e) {
	 e.printStackTrace();
	 }
	 
	 msg = msg.substring(0, msg.length() -1);
	 msg += "]"; 
	
	 return msg;
	
	 }
	

@GET
@Path("/beers/name/{name}/")
@Produces("application/json")
public String search_beer(@PathParam("name") String name)
{

/*
  *     Displays all the beers which match the search Input
  */

      String RESTCall ="";
      String res ="";
      String result="";

      try {


         RESTCall = pint_url + "?key=" +api_key + "&q=" + name + "&type=" +type;

          	 URL url = new URL(RESTCall);

             URLConnection conn = url.openConnection();

             BufferedReader in = new BufferedReader(new
             InputStreamReader(conn.getInputStream()));
             System.out.println("1");

             while ((res = in.readLine()) != null) {

             result += res;

      }
     
     } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
     }

      return result;

      }


}