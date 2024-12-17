package org.nelly.wso2demoapi.repositories;

import org.nelly.wso2demoapi.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> getAccountByAccountNumber(Long accountNumber);
}
