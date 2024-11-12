package edu.troy.pennypilot.budget.persistence;

import edu.troy.pennypilot.transaction.persistence.ExpenseCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder @Getter @Setter
@Entity
@Table(name = "budgets")
public class Budget {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Long id;
    private ExpenseCategory expenseCategory;
    private float amount;

    Budget(){}
}
