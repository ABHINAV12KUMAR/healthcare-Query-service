package com.healthcare.query.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.query.document.PatientDocument;
import com.healthcare.query.service.PatientQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientQueryController.class)
class PatientQueryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientQueryService service;

    private PatientDocument patientDocument;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        patientDocument = new PatientDocument();
        patientDocument.setId(1L);
        patientDocument.setName("John Doe");
        patientDocument.setDisease("Diabetes");
        patientDocument.setEmail("john@example.com");
    }

    @Test
    void getAllPatients_WithFilters_ReturnsPage() throws Exception {
        List<PatientDocument> patients = Arrays.asList(patientDocument);
        Page<PatientDocument> page = new PageImpl<>(patients, PageRequest.of(0, 10), 1);

        when(service.getAllPatients(eq("John"), eq("Diabetes"), any()))
                .thenReturn(CompletableFuture.completedFuture(page));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/patients")
                        .param("name", "John")
                        .param("disease", "Diabetes")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[0].disease").value("Diabetes"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllPatients_WithoutFilters_ReturnsPage() throws Exception {
        List<PatientDocument> patients = Arrays.asList(patientDocument);
        Page<PatientDocument> page = new PageImpl<>(patients, PageRequest.of(0, 10), 1);

        when(service.getAllPatients(eq(null), eq(null), any()))
                .thenReturn(CompletableFuture.completedFuture(page));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getById_ExistingId_ReturnsPatient() throws Exception {
        when(service.getById(1L)).thenReturn(CompletableFuture.completedFuture(patientDocument));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.disease").value("Diabetes"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getById_NonExistingId_ServiceCalled() throws Exception {
        CompletableFuture<PatientDocument> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Patient not found"));
        when(service.getById(999L)).thenReturn(failedFuture);

        mockMvc.perform(get("/api/v1/patients/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).getById(999L);
    }
}
