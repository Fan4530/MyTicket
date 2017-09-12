package rpc;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.ExternalAPI;
import external.ExternalAPIFactory;


/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private DBConnection conn = DBConnectionFactory.getDBConnection();

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SearchItem.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		
			throws ServletException, IOException {
		

//		HttpSession session = request.getSession();
//		if (session.getAttribute("user") == null) {
//			response.setStatus(403);
//			return;
//		}


		String userId = "1111";
	//	String userId = session.getAttribute("user").toString();//8/10
	//	String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));

		// Term can be empty or null.
		String term = request.getParameter("term");
		LOGGER.log(Level.INFO, "lat:" + lat + ",lon:" + lon);

		//how to input Username ?  what is term?????????
		
		List<Item> items = conn.searchItems(userId, lat, lon, term);//database
		List<JSONObject> list = new ArrayList<>();
		try {
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray array = new JSONArray(list);
		RpcHelper.writeJsonArray(response, array);
	}
//
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
////		//rpc: request and response
//		// TODO Auto-generated method stub
//		double lat = Double.parseDouble(request.getParameter("lat"));
//		double lon = Double.parseDouble(request.getParameter("lon"));
//		// Term can be empty or null.
//		String term = request.getParameter("term");
//		
//		// ExternalAPIFactory is a class, there is static method called getExternalAPI() and getExternalAPI(String name)
//		// used for get a new object. Say ExternalAPIFactory.getExternalAPI("ticket"), it will create a ticketAPI and return to externalAPI
//		ExternalAPI externalAPI = ExternalAPIFactory.getExternalAPI();
//		List<Item> items = externalAPI.search(lat, lon, term);
//		List<JSONObject> list = new ArrayList<>();
//		try {
//			for (Item item : items) {
//				// Add a thin version of item object
//				JSONObject obj = item.toJSONObject();
//				list.add(obj);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		JSONArray array = new JSONArray(list);
//		RpcHelper.writeJsonArray(response, array);
//
//	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
//branch test: for githubTest branch
