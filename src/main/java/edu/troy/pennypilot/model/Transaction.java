package edu.troy.pennypilot.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


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
//    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private float amount;
//    @Enumerated(EnumType.STRING)
    private IncomeCategory incomeCategory;
//    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;
    private String description;

    Transaction() {
    }

    public String toString() {
        return "Date: " + date + " Type: " + (type==type.INCOME ? incomeCategory : expenseCategory) + " Amount: " + amount + " Description: " + description;
    }
}
