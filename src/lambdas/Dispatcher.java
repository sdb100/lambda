package lambdas;

import java.util.function.Consumer;

/**
 * Simple demo of using lambda callbacks on an asynchronous API.
 */
public class Dispatcher {
    public static void main(String[] args) {
        new Dispatcher().makeCalls();
    }

    public void makeCalls() {

        Controller controller = new ControllerImpl();
        final Dispatcher that = this;

        // This does a synchronous call to the Controller
        Request t1 = new Request("synchronous");
        System.out.println("Starting: " + t1);
        Result result = controller.doSyncThing(t1);
        this.output(result);

        // Pre-Java8 way of making an asynchronous call (well, except for the
        // Consumer)
        Request t2 = new Request("Pre Java 8 anonymous inner class");
        controller.doAsyncThing(t2, new Consumer<Result>() {
            public void accept(Result r) {
                that.output(r);
            }
        });
        System.out.println("Submitted: " + t2);

        // Simplified with a lambda - note change in scope of "this"
        Request t3 = new Request("Java 8 lambda");
        controller.doAsyncThing(t3, (Result r) -> {
            this.output(r);
        });
        System.out.println("Submitted: " + t3);

        // Slightly simplified lambda, using println directly
        Request t4 = new Request("Java 8 lambda simplified slightly");
        controller.doAsyncThing(t4, (r) -> {
            System.out.println(r);
        });
        System.out.println("Submitted: " + t4);

        // Using a method reference to do the same as above
        Request t5 = new Request("Java 8 method reference");
        controller.doAsyncThingJ8Style(t5, System.out::println);
        System.out.println("Submitted: " + t5);

        // Shut the executor down
        controller.finish();

    }

    private void output(Result r) {
        System.out.println("Finished: " + r);
    }
}
