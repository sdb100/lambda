package lambdas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Simple demo of a synchronous/asynchronous API.
 *
 */
public class Controller {

    private ExecutorService es = Executors.newFixedThreadPool(4);

    /**
     * The synchronous version of the API.
     * 
     * @param t
     *            a request
     * @return a result
     */
    public Result doSyncThing(Request t) {
        return new Result(t);
    }

    /**
     * An asynchronous API.
     * 
     * @param t
     *            a request object
     * @param callback
     *            a thing to execute when the request is complete
     */
    public void doAsyncThing(Request t, Consumer<Result> callback) {
        this.es.execute(new Processor(t, callback));
    }

    /**
     * Basically the same asynchronous API, but uses a lambda instead of the
     * inner class.
     * 
     * @param t
     *            a request object
     * @param callback
     *            a thing to execute when the request is complete
     */
    public void doAsyncThingJ8Style(Request t, Consumer<Result> callback) {
        this.es.execute(() -> {
            try {
                // Add a delay on this thread
                Thread.sleep(1000, 0);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            callback.accept(new Result(t));
        });
    }

    /**
     * Gracefully close down the executor.
     */
    public void finish() {
        this.es.shutdown();
        try {
            if (!this.es.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Shutdown timed out");
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    /**
     * A simple wrapper to package the request and its callback in a runnable
     * for future execution.
     */
    private class Processor implements Runnable {

        private Request r = null;
        private Consumer<Result> c = null;

        public Processor(Request t, Consumer<Result> callback) {
            this.r = t;
            this.c = callback;
        }

        @Override
        public void run() {
            try {
                // Add a delay on this thread
                Thread.sleep(1000, 0);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            this.c.accept(new Result(this.r));
        }

    }
}
