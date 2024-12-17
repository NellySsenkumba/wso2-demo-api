package org.nelly.wso2demoapi.repositories;

import org.nelly.wso2demoapi.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTransactionsByCreditAccount_AccountNumber(Long creditAccountAccountNumber);
}
