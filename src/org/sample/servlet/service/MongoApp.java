/*
 * 
 * 
 * Sample MongoDB Backend to insert data into database
 * and retrieve on the basis of username provided
 * 
 * Author					Modified
 * Hilay Khatri				05-31-2012
 * 
 * 
 */

package org.sample.servlet.service;

import java.net.UnknownHostException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoApp {

 public void insert(String userID, String password) {

 try {

 Mongo mongo = new Mongo("localhost", 27017);
 DB db = mongo.getDB("beershift");
 DBCollection collection = db.getCollection("data");
 BasicDBObject document = new BasicDBObject();

 document.put("username", userID);
 document.put("beer", password);
 document.put("date", new Date().toString());

 collection.insert(document);
 
 } catch (UnknownHostException e) {
 e.printStackTrace();
 } catch (MongoException e) {
 e.printStackTrace();
 }

 }
 
 
 public String display(String userID) {
	 
	 String msg ="";

	 try {
		 
	 
	 Mongo mongo = new Mongo("localhost", 27017);
	 DB db = mongo.getDB("beershift");
	 DBCollection collection = db.getCollection("data");
	 BasicDBObject document = new BasicDBObject();

	 BasicDBObject searchQuery = new BasicDBObject();
	 searchQuery.put("username", userID);
	 DBCursor cursor = collection.find(searchQuery);
	 
	 while (cursor.hasNext()) {
	 msg += cursor.next().toString();
	 }

	 } catch (UnknownHostException e) {
	 e.printStackTrace();
	 } catch (MongoException e) {
	 e.printStackTrace();
	 }
	 
	 return msg;

	 }

}