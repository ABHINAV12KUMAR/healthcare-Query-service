package com.healthcare.query.service;

import com.healthcare.query.document.PatientDocument;
import com.healthcare.query.repository.PatientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class PatientQueryService {

    private final PatientRepository repository;
    private final MongoTemplate mongoTemplate;

    @Async
    public CompletableFuture<Page<PatientDocument>> getAllPatients(String name, String disease, Pageable pageable) {
        log.info("Fetching patients with filters - name: {}, disease: {}, pageable: {}", name, disease, pageable);

        Criteria criteria = new Criteria();
        if (name != null) {
            criteria.and("name").regex(name, "i");
        }
        if (disease != null) {
            criteria.and("disease").regex(disease, "i");
        }

        Query query = new Query(criteria).with(pageable);
        List<PatientDocument> patients = mongoTemplate.find(query, PatientDocument.class);
        Page<PatientDocument> page = PageableExecutionUtils.getPage(patients, pageable, () -> mongoTemplate.count(query, PatientDocument.class));
        return CompletableFuture.completedFuture(page);
    }

    @Async
    public CompletableFuture<PatientDocument> getById(Long id) {
        log.info("Fetching patient by id: {}", id);
        PatientDocument patient = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return CompletableFuture.completedFuture(patient);
    }
}