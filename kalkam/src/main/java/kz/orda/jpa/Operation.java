package kz.orda.jpa;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Ali on 27.12.2015.
 */
@Entity
@Table(name = "OPERATIONS")
public class Operation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Product product;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "price")
    private Double price;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "type")
    private OperationType operationType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (id != null ? !id.equals(operation.id) : operation.id != null) return false;
        return !(date != null ? !date.equals(operation.date) : operation.date != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
