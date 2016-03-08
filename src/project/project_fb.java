/**
 * 
 * @author 
 * 
 * akash aggarwal 2014008
 * rohan juneja 2014156
 *
 */

package project;

import static java.lang.System.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.types.Post;

/**
 * Servlet implementation class project_fb
 */
@WebServlet("/project_fb")
public class project_fb extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public project_fb() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		HashMap<String, Integer> map3 = new HashMap<String, Integer>();
		ArrayList<String> predictList = new ArrayList<String>();
		ArrayList<String> rmwords = new ArrayList<String>();
		Random randomGenerator = new Random();
		rmwords.add("the");
		rmwords.add("of");
		rmwords.add("and");
		rmwords.add("to");
		rmwords.add("a");
		rmwords.add("in");
		rmwords.add("that");
		rmwords.add("is");
		rmwords.add("was");
		rmwords.add("he");
		rmwords.add("for");
		rmwords.add("it");
		rmwords.add("with");
		rmwords.add("as");
		rmwords.add("his");
		rmwords.add("on");
		rmwords.add("be");
		rmwords.add("at");
		rmwords.add("by");
		rmwords.add("I");
		Integer i = 0;
		@SuppressWarnings("deprecation")
		DefaultFacebookClient facebookClient = new DefaultFacebookClient(request.getParameter("access_token"));

		out.println("Starting BATCH \n");

		// Building Batch Request to send to Facebook
		out.println("Creating BATCH \n");

		Connection<Post> myfeed = facebookClient.fetchConnection("me/feed", Post.class);
		for (List<Post> myFeedConnectionPage : myfeed)
			for (Post post : myFeedConnectionPage) {
				Post obj = post;
				try {
					 out.println("Post: " + obj.getMessage());
					 out.println("Likes: " + obj.getLikes().getData().size());
					 out.print("\n");
					map.put(obj.getMessage(), obj.getLikes().getData().size());
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		int predictpost = (map.size() - 1) / 5;
		int count = 0;
		int meandiff = 0;
		int curr = 0;;
		
		for(Map.Entry<String, Integer> entry : map.entrySet()) {
			if(count == predictpost + 1)
				curr = entry.getValue();
			if(count > predictpost) {
				if(entry.getKey() != null) {
					String[] words = entry.getKey().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
					meandiff += entry.getValue() - curr;
					curr = entry.getValue();
					for(String itn : words) {
						if(!rmwords.contains(itn)) {
						if(!map2.containsKey(itn)) {
							map2.put(itn, entry.getValue());
							map3.put(itn, 1);
						}
						else {
							map2.put(itn, map2.get(itn) + entry.getValue());
							map3.put(itn, map3.get(itn) + 1);
						}
						}
					}
				}
			}
			count++;
		}
		
		meandiff = meandiff / (count - predictpost - 1);
		
		count = 0;
		int sum, run;
		
		for(Map.Entry<String, Integer> entry : map.entrySet()) {
			sum = 0;
			run = 0;
			if(count <= predictpost) {
				if(entry.getKey() != null) {
					String[] words = entry.getKey().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
					for(String itn : words) {
						if(!rmwords.contains(itn))
							predictList.add(itn);
					}
					for(String wrd : predictList) {
						if(map2.containsKey(wrd)) {
							sum += map2.get(wrd)/map3.get(wrd);
							run++;
						}
					}
					if(run != 0)
						System.out.println("Number of \"likes\" Count Prediction : " + sum/run);
					else {
						for(Map.Entry<String, Integer> entry1 : map.entrySet()) {
							if (run > predictpost)
								sum += map.get(entry1.getKey()); 
						
							if (run >= predictpost + 4) {
								run++;
								break;
							}	
							
							run++;
						}
						System.out.println("Number of \"likes\" Count Prediction : " + ((sum/(run-predictpost-1)) + randomGenerator.nextInt(meandiff + 1)));
					}
				}
			}
			count++;
			predictList.clear();
		}
		
			
	}
		// BatchRequest meRequest = new BatchRequestBuilder("me").build();
		// BatchRequest meFriendRequest = new
		// BatchRequestBuilder("me/friends").build();
		// BatchRequest meLikeRequest = new
		// BatchRequestBuilder("me/likes").parameters(Parameter.with("limit",
		// 100)).build();
		//
		//
		// //Creating POST Request - Not working yet - moved to GET
		// out.println("Posting Request \n");
		// BatchRequest postRequest = new
		// BatchRequestBuilder("me").method("GET").body(Parameter.with("message",
		// "Info!!!")).build();
		//
		// //Executing BATCH Request
		// out.println("Complete Batch Response \n");
		// List<BatchResponse> batchResponses =
		// facebookClient.executeBatch(meRequest, meFriendRequest,
		// meLikeRequest, postRequest);
		//
		//
		// //Got Response we can use this information to process further.
		// out.println("\n Response \n");
		// BatchResponse meResponse = batchResponses.get(0);
		// BatchResponse meFriendResponse = batchResponses.get(1);
		// BatchResponse meLikeResponse = batchResponses.get(2);
		// BatchResponse postResponse = batchResponses.get(3);
		//
		// out.println("\n *********** Individual Reponse ************* \n");
		//
		// out.println("\n meResponse \n");
		// out.println(meResponse.getBody());
		//
		// out.println("\n meFriendResponse \n");
		// out.println(meFriendResponse.getBody());
		//
		// out.println("\n meLikeResponse Getting 5 (LIMITED) \n");
		// out.println(meLikeResponse.getBody());
		//
		// out.println("\n postResponse \n");
		// out.println(postResponse.getBody());

}
