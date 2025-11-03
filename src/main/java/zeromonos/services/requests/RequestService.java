package zeromonos.services.requests;

import org.springframework.stereotype.Service;
import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestRepository;
import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RequestService implements RequestServiceInterface {

    private final RequestRepository requestRepository;
    private final ResidueRepository residueRepository;

    RequestService(RequestRepository requestRepository, ResidueRepository residueRepository) {
        this.requestRepository = requestRepository;
        this.residueRepository = residueRepository;
    }

    @Override
    public String createRequest(Request request, List<Long> residueIds) {
        if (residueIds.isEmpty()) {
            throw new IllegalArgumentException("Argument residueIds cannot be empty");
        }

        for (Long id: residueIds) {
            Optional<Residue> r0 = residueRepository.findById(id);
            if (r0.isPresent()) {
                request.addResidue(r0.get());
            } else {
                throw new NoSuchElementException("Residue with id " + id + " not found");
            }
        }
        return requestRepository.saveAndFlush(request).getToken();
    }

    @Override
    public void deleteRequest(String token) {
        Optional<Request> requestOptional = requestRepository.findRequestByTokenEquals(token);

        if (requestOptional.isPresent()) {
            requestRepository.deleteByToken(token);
        } else {
            throw new NoSuchElementException("Request with Token " + token + " not found");
        }
    }

    @Override
    public Request getRequest(String token) {
        Optional<Request> req = requestRepository.findRequestByTokenEquals(token);

        if (req.isPresent()) {
            return req.get();
        } else {
            throw new  NoSuchElementException("Request with Token " + token + " not found");
        }
    }

    @Override
    public void cancelRequest(String token) {
        Optional<Request> requestOptional = requestRepository.findRequestByTokenEquals(token);

        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            requestRepository.save(request.cancel());
        } else {
            throw new  NoSuchElementException("Request with Token " + token + " not found");
        }
    }

    @Override
    public void assignRequest(String token) {
        Optional<Request> requestOptional = requestRepository.findRequestByTokenEquals(token);

        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            requestRepository.save(request.assign());
        } else {
            throw new  NoSuchElementException("Request with Token " + token + " not found");
        }
    }

    @Override
    public void startRequest(String token) {
        Optional<Request> requestOptional = requestRepository.findRequestByTokenEquals(token);

        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            requestRepository.save(request.start());
        } else {
            throw new  NoSuchElementException("Request with Token " + token + " not found");
        }
    }

    @Override
    public void completeRequest(String token) {
        Optional<Request> requestOptional = requestRepository.findRequestByTokenEquals(token);

        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            requestRepository.save(request.complete());
        } else {
            throw new  NoSuchElementException("Request with Token " + token + " not found");
        }
    }

    @Override
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public List<Request> getAllRequestsByMunicipality(String municipality) {
        return requestRepository.findAllByMunicipalityEqualsIgnoreCase(municipality);
    }
}
