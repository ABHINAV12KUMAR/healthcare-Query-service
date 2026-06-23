package com.healthcare.query.repository;

import com.healthcare.query.document.PatientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends MongoRepository<PatientDocument,Long> {

}
