package com.dinedynamo.repositories;

import com.dinedynamo.collections.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepository extends MongoRepository<Invoice,String> {


}
