package org.nelly.wso2demoapi.services;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.nelly.wso2demoapi.models.Account;
import org.nelly.wso2demoapi.models.Customer;
import org.nelly.wso2demoapi.models.Transaction;
import org.nelly.wso2demoapi.models.enums.AccountStatus;
import org.nelly.wso2demoapi.models.enums.Currency;
import org.nelly.wso2demoapi.models.enums.TransactionType;
import org.nelly.wso2demoapi.repositories.AccountRepository;
import org.nelly.wso2demoapi.repositories.CustomerRepository;
import org.nelly.wso2demoapi.repositories.TransactionRepository;
import org.nelly.wso2demoapi.utils.Generator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService implements IService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Generator generator;

    public JSONObject createCustomer(JSONObject request) {
        requires(
                List.of("first_name", "last_name", "email", "phone_number", "address"),
                request
        );

        String email = request.getString("email");
        String phoneNumber = request.getString("phone_number");
        if (customerRepository.existsCustomerByEmailIgnoreCaseOrPhoneNumber(email, phoneNumber)) {
            throw new RuntimeException("Customer with email or phone number already exist");
        }
        Customer customer = Customer.builder()
                .firstName(request.getString("first_name"))
                .lastName(request.getString("last_name"))
                .email(request.getString("email"))
                .phoneNumber(phoneNumber)
                .address(request.getString("address"))
                .build();


        return JSONObject.from(customerRepository.saveAndFlush(customer));
    }

    public JSONObject createAccount(JSONObject request) {
        requires(
                List.of("currency", "customer_email", "account_title"),
                request
        );

        Currency currency = Currency.valueOf(request.getString("currency"));
        String customerEmail = request.getString("customer_email");
        String accountTitle = request.getString("account_title");

        Customer customer = customerRepository.findCustomerByEmailIgnoreCase(customerEmail)
                .orElseThrow(() -> new RuntimeException("Invalid user email"));

        Account account = Account.builder()
                .accountCurrency(currency)
                .accountTitle(accountTitle)
                .customer(customer)
                .accountNumber(generator.generateAccountNumber())
                .build();


        return JSONObject.from(accountRepository.saveAndFlush(account));
    }

    public JSONObject activateAccount(JSONObject request) {
        requires(
                List.of("account_number"),
                request
        );
        String accountNumber = request.getString("account_number");
        Account account = accountRepository.getAccountByAccountNumber(Long.valueOf(accountNumber))
                .orElseThrow(() -> new RuntimeException("Invalid account number"));
        if (account.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is already active");
        }
        account.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.saveAndFlush(account);
        return JSONObject.of("message", "Account activated successfully");
    }

    public JSONObject deposit(JSONObject request) {
        requires(
                List.of("account_number", "amount", "transaction_narration"),
                request
        );
        Account account = this.getAccount(request.getString("account_number"));
        if (account.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new RuntimeException("Account hasn't been activated yet");
        }
        String amount = request.getString("amount");
        String depositorName = request.getString("depositor_name");
        String depositorPhone = request.getString("depositor_phone");
        String transactionNarration = request.getString("transaction_narration");

        Transaction transaction = Transaction.builder()
                .creditAccount(account)
                .creditAmount(new BigDecimal(amount))
                .transactionNarration(transactionNarration)
                .depositorPhoneNumber(depositorPhone)
                .depositorName(depositorName)
                .externalTranRefNum(generator.generateTransactionReference())
                .build();
        transactionRepository.saveAndFlush(transaction);
        account.setBalance(account.getBalance().add(transaction.getCreditAmount()));
        accountRepository.saveAndFlush(account);

        return JSONObject.of("message", "deposited " + amount + " on account: " + account.getAccountNumber());
    }

    public JSONObject getAccountDetails(JSONObject request) {
        requires(
                List.of("account_number"),
                request
        );
        return JSONObject.from(this.getAccount(request.getString("account_number")));
    }

    public JSONArray ministatement(JSONObject request) {
        requires(
                List.of("account_number"),
                request
        );
        Account account = this.getAccount(request.getString("account_number"));
        return JSONArray.from(transactionRepository.findTransactionsByCreditAccount_AccountNumber(account.getAccountNumber()));
    }

    public JSONObject transfer(JSONObject request) {
        requires(
                List.of("src_acc_number", "dest_acc_number", "amount", "transaction_narration"),
                request
        );

        Account srcAccount = this.getAccount(request.getString("src_acc_number"));
        Account destAccount = this.getAccount(request.getString("dest_acc_number"));
        if (srcAccount.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new RuntimeException("Source account hasn't been activated yet");
        }
        if (destAccount.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new RuntimeException("Destination account hasn't been activated yet");
        }
        BigDecimal amount = request.getBigDecimal("amount");
        if (srcAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        String transactionNarration = request.getString("transaction_narration");
        String depositorName = request.getString("depositor_name");
        String depositorPhone = request.getString("depositor_phone");
        String externalTranRefNum = generator.generateTransactionReference();

        Transaction transactionDebit = Transaction.builder()
                .transactionType(TransactionType.DEBIT)
                .creditAccount(srcAccount)
                .transactionNarration(transactionNarration)
                .externalTranRefNum(externalTranRefNum)
                .depositorPhoneNumber(depositorPhone)
                .depositorName(depositorName)
                .creditAmount(amount)
                .build();

        Transaction transactionCredit = Transaction.builder()
                .transactionType(TransactionType.CREDIT)
                .creditAccount(destAccount)
                .transactionNarration(transactionNarration)
                .externalTranRefNum(generator.generateTransactionReference())
                .depositorPhoneNumber(depositorPhone)
                .creditAmount(amount)
                .depositorName(depositorName)
                .build();
        destAccount.setBalance(destAccount.getBalance().add(transactionCredit.getCreditAmount()));
        srcAccount.setBalance(srcAccount.getBalance().subtract(transactionDebit.getCreditAmount()));
        transactionRepository.saveAllAndFlush(List.of(transactionCredit, transactionDebit));
        accountRepository.saveAllAndFlush(List.of(srcAccount, destAccount));


        return JSONObject.of("message", "Transfer of " + amount + " from " + srcAccount.getAccountNumber() + " to " + destAccount.getAccountNumber() + " was successful");
    }

    public JSONArray accounts() {
        return JSONArray.from(accountRepository.findAll());
    }


    private Account getAccount(String accountNumber) {
        Account account = accountRepository.getAccountByAccountNumber(Long.valueOf(accountNumber))
                .orElseThrow(() -> new RuntimeException("Invalid account number"));
        return account;
    }
}
