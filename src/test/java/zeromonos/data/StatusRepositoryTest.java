package zeromonos.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestStatus;
import zeromonos.data.residues.Residue;
import zeromonos.data.statuses.Status;
import zeromonos.data.statuses.StatusRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StatusRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private StatusRepository statusRepository;

    private Request r0;
    private Request r1;

    @BeforeEach
    void setup() {
        r0 = createRequestWithSimpleResidue("Aveiro");
        r1 = createRequestWithSimpleResidue("Porto");

        r0.assign().start();
        r1.cancel();

        r0 = em.persistFlushFind(r0);
        r1 = em.persistFlushFind(r1);
    }

    @AfterEach
    void tearDown() {
        em.clear();
    }

    @Test
    void whenFindAllByValidRequest_thenReturnList() {
        List<Status> statuses0 = statusRepository.findAllByRequest_Token(r0.getToken());
        assertThat(statuses0)
                .hasSize(3)
                .extracting(Status::getRequestStatus)
                .contains(RequestStatus.RECEIVED, RequestStatus.ASSIGNED, RequestStatus.IN_PROGRESS);

        List<Status> statuses1 = statusRepository.findAllByRequest_Token(r1.getToken());
        assertThat(statuses1)
                .hasSize(2)
                .extracting(Status::getRequestStatus)
                .contains(RequestStatus.RECEIVED, RequestStatus.CANCELED);
    }

    @Test
    void whenFindAllByValidRequest_thenReturnEmptyList() {
        List<Status> statuses0 = statusRepository.findAllByRequest_Token(null);
        assertThat(statuses0).isEmpty();
    }

    @Test
    void whenFindAllByValidRequestAndRequestStatus_thenReturnList() {
        List<Status> statuses = statusRepository
                .findAllByRequest_TokenAndRequestStatusEquals(r0.getToken(), RequestStatus.ASSIGNED);

        assertThat(statuses)
                .hasSize(1)
                .extracting(Status::getRequestStatus)
                .contains(RequestStatus.ASSIGNED);
    }

    @Test
    void whenFindAllByInvalidRequestAndRequestStatus_thenReturnEmptyList() {
        List<Status> statuses0 = statusRepository
                .findAllByRequest_TokenAndRequestStatusEquals(r0.getToken(), RequestStatus.COMPLETED);

        assertThat(statuses0).isEmpty();

        List<Status> statuses1 = statusRepository
                .findAllByRequest_TokenAndRequestStatusEquals(r1.getToken(), RequestStatus.ASSIGNED);

        assertThat(statuses1).isEmpty();
    }

    private static Request createRequestWithSimpleResidue(String municipality) {
        Residue residue = new Residue("Residue", "Simple Residue", 1f, 1f);
        Request request = new Request(municipality, LocalDateTime.now());
        request.addResidue(residue);

        return request;
    }
}
