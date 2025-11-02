package zeromonos.data.statuses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zeromonos.data.requests.RequestStatus;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    List<Status> findAllByRequest_TokenAndRequestStatusEquals(String token, RequestStatus status);

    List<Status> findAllByRequest_Token(String requestToken);

}
