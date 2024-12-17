package org.nelly.wso2demoapi.repositories;

import org.nelly.wso2demoapi.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsCustomerByEmailIgnoreCaseOrPhoneNumber(String email, String phoneNumber);

    Optional<Customer> findCustomerByEmailIgnoreCase(String customerEmail);
}
