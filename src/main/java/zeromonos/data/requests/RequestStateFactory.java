package zeromonos.data.requests;

public class RequestStateFactory {
    private RequestStateFactory() {}

    public static RequestState getState(Request request) {
        return switch (request.getRequestStatus()) {
            case RECEIVED -> new ReceivedState(request);
            case ASSIGNED -> new AssignedState(request);
            case IN_PROGRESS -> new InProgressState(request);
            case COMPLETED -> new CompletedState(request);
            case CANCELED -> new CanceledState(request);
        };
    }
}
