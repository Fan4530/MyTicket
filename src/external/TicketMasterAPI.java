
package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import entity.Item;
import entity.Item.ItemBuilder;
//you should also import static class 

public class TicketMasterAPI  implements ExternalAPI{
	private static final String API_HOST = "app.ticketmaster.com";
	private static final String SEARCH_PATH = "/discovery/v2/events.json";
	private static final String DEFAULT_TERM = "";  // no restriction
	private static final String API_KEY = "Jnt6QHEgL77JF2GP093dwJapLSSbAhV9";
	  private static final String DEFAULT_PIPELINE = "ticketmaster";

	/**
	 * Creates and sends a request to the TicketMaster API by term and location.
	 */
	@Override

	public  List<Item> search(double lat, double lon, String term) {
		String url = "http://" + API_HOST + SEARCH_PATH;
		String latlong = lat + "," + lon;//input number 
		if (term == null) {
			term = DEFAULT_TERM;
		}
		term = urlEncodeHelper(term);
		String query = String.format("apikey=%s&latlong=%s&keyword=%s", API_KEY, latlong, term);
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();//return a URL connection
			connection.setRequestMethod("GET");//get method, connection is a instance, type is HttpURLConnection,  and was newed by URL("..").openConnection() 
			//complete request
			// if connection sucess 
			
			// block, until I need a response  
			int responseCode = connection.getResponseCode();//call get Response, response code 200 404 or 500
			System.out.println("\nSending 'GET' request to URL : " + url + "?" + query);
			System.out.println("Response Code : " + responseCode);
			//print out response code 
			/**
			 * the content that printed out
			 * Sending 'GET' request to URL : http://app.ticketmaster.com/discovery/v2/events.json?apikey=Jnt6QHEgL77JF2GP093dwJapLSSbAhV9&latlong=37.38,-122.08&keyword=
				Response Code : 200
			 */
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));// get the message 
			String inputLine;
			StringBuffer response = new StringBuffer();
			//read the inputLine to the response StringBuffer
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				//System.out.println(inputLine);
			}
			//there are some useless information, and we want to simplify them 

			in.close();
			//debug
			//System.out.println(response.toString());
			// Extract events array only.
			JSONObject responseJson = new JSONObject(response.toString());
			JSONObject embedded = (JSONObject) responseJson.get("_embedded");
			JSONArray events = (JSONArray) embedded.get("events");

			return getItemList(events);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	private String urlEncodeHelper(String term) {
		try {
			term = java.net.URLEncoder.encode(term, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return term;
	}
	//only print
	private void queryAPI(double lat, double lon) {
		List<Item> itemList = search(lat, lon, null);
		try {
			for (Item item : itemList) {
				JSONObject jsonObject = item.toJSONObject();
				//System.out.println(jsonObject);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper methods
	 */
	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();

		for (int i = 0; i < events.length(); i++) {
			//should cehck null first 
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			builder.setItemId(getStringFieldOrNull(event, "id"));
			builder.setName(getStringFieldOrNull(event, "name"));
			builder.setDescription(getDescription(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			builder.setUrl(getStringFieldOrNull(event, "url"));
			JSONObject venue = getVenue(event);
			if (venue != null) {
				if (!venue.isNull("address")) {
					JSONObject address = venue.getJSONObject("address");
					StringBuilder sb = new StringBuilder();
					if (!address.isNull("line1")) {
						sb.append(address.getString("line1"));
					}
					if (!address.isNull("line2")) {
						sb.append(address.getString("line2"));
					}
					if (!address.isNull("line3")) {
						sb.append(address.getString("line3"));
					}
					builder.setAddress(sb.toString());
				}
				if (!venue.isNull("city")) {
					JSONObject city = venue.getJSONObject("city");
					builder.setCity(getStringFieldOrNull(city, "name"));
				}
				if (!venue.isNull("country")) {
					JSONObject country = venue.getJSONObject("country");
					builder.setCountry(getStringFieldOrNull(country, "name"));
				}
				if (!venue.isNull("state")) {
					JSONObject state = venue.getJSONObject("state");
					builder.setState(getStringFieldOrNull(state, "name"));
				}
				builder.setZipcode(getStringFieldOrNull(venue, "postalCode"));
				if (!venue.isNull("location")) {
					JSONObject location = venue.getJSONObject("location");
					builder.setLatitude(getNumericFieldOrNull(location, "latitude"));
					builder.setLongitude(getNumericFieldOrNull(location, "longitude"));
				}
			}

			// Uses this builder pattern we can freely add fields.
			Item item = builder.build();
			itemList.add(item);
		}

		return itemList;
	}

	private JSONObject getVenue(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				if (venues.length() >= 1) {
					return venues.getJSONObject(0);
				}
			}
		}
		return null;
	}

	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray imagesArray = event.getJSONArray("images");
			if (imagesArray.length() >= 1) {
				return getStringFieldOrNull(imagesArray.getJSONObject(0), "url" );
			}
		}
		return null;
	}

	private String getDescription(JSONObject event) throws JSONException {
		if (!event.isNull("description")) {
			return event.getString("description");
		} else if (!event.isNull("additionalInfo")) {
			return event.getString("additionalInfo");
		} else if (!event.isNull("info")) {
			return event.getString("info");
		} else if (!event.isNull("pleaseNote")) {
			return event.getString("pleaseNote");
		}
		return null;
	}

	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		JSONArray classifications = (JSONArray) event.get("classifications");
		for (int j = 0; j < classifications.length(); j++) {
			JSONObject classification = classifications.getJSONObject(j);
			JSONObject segment = classification.getJSONObject("segment");
			categories.add(segment.getString("name"));
		}
		return categories;
	}
	//check if it is null, if it is null, we will not call get function
	private String getStringFieldOrNull(JSONObject event, String field) throws JSONException {
		return event.isNull(field) ? null : event.getString(field);
	}

	private double getNumericFieldOrNull(JSONObject event, String field) throws JSONException {
		return event.isNull(field) ? 0.0 : event.getDouble(field);
	}

 
	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	
	
	//test
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		tmApi.queryAPI(37.38, -122.08);
	}
}
