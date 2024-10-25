package edu.troy.pennypilot.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Builder @Getter @Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Long id;

    private LocalDate date;
    private TransactionType type;
    private float amount;
    private IncomeCategory incomeCategory;
    private ExpenseCategory expenseCategory;
    private String description;

    Transaction() {
    }

    public String toString() {
        return "Date: " + date + " Type: " + (type==type.INCOME ? incomeCategory : expenseCategory) + " Amount: " + amount + " Description: " + description;
    }
}
