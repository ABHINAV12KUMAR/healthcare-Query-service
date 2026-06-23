package com.healthcare.query.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "patients")
public class PatientDocument {
    @Id
    private Long id;
    private String name;
    private String disease;
    private String email;
}
