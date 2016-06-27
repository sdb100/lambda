package lambdas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Simple demo of a synchronous/asynchronous API.
 *
 */
public class ControllerImpl implements Controller {

    private ExecutorService es = Executors.newFixedThreadPool(4);

    /*
     * (non-Javadoc)
     * 
     * @see lambdas.Controller#doSyncThing(lambdas.Request)
     */
    @Override
    public Result doSyncThing(Request t) {
        return new Result(t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see lambdas.Controller#doAsyncThing(lambdas.Request,
     * java.util.function.Consumer)
     */
    @Override
    public void doAsyncThing(Request t, Consumer<Result> callback) {
        this.es.execute(new Processor(t, callback));
    }

    /*
     * (non-Javadoc)
     * 
     * @see lambdas.Controller#doAsyncThingJ8Style(lambdas.Request,
     * java.util.function.Consumer)
     */
    @Override
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

    /*
     * (non-Javadoc)
     * 
     * @see lambdas.Controller#finish()
     */
    @Override
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
