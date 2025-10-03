package com.proyecto.congreso.pases.controller;

import com.proyecto.congreso.pases.dto.PassRequest;
import com.proyecto.congreso.pases.dto.PassResponse;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.service.PassService;
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
@RequestMapping("/api/pases")
@RequiredArgsConstructor
@Tag(name = "Pases", description = "Passes management and points operations APIs")
public class PassController {

    private final PassService passService;

    @PostMapping("/pass")
    @Operation(summary = "Create a new Pass")
    public ResponseEntity<PassResponse> createPass(@Valid @RequestBody PassRequest request) {
        Pass pass = new Pass();
        pass.setPassType(request.getPassType());
        pass.setPointsBalance(request.getPointsBalance());
        pass.setParticipantId(request.getParticipantId());

        Pass createdPass = passService.createPass(pass);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PassResponse.fromEntity(createdPass));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Pass by ID")
    public ResponseEntity<PassResponse> getPassById(@PathVariable Long id) {
        Pass pass = passService.getPassById(id);
        return ResponseEntity.ok(PassResponse.fromEntity(pass));
    }

    @GetMapping("/pases")
    @Operation(summary = "Get all Passes")
    public ResponseEntity<List<PassResponse>> getAllPass() {
        List<PassResponse> pass = passService.getAllPass()
                .stream()
                .map(PassResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pass);
    }

    @GetMapping("/participants/{participantId}")
    @Operation(summary = "Get all Pass by participant ID")
    public ResponseEntity<List<PassResponse>> getPassByParticipantId(@PathVariable Long participantId) {
        List<PassResponse> pass = passService.getPassByParticipantId(participantId)
                .stream()
                .map(PassResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pass);
    }

    @GetMapping("/participant/{participantId}/active")
    @Operation(summary = "Get active accounts by participant ID")
    public ResponseEntity<List<PassResponse>> getActivePassByParticipantId(@PathVariable Long participantId){
        List<PassResponse> pass = passService.getActivePassByParticipantId(participantId)
                .stream()
                .map(PassResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pass);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get Pass by status")
    public ResponseEntity<List<PassResponse>> getPassByStatus(@PathVariable Pass.PassStatus status) {
        List<PassResponse> pass = passService.getPassByStatus(status)
                .stream()
                .map(PassResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pass);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get Pass by type")
    public ResponseEntity<List<PassResponse>> getAPassByType(@PathVariable Pass.PassType type) {
        List<PassResponse> pass = passService.getPassByType(type)
                .stream()
                .map(PassResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pass);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Pass")
    public ResponseEntity<PassResponse> updatePass(
            @PathVariable Long id,
            @Valid @RequestBody PassRequest request) {
        Pass pass = new Pass();
        pass.setPassType(request.getPassType());

        Pass updatedPass = passService.updatePass(id, pass);
        return ResponseEntity.ok(PassResponse.fromEntity(updatedPass));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Pass (soft delete)")
    public ResponseEntity<Void> deletePass(@PathVariable Long id) {
        passService.deletePass(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate Pass")
    public ResponseEntity<PassResponse> activateAPass(@PathVariable Long id) {
        Pass pass = passService.activatePass(id);
        return ResponseEntity.ok(PassResponse.fromEntity(pass));
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close Pass")
    public ResponseEntity<PassResponse> closePass(@PathVariable Long id) {
        Pass pass = passService.closePass(id);
        return ResponseEntity.ok(PassResponse.fromEntity(pass));
    }

    @GetMapping("/participant/{participantId}/count")
    @Operation(summary = "Count Pass by Participant ID")
    public ResponseEntity<Long> countPassByParticipantId(@PathVariable Long participantId) {
        long count = passService.countPassByParticipantId(participantId);
        return ResponseEntity.ok(count);
    }
}
