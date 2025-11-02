package zeromonos.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestDTO;
import zeromonos.data.residues.ResidueDTO;
import zeromonos.services.requests.RequestService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<RequestDTO>> getAllRequests() {
        List<Request> requests = requestService.getAllRequests();
        return ResponseEntity.ok(
            requests.stream().map(RequestDTO::fromRequestEntity).toList()
        );
    }

    @PostMapping
    public ResponseEntity<String> createRequest(@RequestBody RequestDTO request) {
        try {
            String token = requestService.createRequest(
                    RequestDTO.toRequestEntity(request),
                    request.getResidues().stream().map(ResidueDTO::getId).toList()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/{token}")
    public ResponseEntity<RequestDTO> getRequest(@PathVariable("token") String token) {
        try {
            return ResponseEntity.ok(
                    RequestDTO.fromRequestEntity(requestService.getRequest(token))
            );
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Void> deleteRequest(@PathVariable("token") String token) {
        try {
            requestService.deleteRequest(token);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{token}/assign")
    public ResponseEntity<Void> assignRequest(@PathVariable("token") String token) {
        try {
            requestService.assignRequest(token);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{token}/start")
    public ResponseEntity<Void> startRequest(@PathVariable("token") String token) {
        try {
            requestService.startRequest(token);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{token}/complete")
    public ResponseEntity<Void> completeRequest(@PathVariable("token") String token) {
        try {
            requestService.completeRequest(token);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{token}/cancel")
    public ResponseEntity<Void> cancelRequest(@PathVariable("token") String token) {
        try {
            requestService.cancelRequest(token);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/municipality/{query}")
    public ResponseEntity<List<RequestDTO>> getAllRequests(@PathVariable("query") String query) {
        List<Request> requests = requestService.getAllRequestsByMunicipality(query);
        return ResponseEntity.ok(
                requests.stream().map(RequestDTO::fromRequestEntity).toList()
        );
    }
}
