package zeromonos.data.requests;

public class CanceledState implements RequestState {
    private Request request;

    public CanceledState(Request request) {
        this.request = request;
    }

    @Override
    public void assign() {
        throw new IllegalStateException("Illegal state change.");
    }

    @Override
    public void start() {
        throw new IllegalStateException("Illegal state change.");
    }

    @Override
    public void complete() {
        throw new IllegalStateException("Illegal state change.");
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("Illegal state change.");
    }
}
