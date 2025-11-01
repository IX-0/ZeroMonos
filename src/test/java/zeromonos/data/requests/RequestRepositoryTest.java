package zeromonos.data.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import zeromonos.data.residues.Residue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    void setup() {
        em.clear();
    }

    @Test
    void whenFindByValidToken_thenFindValidRequest() {
        Request r0 = em.persistFlushFind(new Request("Aveiro", LocalDateTime.now()));

        Optional<Request> persistedRequest = requestRepository.findRequestByTokenEquals(r0.getToken());
        assertThat(persistedRequest).isNotEmpty().contains(r0);
    }

    @Test
    void whenFindByInvalidToken_thenFindInvalidRequest() {
        em.persistFlushFind(new Request("Aveiro", LocalDateTime.now()));

        Optional<Request> persistedRequest = requestRepository.findRequestByTokenEquals("invalid uuid");
        assertThat(persistedRequest).isEmpty();
    }

    @Test
    void whenFindAllByMunicipalityIgnoreCase_thenReturnMatchingRequests() {
        Request r1 = em.persistFlushFind(new Request("Lisbon", LocalDateTime.now()));
        Request r2 = em.persistFlushFind(new Request("lisbon", LocalDateTime.now()));
        Request r3 = em.persistFlushFind(new Request("Porto", LocalDateTime.now()));

        List<Request> lisbonRequests = requestRepository.findAllByMunicipalityEqualsIgnoreCase("LISBON");

        assertThat(lisbonRequests)
                .hasSize(2)
                .containsExactlyInAnyOrder(r1, r2)
                .doesNotContain(r3);
    }

    @Test
    void whenFindAllByMunicipalityIgnoreCase_thenReturnEmptyListIfNoMatch() {
        em.persistFlushFind(new Request("Braga", LocalDateTime.now()));

        List<Request> requests = requestRepository.findAllByMunicipalityEqualsIgnoreCase("Aveiro");

        assertThat(requests).isEmpty();
    }

    @Test
    void whenFindAllByRequestStatus_thenReturnMatchingRequests() {
        Request r0 = em.persistFlushFind(createRequestWithSimpleResidue("Aveiro"));
        r0.assign().start();

        Request r1 = em.persistFlushFind(createRequestWithSimpleResidue("Lisboa"));
        r1.assign().cancel();

        Request r2 = em.persistFlushFind(createRequestWithSimpleResidue("Porto"));
        r2.cancel();

        em.persist(r0);
        em.persist(r1);
        em.persist(r2);
        em.flush();

        List<Request> pendingRequests = requestRepository.findAllByRequestStatusEquals(RequestStatus.CANCELED);

        assertThat(pendingRequests)
                .hasSize(2)
                .contains(r1, r2)
                .doesNotContain(r0);
    }

    @Test
    void whenFindAllByRequestStatus_thenReturnEmptyIfNoneMatch() {
        Request r0 = createRequestWithSimpleResidue("Aveiro");
        r0.assign();
        em.persist(r0);

        Request r1 = createRequestWithSimpleResidue("Porto");
        r1.assign();
        em.persist(r1);

        em.flush();

        List<Request> pendingRequests = requestRepository.findAllByRequestStatusEquals(RequestStatus.CANCELED);

        assertThat(pendingRequests).isEmpty();
    }

    @Test
    void whenFindAllResiduesByRequestId_thenReturnResiduesForThatRequest() {
        Residue residue1 = new Residue("Plastic", "The planets doom", 1f, 2f);
        Residue residue2 = new Residue("Glass", "The planets saviour", 2f, 3f);

        Request r1 = new Request("Aveiro", LocalDateTime.now());
        r1.addResidue(residue1);
        r1.addResidue(residue2);

        em.persist(residue1);
        em.persist(residue2);
        em.persist(r1);
        em.flush();

        List<Residue> residues = requestRepository.findAllResiduesByRequestId(r1.getToken());

        assertThat(residues)
                .hasSize(2)
                .contains(residue1, residue2);
    }

    @Test
    void whenFindAllResiduesByRequestId_thenReturnEmptyIfRequestNotFound() {
        List<Residue> residues = requestRepository.findAllResiduesByRequestId("nonexistent-token");
        assertThat(residues).isEmpty();
    }

    private Request createRequestWithSimpleResidue(String municipality) {
        Residue residue = new Residue("Residue", "Simple Residue", 1f, 1f);
        Request request = new Request(municipality, LocalDateTime.now());
        request.addResidue(residue);

        return request;
    }
}
