package zeromonos.data.requests;

public class ReceivedState implements RequestState {
    private Request request;

    public ReceivedState(Request request) {
        this.request = request;
    }

    @Override
    public void assign() {
        request.setRequestStatus(RequestStatus.ASSIGNED);
        request.setState(RequestStateFactory.getState(request));
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
        request.setRequestStatus(RequestStatus.CANCELED);
        request.setState(RequestStateFactory.getState(request));
    }
}
