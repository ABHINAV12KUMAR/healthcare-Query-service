package com.healthcare.query.service;

import com.healthcare.query.document.PatientDocument;
import com.healthcare.query.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientQueryServiceTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private PatientQueryService service;

    private PatientDocument patientDocument;

    @BeforeEach
    void setUp() {
        patientDocument = new PatientDocument();
        patientDocument.setId(1L);
        patientDocument.setName("John Doe");
        patientDocument.setDisease("Diabetes");
        patientDocument.setEmail("john@example.com");
    }

    @Test
    void getAllPatients_WithFilters_ReturnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<PatientDocument> patients = Arrays.asList(patientDocument);

        when(mongoTemplate.find(any(Query.class), eq(PatientDocument.class))).thenReturn(patients);

        CompletableFuture<Page<PatientDocument>> result = service.getAllPatients("John", "Diabetes", pageable);

        assertNotNull(result);
        Page<PatientDocument> page = result.get();
        assertEquals(1, page.getTotalElements());
        assertEquals("John Doe", page.getContent().get(0).getName());

        verify(mongoTemplate, times(1)).find(any(Query.class), eq(PatientDocument.class));
    }

    @Test
    void getAllPatients_WithoutFilters_ReturnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<PatientDocument> patients = Arrays.asList(patientDocument);

        when(mongoTemplate.find(any(Query.class), eq(PatientDocument.class))).thenReturn(patients);

        CompletableFuture<Page<PatientDocument>> result = service.getAllPatients(null, null, pageable);

        assertNotNull(result);
        Page<PatientDocument> page = result.get();
        assertEquals(1, page.getTotalElements());

        verify(mongoTemplate, times(1)).find(any(Query.class), eq(PatientDocument.class));
    }

    @Test
    void getById_ExistingId_ReturnsPatient() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(patientDocument));

        CompletableFuture<PatientDocument> result = service.getById(1L);

        assertNotNull(result);
        PatientDocument patient = result.get();
        assertEquals("John Doe", patient.getName());
        assertEquals("Diabetes", patient.getDisease());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getById_NonExistingId_ThrowsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            CompletableFuture<PatientDocument> result = service.getById(999L);
            result.join();
        });

        verify(repository, times(1)).findById(999L);
    }
}
