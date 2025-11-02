package zeromonos.boundary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeromonos.data.requests.RequestStatus;
import zeromonos.data.statuses.Status;
import zeromonos.services.statuses.StatusService;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/request/{token}")
    public ResponseEntity<List<Status>> getAllStatuses(@PathVariable("token") String token) {
        List<Status> statuses = statusService.getAllStatuses(token);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/request/{token}/filter/{status}")
    public ResponseEntity<List<Status>> getStatusesByType(@PathVariable("token") String token,
                                                          @PathVariable("status") RequestStatus status) {
        List<Status> statuses = statusService.getStatus(token, status);
        return ResponseEntity.ok(statuses);
    }
}
