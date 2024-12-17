package org.nelly.wso2demoapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.nelly.wso2demoapi.models.enums.AccountStatus;
import org.nelly.wso2demoapi.models.enums.Currency;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY, generator = "account_id_seq_gen")
    @SequenceGenerator(name = "account_id_seq_gen", sequenceName = "account_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT default nextval('account_id_seq')")
    private Long id;

    @Column(name = "account_number", unique = true, length = 20, nullable = false)
    private Long accountNumber;

    @Column(name = "account_title", nullable = false)
    private String accountTitle;

    @Column(name = "account_currency", nullable = false, length = 10, columnDefinition = "varchar(10) default 'UGX'")
    @Enumerated(EnumType.STRING)
    @Builder.Default()
    private Currency accountCurrency = Currency.UGX;

    @Column(name = "account_status", nullable = false, columnDefinition = "varchar(10) default 'INACTIVE'", length = 10)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.INACTIVE;

    @Column(name = "balance", nullable = false, precision = 2, length = 15, columnDefinition = "numeric(15,2) default '0.00'")
    @Builder.Default
    private BigDecimal balance = new BigDecimal("0.00");

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;


    public String getCustomerName() {
        return customer.getFirstName() + " " + customer.getLastName();
    }

    public String getMaskedAccountNumber() {
        return "******" + String.valueOf(accountNumber).substring(6);
    }


}
