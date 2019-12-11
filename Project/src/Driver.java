import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Steven Rokkala
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program 
	 *
	 */
	public static void main(String[] args){
		/* Store initial start time */

		// initial thread count
		int threads = 1;

		Instant start = Instant.now();

		ArgumentParser argumentParser = new ArgumentParser(args);

		InvertedIndex invertedIndex;

		InvertedBuilder builder;

		QueryMakerInterface maker;
		
		WebCrawler crawler;
		
		SearchServlet servlet;

		if (argumentParser.hasFlag("-threads") || argumentParser.hasFlag("-url") || argumentParser.hasFlag("-port")) {
			try {
				threads = Integer.parseInt(argumentParser.getString("-threads"));
				if (threads == 0) {
					threads = 5;
				}
			} catch (Exception e) {
				System.out.println("Setting Thread Count to 5");
				threads = 5;
			}

			MultithreadedInvertedIndex threadSafe = new MultithreadedInvertedIndex();
			invertedIndex = threadSafe;
			builder = new MultithreadedInvertedBuilder(threadSafe, threads);
			maker = new MultithreadedQueryMaker(threadSafe, threads);
			
			if(argumentParser.hasFlag("-limit"))
			{
				crawler = new WebCrawler(threadSafe,threads,Integer.parseInt(argumentParser.getString("-limit")));
			}
			else {
				crawler = new WebCrawler(threadSafe, threads, 50);
			}
			
			if(argumentParser.hasFlag("-url")) {
				try {
					URL seed = new URL(argumentParser.getString("-url"));
					crawler.build(seed);
				}
				catch(Exception e) {
					System.out.println("Error occured creating the URL");
				}
			}
			
			if(argumentParser.hasFlag("-port"))
			{
				servlet = new SearchServlet(maker, threadSafe, crawler);
				int port;
				try {
					port = Integer.parseInt(argumentParser.getString("-port"));
					System.out.println(port);
				}
				catch(Exception e) {
					System.out.println("Error Occured While Getting String");
					port = 8080;
				}
				
				try {
					ServletContextHandler servletContextHandler = null;

					servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
					servletContextHandler.setContextPath("/");

					DefaultHandler defaultHandler = new DefaultHandler();
					defaultHandler.setServeIcon(true);

					ContextHandler contextHandler = new ContextHandler("/favicon.ico");
					contextHandler.setHandler(defaultHandler);

					ServletHolder servletHolder = new ServletHolder(servlet);

					ServletHandler servletHandler = new ServletHandler();
					servletHandler.addServletWithMapping(servletHolder, "/");

					Server server = new Server(port);
					server.setHandler(servletHandler);
					server.start();
					server.join();

				} catch (Exception e) {
					System.err.println("Jetty server Did not work");
				}
			}
			
		} 
		
		else {
			invertedIndex = new InvertedIndex();
			maker = new QueryMaker(invertedIndex);
			builder = new InvertedBuilder(invertedIndex);

		}

		if (argumentParser.hasFlag("-path") && argumentParser.getPath("-path") != null) {
			Path path = argumentParser.getPath("-path");
			try {
				builder.build(path);
			} catch (IOException e) {
				System.out.println("Path can not be traversed: " + path.toString());
			}
		}

		if (argumentParser.hasFlag("-index")) {
			Path path = argumentParser.getPath("-index", Path.of("index.json"));
			try {
				invertedIndex.printIndex(path);
			} catch (IOException e) {
				System.out.println("There was an issue while writing inverted index to file: " + path.toString());
			}
		}

		if (argumentParser.hasFlag("-counts")) {
			Path path = argumentParser.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(invertedIndex.getCount(), path);
			} catch (IOException e) {
				System.out.println("There was an issue while writing counts info to file: " + path.toString());
			}
		}

		if (argumentParser.hasFlag("-query") && argumentParser.getPath("-query") != null) {
			Path queryPath = argumentParser.getPath("-query");
			try {
				maker.queryParser(queryPath, argumentParser.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("There was an issue while reading the query file: " + queryPath.toString());
			} catch (Exception r) {
				System.out.println("There was an issue while doing things with file: " + queryPath.toString());
			}
		}

		if (argumentParser.hasFlag("-results")) {
			Path path = argumentParser.getPath("-results", Path.of("results.json"));
			try {
				maker.queryWriter(path);
			} catch (IOException e) {
				System.out.println("Something went wrong while writing search results to path: " + path);
			}
		}

		/* Calculate time elapsed and output */
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
