package lambdas;

import java.util.function.Consumer;

public interface Controller {

    /**
     * The synchronous version of the API.
     * 
     * @param t
     *            a request
     * @return a result
     */
    Result doSyncThing(Request t);

    /**
     * An asynchronous API.
     * 
     * @param t
     *            a request object
     * @param callback
     *            a thing to execute when the request is complete
     */
    void doAsyncThing(Request t, Consumer<Result> callback);

    /**
     * Basically the same asynchronous API, but uses a lambda instead of the
     * inner class in the implementation.
     * 
     * @param t
     *            a request object
     * @param callback
     *            a thing to execute when the request is complete
     */
    void doAsyncThingJ8Style(Request t, Consumer<Result> callback);

    /**
     * Gracefully close down the executor.
     */
    void finish();

}
