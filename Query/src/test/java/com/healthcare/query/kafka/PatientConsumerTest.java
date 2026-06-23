package com.healthcare.query.kafka;

import com.healthcare.model.PatientEvent;
import com.healthcare.query.document.PatientDocument;
import com.healthcare.query.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientConsumerTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientConsumer consumer;

    private PatientEvent patientEvent;

    @BeforeEach
    void setUp() {
        patientEvent = new PatientEvent();
        patientEvent.setPatientId(1L);
        patientEvent.setName("John Doe");
        patientEvent.setDisease("Diabetes");
        patientEvent.setEmail("john@example.com");
    }

    @Test
    void consume_CreateEvent_SavesToRepository() {
        patientEvent.setEventType("CREATE");

        consumer.consume(patientEvent);

        ArgumentCaptor<PatientDocument> captor = ArgumentCaptor.forClass(PatientDocument.class);
        verify(repository, times(1)).save(captor.capture());

        PatientDocument savedDoc = captor.getValue();
        assertEquals(1L, savedDoc.getId());
        assertEquals("John Doe", savedDoc.getName());
        assertEquals("Diabetes", savedDoc.getDisease());
        assertEquals("john@example.com", savedDoc.getEmail());
    }

    @Test
    void consume_UpdateEvent_SavesToRepository() {
        patientEvent.setEventType("UPDATE");

        consumer.consume(patientEvent);

        ArgumentCaptor<PatientDocument> captor = ArgumentCaptor.forClass(PatientDocument.class);
        verify(repository, times(1)).save(captor.capture());

        PatientDocument savedDoc = captor.getValue();
        assertEquals(1L, savedDoc.getId());
        assertEquals("John Doe", savedDoc.getName());
        assertEquals("Diabetes", savedDoc.getDisease());
        assertEquals("john@example.com", savedDoc.getEmail());
    }

    @Test
    void consume_DeleteEvent_DeletesFromRepository() {
        patientEvent.setEventType("DELETE");

        consumer.consume(patientEvent);

        verify(repository, times(1)).deleteById(1L);
        verify(repository, never()).save(any(PatientDocument.class));
    }

    @Test
    void consume_UnknownEventType_DoesNothing() {
        patientEvent.setEventType("UNKNOWN");

        consumer.consume(patientEvent);

        verify(repository, never()).save(any(PatientDocument.class));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void consume_NullEventType_DoesNothing() {
        patientEvent.setEventType(null);

        consumer.consume(patientEvent);

        verify(repository, never()).save(any(PatientDocument.class));
        verify(repository, never()).deleteById(any());
    }
}
