package com.healthcare.query.controller;

import com.healthcare.query.document.PatientDocument;
import com.healthcare.query.service.PatientQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/patients")
@AllArgsConstructor
public class PatientQueryController {
    private final PatientQueryService service;

    @GetMapping
    public CompletableFuture<ResponseEntity<Page<PatientDocument>>> getAllPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String disease,
            Pageable pageable) {
        return service.getAllPatients(name, disease, pageable)
                .thenApply(ResponseEntity::ok);
    }
    @GetMapping("/{id}")
    public CompletableFuture<PatientDocument> getById(@PathVariable Long id) {
        return service.getById(id);
    }
}