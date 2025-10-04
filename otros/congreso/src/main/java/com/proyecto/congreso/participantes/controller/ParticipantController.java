package com.proyecto.congreso.participantes.controller;

import com.proyecto.congreso.participantes.dto.ParticipantRequest;
import com.proyecto.congreso.participantes.dto.ParticipantResponse;
import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
@Tag(name = "Participant", description = "Participant management APIs")
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/participant")
    @Operation(summary = "Create a new Participant")
    public ResponseEntity<ParticipantResponse> createParticipant(@Valid @RequestBody ParticipantRequest request) {

        Participant participant = new Participant();
        participant.setName(request.getName());
        participant.setLastName(request.getLastName());
        participant.setEmail(request.getEmail());
        participant.setPhone(request.getPhone());
        participant.setNacionality(request.getNacionality());
        participant.setAge(request.getAge());
        participant.setArea(request.getArea());

        Participant createdParticipant = participantService.createParticipant(participant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ParticipantResponse.fromEntity(createdParticipant));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Participant by ID")
    public ResponseEntity<ParticipantResponse> getParticipantById(@PathVariable Long id) {
        Participant participant = participantService.getParticipantById(id);
        return ResponseEntity.ok(ParticipantResponse.fromEntity(participant));
    }

    @GetMapping("/participants")
    @Operation(summary = "Get all Participants")
    public ResponseEntity<List<ParticipantResponse>> getAllParticipants() {
        List<Participant> participants = participantService.getAllParticipants();
        List<ParticipantResponse> response = participants.stream()
                .map(ParticipantResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get Participants by status")
    public ResponseEntity<List<ParticipantResponse>> getParticipantByStatus(
            @PathVariable Participant.ParticipantStatus status) {
        List<Participant> participants = participantService.getParticipantByStatus(status);
        List<ParticipantResponse> response = participants.stream()
                .map(ParticipantResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Participant")
    public ResponseEntity<ParticipantResponse> updateParticipant(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantRequest request) {
        Participant participant = new Participant();
        participant.setName(request.getName());
        participant.setLastName(request.getLastName());
        participant.setEmail(request.getEmail());
        participant.setPhone(request.getPhone());
        participant.setNacionality(request.getNacionality());
        participant.setAge(request.getAge());
        participant.setArea(request.getArea());

        Participant updatedParticipant = participantService.updateParticipant(id, participant);
        return ResponseEntity.ok(ParticipantResponse.fromEntity(updatedParticipant));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Participant (soft delete)")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate Participant")
    public ResponseEntity<ParticipantResponse> activateParticipant(@PathVariable Long id) {
        Participant participant = participantService.activateParticipant(id);
        return ResponseEntity.ok(ParticipantResponse.fromEntity(participant));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate Participant")
    public ResponseEntity<ParticipantResponse> deactivateParticipant(@PathVariable Long id) {
        Participant participant = participantService.deactivateParticipant(id);
        return ResponseEntity.ok(ParticipantResponse.fromEntity(participant));
    }
}
