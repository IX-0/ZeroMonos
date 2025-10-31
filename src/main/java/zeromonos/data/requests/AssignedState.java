package zeromonos.data.requests;

public class AssignedState implements RequestState {
    private Request request;

    public AssignedState(Request request) {
        this.request = request;
    }

    @Override
    public void assign() {
        throw new IllegalStateException("Illegal state change.");
    }

    @Override
    public void start() {
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setState(RequestStateFactory.getState(request));
    }

    @Override
    public void complete() {
        throw new IllegalStateException("Illegal state change.");
    }

    @Override
    public void cancel() {
        request.setStatus(RequestStatus.CANCELED);
        request.setState(RequestStateFactory.getState(request));
    }
}
