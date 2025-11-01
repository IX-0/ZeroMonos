package zeromonos.data.requests;

public interface RequestState {
    void assign();
    void start();
    void complete();
    void cancel();
}
