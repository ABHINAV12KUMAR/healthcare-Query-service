package com.healthcare.query.kafka;

import com.healthcare.model.PatientEvent;
import com.healthcare.query.document.PatientDocument;
import com.healthcare.query.repository.PatientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PatientConsumer {

    private final PatientRepository repository;

    @KafkaListener(topics = "patient-topic", groupId = "patient-group")
    public void consume(PatientEvent event) {

        log.info("Received event: {}", event);

        if ("CREATE".equals(event.getEventType()) ||
                "UPDATE".equals(event.getEventType())) {

            PatientDocument doc = new PatientDocument();
            doc.setId(event.getPatientId());
            doc.setName(event.getName());
            doc.setDisease(event.getDisease());
            doc.setEmail(event.getEmail());

            repository.save(doc);

            log.info("Saved to MongoDB: {}", doc);

        } else if ("DELETE".equals(event.getEventType())) {

            repository.deleteById(event.getPatientId());

            log.info("Deleted from MongoDB id: {}", event.getPatientId());
        }
    }
}