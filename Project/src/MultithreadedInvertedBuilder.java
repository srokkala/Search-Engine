import java.io.IOException;
import java.nio.file.Path;

/**
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class MultithreadedInvertedBuilder extends InvertedBuilder {

	/**
	 * Creating a usable object of @class MultiThreadedInvertedIndex
	 */
	private final MultithreadedInvertedIndex inverted;

	/**
	 * Constructor Method
	 * 
	 * @param inverted The thread safe inverted index we will add to
	 */
	public MultithreadedInvertedBuilder(MultithreadedInvertedIndex inverted) {
		super(inverted);
		this.inverted = inverted;
	}

	/**
	 * Overrides function in @class InvertedBuilder This method creates paths during
	 * directory traversal
	 * 
	 * @param path    The initial traversal point
	 * @param threads Passing through the number of threads
	 * @throws IOException
	 */
	@Override
	public void build(Path path, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		for (Path currentPath : getTextFiles(path)) {
			if (isText(currentPath)) {
				queue.execute(new BuildHelper(currentPath, this.inverted));

			}
		}
		try {
			queue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
		queue.shutdown();
	}

	/**
	 * This function is used to add the path of the inverted index and overrides the
	 * run function from the @interface Runnable
	 * 
	 * @author CS 212 Software Development
	 * @author University of San Francisco
	 * @version Fall 2019
	 */
	private static class BuildHelper implements Runnable {

		/**
		 * New path object that will take the path from the build function from @class
		 * MultiThreadedInvertedBuilder This will be instantiated in the constructor and
		 * used in the run function
		 */
		private final Path path;

		/**
		 * Creating a usable object of @class MultiThreadedInvertedIndex
		 */
		private final MultithreadedInvertedIndex inverted;

		/**
		 * Constructor Method
		 * 
		 * @param path     The initial traversal point passed in
		 * @param inverted The threadsafe object we created in @class
		 *                 MultiThreadedInvertedBuilder
		 */
		public BuildHelper(Path path, MultithreadedInvertedIndex inverted) {
			this.path = path;
			this.inverted = inverted;
		}

		/**
		 * This method tries to add the path of the thread safe invertedindex results in
		 * exception if it failed
		 */
		@Override
		public void run() {
			try {
				addPath(path, inverted);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
