package org.nelly.wso2demoapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.nelly.wso2demoapi.models.enums.Currency;
import org.nelly.wso2demoapi.models.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BIGINT default nextval('transaction_id_seq')")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "transaction_id_seq_gen")
    @SequenceGenerator(name = "transaction_id_seq_gen", sequenceName = "transaction_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "external_tran_ref_num", unique = true, length = 50, nullable = false)
    private String externalTranRefNum;

    @Column(name = "transaction_date_time", nullable = false, updatable = false, columnDefinition = "timestamp(6) default now()")
    @CreationTimestamp
    @Builder.Default
    private Timestamp transactionDateTime = new Timestamp(System.currentTimeMillis());

    @JoinColumn(name = "credit_account", nullable = false)
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Account creditAccount;

    @Column(name = "credit_amount", length = 15, nullable = false, precision = 2, columnDefinition = "numeric(15,2) default '0.00'")
    private BigDecimal creditAmount;

    @Column(name = "transaction_currency", nullable = false, length = 10, columnDefinition = "varchar(10) default 'UGX'")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Currency transactionCurrency = Currency.UGX;

    @Column(name = "transaction_narration", columnDefinition = "TEXT", nullable = false)
    private String transactionNarration;

    @Column(name = "depositor_name")
    private String depositorName;

    @Column(name = "depositor_phone_number", length = 15)
    private String depositorPhoneNumber;

    @Column(name = "transaction_type", nullable = false, columnDefinition = "varchar(10) default 'CREDIT'")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionType transactionType = TransactionType.CREDIT;

    public Long getAccountNumber() {
        return this.creditAccount.getAccountNumber();
    }

}

