package offline;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoDatabase;
import db.mongodb.MongoDBUtil;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Prediction {
	// potential good results are AWLVQ1NSU3LDS
	// A1GO6VJZN0UDLN
	private static final String USER_ID = "AWLVQ1NSU3LDS";
	private static final String COLLECTION_NAME = "ratings";
	private static final String USER_COLUMN = "user";
	private static final String ITEM_COLUMN = "item";
	private static final String RATING_COLUMN = "rating";
	
	public static void main(String [] args) {
		// Init
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
		
		// Get purchase records of target user
		List<String> previousItems = new ArrayList<>();
		List<Double> previousRatings = new ArrayList<>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME).find(
				eq(USER_COLUMN, USER_ID));//find equal to USER_ID in USER_COLUMN

		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				previousItems.add(document.getString(ITEM_COLUMN));
				previousRatings.add(document.getDouble(RATING_COLUMN));
			}
		});

                             /**
	              String map = “function() 
			{ if (this.item == "0634029363" && this.rating == 2) 
			{ emit(this.user, 1); }
			if (this.item == "B000TZTPQ6" && this.rating == 5)
			{ emit(this.user, 1); }
			if (this.item == "B001QE994M" && this.rating == 4)
			{ emit(this.user, 1); }
			if (this.item == “B001QE997E" && this.rating == 5) 
			{ emit(this.user, 1); }}”
		 */

		// Construct mapper function
		StringBuilder sb = new StringBuilder();
		sb.append("function() {");
		
		for (int i = 0; i < previousItems.size(); i ++) {
			String item = previousItems.get(i);
			Double rating = previousRatings.get(i);
			sb.append("if (this.item == \"");
			sb.append(item);
			sb.append("\" && this.rating == ");
			sb.append(rating);
			sb.append(" ){ emit(this.user, 1); }");
		}
		sb.append("}");
		String map = sb.toString();
		// Construct a reducer function
		String reduce = "function(key, values) {return Array.sum(values)} ";//js, reducer function 
		
		// MapReduce
		MapReduceIterable<Document> results = db.getCollection(COLLECTION_NAME)
				.mapReduce(map, reduce);	
		// Need a sorting here
		List<User> similarUsers = new ArrayList<>();
		results.forEach(new Block<Document>() {//overrdie appy function of block
			@Override
			public void apply(final Document document) {
				String id = document.getString("_id");
				Double value = document.getDouble("value");
				if (!id.equals(USER_ID)) {
					similarUsers.add(new User(id, value));
					
				}
			}
		});
		//printList(similarUsers);
		//System.out.println("\n\n\n");
		Collections.sort(similarUsers);
		printList(similarUsers);
		
		// Get similar users' previous records order by similarity
		Set<String> products = new HashSet<>();
		for (User user : similarUsers) {
			String id = user.getId();
			iterable = db.getCollection(COLLECTION_NAME).find(
					new Document(USER_COLUMN, id));

			iterable.forEach(new Block<Document>() {
				@Override
				public void apply(final Document document) {
					String item = document.getString(ITEM_COLUMN);
					if (!previousItems.contains(item) && products.size() < 5
){
						products.add(document.getString(ITEM_COLUMN));
					}
				}
			});
			if (products.size() >= 5) {
				break;
			}
		}
		
		for (String product : products) {
			System.out.println("Recommended product: " + product);
		}
		
		mongoClient.close();		
	}
	
	private static void printList(List<User> similarUsers) {
		for (User user: similarUsers) {
			System.out.println(user.getId() + "," + user.getValue());
		}
	}
}
