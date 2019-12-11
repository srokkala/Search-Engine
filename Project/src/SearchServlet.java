import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class is used to design the front end part of the search engine
 *
 * @author Steven Rokkala
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SearchServlet extends HttpServlet {

	/**
	 *  Default Serial VersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Stemming Algorithm
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/** My Search Engine "Brand Name" */
	private static final String TITLE = "Speed Search";

	/** A Queue of Links */
	private ConcurrentLinkedQueue<String> outputQueue;

	/**
	 * The crawler of the web.
	 */
	private WebCrawler webCrawler;

	/**
	 * The number of Results from the Search
	 */
	private int searchResults = 0;

	/**
	 * The time it took for search 
	 */
	private long seconds;
	
	/**
	 * A List of Search Entries (History)
	 */
	ArrayList<String> queryList = new ArrayList<String>();

	/**
	 * Constructor of the search servlet.
	 *
	 * @param querymaker The interface to build a query
	 * @param threadSafe Our thread safe index
	 * @param webCrawler Crawls the web
	 */
	public SearchServlet(QueryMakerInterface querymaker, MultithreadedInvertedIndex threadSafe, WebCrawler webCrawler) {
		super();
		this.webCrawler = webCrawler;
		outputQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();

		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<body style=\"background-color:black;\">");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", TITLE);
		out.printf(
				"	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.4/css/bulma.min.css\">%n");
		out.printf(
				"	<script defer src=\"https://use.fontawesome.com/releases/v5.8.1/js/all.js\" integrity=\"sha384-g5uSoOSBd7KkhAMlnQILrecXvzst9TdC09/VM+pjDTCM+1il8RHz5fKANTFFb+gQ\" crossorigin=\"anonymous\"></script>%n");

		out.printf("</head>%n");
		out.printf("%n");
		out.printf("<body>%n");
		out.printf("	  <div class=\"hero-body\">%n");
		out.printf("	    <div class=\"center container\" style=\"text-align:center\">%n");
		out.printf("<figure class=\"image is-96x96\">\n"
				+ "  <img  src=\"https://cdn.pixabay.com/photo/2018/09/06/10/02/car-icon-3657902_1280.png\">\n"
				+ "</figure>");
		out.printf("	      <h1 class=\"title\">%n");
		out.printf(TITLE + "%n");
		out.printf("	      </h1>%n");
		out.printf("<p1>Fast and Steady Wins The Race</p1>");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("      <h2 class=\"subtitle\">\n");
		out.printf("      </h2>");
		out.printf("	  </div>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
		out.printf("<h2 class=\"subtitle\">\n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("%n");
		out.printf("<input type = \"checkbox\" name = \"exact\" id = \"exact\" > Exact");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf(
				"						<input class=\"input is-rounded\" type=\"text\" name=\"%s\" placeholder=\"You ask, I'll Find it\">%n",
				"search");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-search\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");

		out.printf("			<div class=\"field is-grouped is-grouped-centered\">%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-link\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-rocket\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("Search" + "%n");
		out.printf("					</button>%n");
		out.printf("			    <button class=\"button is-link \" name=\"lucky\"  id = \"lucky\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-hand-holding-usd\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("I'm feeling lucky%n");
		out.printf("					</button>%n");
		out.printf("%n");
		out.printf("<div class=\"dropdown\">");
		out.printf("<div class=\"dropdown-trigger\">");
		out.printf("<button class=\"button is-link\" aria-haspopup=\"true\" aria-controls=\"dropdown-menu3\">");
		out.printf("Search History &nbsp;%n");
		out.printf("<span class=\"icon is-small is-left\">%n");
		out.printf("<i class=\"fas fa-database\"></i></i>");
		out.printf("</span>");
		out.printf("<span class=\"icon is-small is-right\">");
		out.printf("<i class=\"fas fa-angle-down\" aria-hidden=\"true\"></i>");
		out.printf("</span>");
		out.printf("</button>");
		out.printf("</div>");
		out.printf("<div class=\"dropdown-menu\" id=\"dropdown-menu3\" role=\"menu\">");
		out.printf("<div class=\"dropdown-content\">");
		if (!queryList.isEmpty()) {
			for (String query : queryList) {
				out.printf("				<div class=\"box\">%n");
				out.printf(query);
				out.printf("				</div>%n");
				out.printf("%n");
			}
		}
		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("</div>");
		out.printf("</div>");
		out.printf("			  </div>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		if (!outputQueue.isEmpty()) {
			for (String message : outputQueue) {
				out.printf("				<div class=\"box\">%n");
				out.printf(message);
				out.printf("				</div>%n");
				out.printf("%n");
			}
		}
		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("	<footer class=\"footer\" style=\"background-color:black;\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("			<i class=\"fas fa-code-branch\"></i>%n");
		out.printf("	      &nbsp;This request was handled by thread %s.%n", Thread.currentThread().getName());
		out.printf("	    </p>%n");
		out.printf("	      <p>");
		out.printf("	<p1> <i class=\"fas fa-list\"></i> &nbsp; Results Found: " + searchResults + "</p1>%n");
		out.printf("	      </p>%n");
		out.printf("	    <p><i class=\"fas fa-clock fa-spin\"></i>%n");
		out.printf(seconds + " ms.</p1>%n");
		out.printf("	    </p>%n");
		out.printf("%n");
		out.printf("	    <p>%n");
		out.printf("					<i class=\"fas fa-calendar-alt\"></i>%n");
		out.printf("					&nbsp;Last Visited %s%n", getDate());
		out.printf("	    </p>%n");
		out.printf("<br />");
		out.printf("	  </div>");
		out.printf("	</footer>");
		out.printf("</body>");
		out.printf("</html>");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		long st = System.nanoTime();
		String message = request.getParameter("search");
		String checkBox = request.getParameter("exact");
		String lucky = request.getParameter("lucky");

		boolean luck = false;
		if (lucky != null) {
			luck = true;
		}
		if (luck) {
			System.out.println("Hmm. On the right track..");
		}

		if (message == null) {
			message = "";
		}


	 //Using Apache Commons Text to protect from attacks
		message = StringEscapeUtils.escapeHtml4(message);

		response.getWriter();

		boolean exact;
		if (checkBox != null && checkBox.contains("on")) {
			exact = true;
		} else {
			exact = false;
		}

		String formatString = null;

		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		
		for (String part : message.split(" ")) {
			if (part == " ") {
				part = "";
			}
			queryList.add((stemmer.stem(part.toLowerCase())).toString());
		}
		
		//Attempting to keep track of history 
		System.out.println(queryList);
		
		
		List<InvertedIndex.SearchResult> results = this.webCrawler.search(queryList, exact);

		if (results == null || results.isEmpty()) {
			searchResults = 0;
			outputQueue.clear();
			formatString = String.format("<i></i> %s <i></i>%n" + "<p\"></p>%n",
					"The String: \"" + request.getParameter("search") + "\" Does not exist", getDate());
			outputQueue.add(formatString);
		} else {
			searchResults = 0;
			outputQueue.clear();
			for (MultithreadedInvertedIndex.SearchResult result : results) {
				formatString = String.format(
						"<a href=\"%s\">%s</a>" + "<p class=\"has-text-grey is-size-7 has-text-right\">%s</p>%n",
						result.getPlace(), result.getPlace(), getDate());
				searchResults++;
				outputQueue.add(formatString);
			}
		}


		seconds = (System.nanoTime() - st) / 1000000;
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
		response.flushBuffer();
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}