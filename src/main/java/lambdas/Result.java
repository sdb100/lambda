package lambdas;

public class Result {
    private Request request = null;

    public Result(Request t) {
        this.request = t;
    }

    @Override
    public String toString() {
        return this.request.toString();
    }
}
