package zeromonos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zeromonos.data.requests.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestStateTest {
    private Request request;

    @BeforeEach
    void setup() {
        request = new Request();
    }

    @Test
    void GetStateInstancesNewState() {
        assertThat(request.getState())
                .isNotNull()
                .isInstanceOf(ReceivedState.class);
    }

    @Test
    void InvalidReceivedStateTest() {
        assertThatThrownBy(request::complete)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::start)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void InvalidAssignedStateTest() {
        request.assign();

        assertThatThrownBy(request::complete)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::assign)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void InvalidInProgressStateTest() {
        request.assign();
        request.start();

        assertThatThrownBy(request::start)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::assign)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void InvalidCompletedStateTest() {
        request.assign();
        request.start();
        request.complete();

        assertThatThrownBy(request::start)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::assign)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::complete)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void InvalidCanceledStateTest() {
        request.cancel();

        assertThatThrownBy(request::start)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::assign)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::complete)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(request::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void ValidAssignedStateTest() {
        request.assign();

        assertThat(request.getState()).isInstanceOf(AssignedState.class);
    }

    @Test
    void ValidInProgressStateTest() {
        request.assign();
        request.start();

        assertThat(request.getState()).isInstanceOf(InProgressState.class);
    }

    @Test
    void ValidCompletedStateTest() {
        request.assign();
        request.start();
        request.complete();

        assertThat(request.getState()).isInstanceOf(CompletedState.class);
    }

    @Test
    void ValidCanceledFromAssignedStateTest() {
        request.assign();
        request.cancel();

        assertThat(request.getState()).isInstanceOf(CanceledState.class);
    }

    @Test
    void ValidCanceledFromReceivedStateTest() {
        request.cancel();

        assertThat(request.getState()).isInstanceOf(CanceledState.class);
    }
}
