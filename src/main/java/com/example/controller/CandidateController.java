package com.example.controller;

import com.example.entity.Candidate;
import com.example.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/itoquiz/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<?> createCandidate(@RequestBody Candidate candidate) {
        // Validate input
        if (candidate.getEmailId() == null || candidate.getEmailId().isEmpty()) {
            return ResponseEntity.badRequest().body("Email ID cannot be null or empty.");
        }

        try {
            // Check if email ID already exists
            if ((candidate.getEmailId() == null || candidate.getEmailId().isEmpty()) ||
                    (candidate.getName() == null || candidate.getName().isEmpty()))
            {
                return ResponseEntity.badRequest().body("Email ID and Name cannot be null or empty.");
            }
            candidate.setStarted(false);
            candidate.setSubmit(false);
            Candidate createdCandidate = candidateService.createCandidate(candidate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Candidate ID: " + createdCandidate.getCandidateId() + " generated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating candidate: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        if (candidates.isEmpty()) {
            return ResponseEntity.ok("No Candidates available.");
        }
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/candidate")
    public ResponseEntity<?> getCandidateById(@RequestParam int candidateId) {
        Candidate candidate = candidateService.getCandidateById(candidateId);
        if (candidate == null) {
            return ResponseEntity.badRequest().body("Invalid Candidate ID.");
        }
        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/candidate")
    public ResponseEntity<?> updateCandidate(@RequestParam int candidateId, @RequestBody Candidate candidate) {
        // Validate input
        if ((candidate.getEmailId() == null || candidate.getEmailId().isEmpty())
                || (candidate.getName() == null || candidate.getName().isEmpty())) {
            return ResponseEntity.badRequest().body("Email ID and Name cannot be null or empty.");
        }

        try {
            // Check if candidate exists
            Optional<Candidate> existingCandidate = Optional.ofNullable(candidateService.getCandidateById(candidateId));
            if (!existingCandidate.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found.");
            }

            // Check if the updated email ID already exists (excluding the current candidate's email)
            if (!existingCandidate.get().getEmailId().equals(candidate.getEmailId()) && candidateService.isEmailIdExists(candidate.getEmailId())) {
                return ResponseEntity.badRequest().body("Email ID already exists, please enter a different email ID.");
            }

            // Update candidate details
            Candidate updatedCandidate = candidateService.updateCandidate(candidateId, candidate);
            return ResponseEntity.ok("Candidate ID: " + updatedCandidate.getCandidateId() + " updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating candidate: " + e.getMessage());
        }
    }


    @DeleteMapping("/candidate")
    public ResponseEntity<?> deleteCandidate(@RequestParam int candidateId) {
        try {
            candidateService.deleteCandidate(candidateId);
            return ResponseEntity.ok("Deleted candidate ID " + candidateId + " successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error deleting candidate: " + e.getMessage());
        }
    }
}
