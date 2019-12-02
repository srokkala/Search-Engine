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
	 * Number of Threads
	 */
	private final int threads;

	/**
	 * Constructor Method
	 * 
	 * @param inverted The thread safe inverted index we will add to
	 * @param threads  Passing in the number of threads
	 */
	public MultithreadedInvertedBuilder(MultithreadedInvertedIndex inverted, int threads) {
		super(inverted);
		this.inverted = inverted;
		this.threads = threads;

	}

	/**
	 * Overrides function in @class InvertedBuilder This method creates paths during
	 * directory traversal
	 * 
	 * @param path The initial traversal point
	 * @throws IOException
	 */
	@Override
	public void build(Path path) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		for (Path currentPath : getTextFiles(path)) {
			if (isText(currentPath)) { // TODO Remove
				queue.execute(new BuildHelper(currentPath));

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
	private class BuildHelper implements Runnable {

		/**
		 * New path object that will take the path from the build function from @class
		 * MultiThreadedInvertedBuilder This will be instantiated in the constructor and
		 * used in the run function
		 */
		private final Path path;

		/**
		 * Constructor Method
		 * 
		 * @param path The initial traversal point passed in
		 */
		public BuildHelper(Path path) {
			this.path = path;

		}

		/**
		 * This method tries to add the path of the thread safe invertedindex results in
		 * exception if it failed
		 */
		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				addPath(path, local);
				inverted.addAll(local);

			} catch (IOException e) {
				System.out.println("Warning: Adding To Inverted Index Failed");
			}

		}
	}

}
