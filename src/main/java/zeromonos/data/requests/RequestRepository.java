package zeromonos.data.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zeromonos.data.residues.Residue;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {

    Optional<Request> findRequestByTokenEquals(String token);

    List<Request> findAllByMunicipalityEqualsIgnoreCase(String municipality);

    List<Request> findAllByRequestStatusEquals(RequestStatus requestStatus);

    @Query("SELECT r.residues FROM Request r WHERE r.token = :requestToken")
    List<Residue> findAllResiduesByRequestId(@Param("requestToken") String requestToken);

}
