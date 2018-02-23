package concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Create/return a single instance of a "Custom" thread pool.
 * <p>
 *  NOTE: This class implements the Singleton pattern to only allow one (custom)
 *        thread pool instance to be created.
 * </p>
 * <p>
 *  NOTE: This class implements the Abstract Factory pattern by letting the
 *        implementing class(es) create/return the appropriate type of thread pool.
 * </p>
 * 
 * @author TAYLOJ10
 */
public class ThreadPoolFactory {

	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("view");

	/**
	 * Minimum size (threads) of thread pool.
	 */
	private static short THREADPOOL_OPTION_POOL_CORE_SIZE = 4;
	public static short getTHREADPOOL_OPTION_POOL_CORE_SIZE() {
		return THREADPOOL_OPTION_POOL_CORE_SIZE;
	}
	public static void setTHREADPOOL_OPTION_POOL_CORE_SIZE(
			short tHREADPOOL_OPTION_POOL_CORE_SIZE) {
		THREADPOOL_OPTION_POOL_CORE_SIZE = tHREADPOOL_OPTION_POOL_CORE_SIZE;
	}

	/**
	 * Maximum size (threads) of thread pool.
	 */
	private static short THREADPOOL_OPTION_POOL_MAXIMUM_SIZE = 8;
	public static short getTHREADPOOL_OPTION_POOL_MAXIMUM_SIZE() {
		return THREADPOOL_OPTION_POOL_MAXIMUM_SIZE;
	}
	public static void setTHREADPOOL_OPTION_POOL_MAXIMUM_SIZE(
			short tHREADPOOL_OPTION_POOL_MAXIMUM_SIZE) {
		THREADPOOL_OPTION_POOL_MAXIMUM_SIZE = tHREADPOOL_OPTION_POOL_MAXIMUM_SIZE;
	}

	/**
	 * Maximum size (tasks) of (bounded) queue.
	 */
	private static short THREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE = 64;
	public static short getTHREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE() {
		return THREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE;
	}
	public static void setTHREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE(
			short tHREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE) {
		THREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE = tHREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE;
	}

	/**
	 * Order which items are pulled/processed from the thread pool queue.
	 * <p>
	 *  NOTE: Potential values...
	 *  <ul>
	 *   <li>TRUE == FIFO</li>
	 *   <li>FALSE == Random order</li>
	 *  </ul>
	 * </p>
	 */
	private static boolean THREADPOOL_OPTION_QUEUE_FAIR_ORDER = true;
	public static boolean isTHREADPOOL_OPTION_QUEUE_FAIR_ORDER() {
		return THREADPOOL_OPTION_QUEUE_FAIR_ORDER;
	}
	public static void setTHREADPOOL_OPTION_QUEUE_FAIR_ORDER(
			boolean tHREADPOOL_OPTION_QUEUE_FAIR_ORDER) {
		THREADPOOL_OPTION_QUEUE_FAIR_ORDER = tHREADPOOL_OPTION_QUEUE_FAIR_ORDER;
	}

	/**
	 * Core threads to prestart.
	 * <p>
	 *  NOTE: Potential values...
	 *  <ul>
	 *   <li>[0] == No</li>
	 *   <li>[1] == Core Thread</li>
	 *   <li>[2] == All Core Threads</li>
	 *  </ul>
	 */
	private static byte THREADPOOL_OPTION_PRESTART_CORE_THREADS = 2;
	public static byte getTHREADPOOL_OPTION_PRESTART_CORE_THREADS() {
		return THREADPOOL_OPTION_PRESTART_CORE_THREADS;
	}
	public static void setTHREADPOOL_OPTION_PRESTART_CORE_THREADS(
			byte tHREADPOOL_OPTION_PRESTART_CORE_THREADS) {
		THREADPOOL_OPTION_PRESTART_CORE_THREADS = tHREADPOOL_OPTION_PRESTART_CORE_THREADS;
	}


	/**
	 * Time (milliseconds) before inactive thread is removed from thread pool.
	 */
	private static int THREADPOOL_OPTION_KEEP_ALIVE_TIME = 60000;
	public static int getTHREADPOOL_OPTION_KEEP_ALIVE_TIME() {
		return THREADPOOL_OPTION_KEEP_ALIVE_TIME;
	}
	public static void setTHREADPOOL_OPTION_KEEP_ALIVE_TIME(
			int tHREADPOOL_OPTION_KEEP_ALIVE_TIME) {
		THREADPOOL_OPTION_KEEP_ALIVE_TIME = tHREADPOOL_OPTION_KEEP_ALIVE_TIME;
	}

	/**
	 * Type of thread pool queue to be created.
	 * <p>
	 *  NOTE: Potential values...
	 *  <ul>
	 *   <li>[0] == n/a</li>
	 *   <li>[1] == Synchronous</li>
	 *   <li>[2] == Unbounded</li>
	 *   <li>[3] == Bounded</li>
	 *  </ul>
	 * </p>
	 */
	private static byte THREADPOOL_OPTION_QUEUE_TYPE = 2;
	public static byte getTHREADPOOL_OPTION_QUEUE_TYPE() {
		return THREADPOOL_OPTION_QUEUE_TYPE;
	}
	public static void setTHREADPOOL_OPTION_QUEUE_TYPE(
			byte tHREADPOOL_OPTION_QUEUE_TYPE) {
		THREADPOOL_OPTION_QUEUE_TYPE = tHREADPOOL_OPTION_QUEUE_TYPE;
	}

	/**
	 * Type of thread pool rejection policy to be created.
	 * <p>
	 *  NOTE: Potential values...
	 *  <ul>
	 *   <li>[0] == n/a</li>
	 *   <li>[1] == Abort</li>
	 *   <li>[2] == Caller Runs</li>
	 *   <li>[3] == Discard</li>
	 *   <li>[4] == Discard Oldest</li>
	 *  </ul>
	 * </p>
	 */
	private static byte THREADPOOL_OPTION_REJECTION_POLICY = 1;
	public static byte getTHREADPOOL_OPTION_REJECTION_POLICY() {
		return THREADPOOL_OPTION_REJECTION_POLICY;
	}
	public static void setTHREADPOOL_OPTION_REJECTION_POLICY(
			byte tHREADPOOL_OPTION_REJECTION_POLICY) {
		THREADPOOL_OPTION_REJECTION_POLICY = tHREADPOOL_OPTION_REJECTION_POLICY;
	}

	/**
	 * Wait time (milliseconds) before killing thread(s) due to thread pool shutdown.
	 */
	public static int THREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD = 60000; 
	public static int getTHREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD() {
		return THREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD;
	}
	public static void setTHREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD(
			int tHREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD) {
		THREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD = tHREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD;
	}

	/**
	 * Contains the (eagerly loaded) single instance of the thread pool.
	 */
	private static ExecutorService threadPool = createThreadPool();

	/**
	 * Contains the (eagerly loaded) single instance of the thread pool factory class
	 * (Singleton pattern).
	 */
	private static ThreadPoolFactory uniqueInstance = new ThreadPoolFactory();

	/**
	 * Prevent direct instantiation of this class (Singleton pattern).
	 * <p>
	 *  NOTE: In order to create the thread pool instance eagerly, we are creating
	 *        the thread pool here.  Because this class implements the Singleton
	 *        pattern, that means that this constructor will only be called once,
	 *        which ensures that only one instance of the thread pool will ever
	 *        exist at any point in time.
	 * </p>
	 */
	private ThreadPoolFactory() {
	}

	/**
	 * Provide controlled instantiation of this class so that a maximum of one
	 * instance is ever created (Singleton pattern).
	 * 
	 * @return ThreadPoolCustom The only instance of this class.
	 */
	public static ThreadPoolFactory getInstance() {
		return uniqueInstance;
	}

	/**
	 * Create/return a "Custom" thread pool (Abstract Factory pattern)
	 * based on the configurable options.
	 * <p>
	 *  NOTE: This method MUST NOT be <code>static</code>, because not being
	 *        <code>static</code> forces clients to use the {@link #getInstance()}
	 *        method which, in turn, triggers the {@link #ThreadPoolFactory()}
	 *        constructor to be called (indirectly). The {@link #ThreadPoolFactory()}
	 *        constructor is (currently) where the thread pool gets initialized.
	 *        Changing this method to be <code>static</code> would allow the
	 *        client to potentially access the (not yet initialized) thread pool.
	 * </p>
	 * 
	 * @return ExecutorService containing a "Custom" thread pool.
	 */
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	/**
	 * Shutdown the current threadpool and create a new one (using current values).
	 */
	public void resetThreadPool() {
		if (threadPool != null) {
			threadPool.shutdown();
			while (!threadPool.isShutdown()) {
				try {
					threadPool.awaitTermination(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		threadPool = createThreadPool();
	}

	/**
	 * Helper method used to create a "Custom" thread pool
	 * based on the configurable options.
	 * 
	 * @return ExecutorService containing the resulting thread pool.
	 */
	private static ExecutorService createThreadPool() {
		ExecutorService executorService = null;
		try {
			// Create Thread Pool (based on configurable options)
			executorService = new ThreadPoolExecutor(
					THREADPOOL_OPTION_POOL_CORE_SIZE,
					THREADPOOL_OPTION_POOL_MAXIMUM_SIZE,
					THREADPOOL_OPTION_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
					createQueue(),
					createRejectionHandler()
					);
			// Prestart core threads
			int coreThreadsPrestarted = preStartCoreThreads(executorService);
			// Log the properties of the Thread Pool
			logger.debug("Thread Pool created..." +
					"\n+ poolType=Custom" +
					"\n+ poolCoreSize=" + THREADPOOL_OPTION_POOL_CORE_SIZE +
					"\n+ poolMaximumSize=" + THREADPOOL_OPTION_POOL_MAXIMUM_SIZE +
					"\n+ coreThreadsPrestarted=" + coreThreadsPrestarted +
					"\n+ idleThreadKeepAliveTime=" + THREADPOOL_OPTION_KEEP_ALIVE_TIME + " " + TimeUnit.MILLISECONDS +
					"\n+ rejectionPolicy=" + THREADPOOL_OPTION_REJECTION_POLICY);
		} catch (Throwable t) {
			logger.error("Thread Pool creation failed: " + t.getMessage());
		}
		return executorService;
	}

	/**
	 * Helper method used to create a thread pool rejected execution handler
	 * based on the configurable options.
	 * 
	 * @return RejectedExecutionHandler containing the resulting rejected execution handler.
	 */
	private static RejectedExecutionHandler createRejectionHandler() {
		RejectedExecutionHandler rejectionExecutionHandler;
		// Rejection policy
		switch (THREADPOOL_OPTION_REJECTION_POLICY) {
			case 1: // Abort
				rejectionExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
				break;
			case 2: // Caller Runs
				rejectionExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
				break;
			case 3: // Discard
				rejectionExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
				break;
			case 4: // Discard Oldest
				rejectionExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
				break;
			default: // Unspecified (default to Abort)
				rejectionExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
				break;
		}
		return rejectionExecutionHandler;
	}

	/**
	 * Helper method used to create a thread pool queue
	 * based on the configurable options.
	 * 
	 * @return BlockingQueue<Runnable> containing the thread pool queue.
	 */
	private static BlockingQueue<Runnable> createQueue() {
		BlockingQueue<Runnable> queue;
		/**************
		 *** CUSTOM ***
		 **************/
		// Queue type
		switch (THREADPOOL_OPTION_QUEUE_TYPE) {
			case 1: // Synchronous
				queue = new SynchronousQueue<Runnable>();
				break;
			case 2: // Unbounded
				queue = new LinkedBlockingQueue<Runnable>(THREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE);
				break;
			case 3: // Bounded
				queue = new ArrayBlockingQueue<Runnable>(THREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE, THREADPOOL_OPTION_QUEUE_FAIR_ORDER);
				break;
			default: // Unspecified (default to Synchronous)
				queue = new SynchronousQueue<Runnable>();
				break;
		}
		logger.debug("Thread Pool Queue created..." +
				"\n+ queueType=" + THREADPOOL_OPTION_QUEUE_TYPE +
				"\n+ queueMaximumSize=" + THREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE +
				"\n+ queueFairOrder=" + THREADPOOL_OPTION_QUEUE_FAIR_ORDER
				);
		return queue;
	}

	/**
	 * Helper method used to prestart core threads in the thread pool
	 * based on the configurable options.
	 * 
	 * @return Integer containing the actual number of core threads that were prestarted.
	 */
	private static int preStartCoreThreads(ExecutorService executorService) {
		int coreThreadsPrestarted;
		// Prestart Core Thread(s)
		switch (THREADPOOL_OPTION_PRESTART_CORE_THREADS) {
			case 0: // No
				coreThreadsPrestarted = 0;
				break;
			case 1: // Core Thread
				coreThreadsPrestarted = ((ThreadPoolExecutor)executorService).prestartCoreThread()?1:0;
				break;
			case 2: // All Core Threads
				coreThreadsPrestarted = ((ThreadPoolExecutor)executorService).prestartAllCoreThreads();
				break;
			default: // Unspecified (default to No)
				coreThreadsPrestarted = 0;
				break;
		}
		logger.debug("Number of core threads prestarted: " + coreThreadsPrestarted);
		return coreThreadsPrestarted;
	}

}
