package zeromonos.data.requests;

public class InProgressState implements RequestState {
    private Request request;

    public InProgressState(Request request) {
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
        request.setStatus(RequestStatus.COMPLETED);
        request.setState(RequestStateFactory.getState(request));
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("Illegal state change.");
    }
}
