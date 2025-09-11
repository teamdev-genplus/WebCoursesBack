package com.aecode.webcoursesback.entities.Paid;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "payment_receipt_items",
        uniqueConstraints = @UniqueConstraint(name = "uq_payment_receipt_item", columnNames = {"receipt_id","module_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentReceiptItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false, foreignKey = @ForeignKey(name = "fk_receipt_item_receipt"))
    private PaymentReceipt receipt;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "course_title", length = 256)
    private String courseTitle;  // opcional, para reporting

    @Column(name = "module_title", length = 256)
    private String moduleTitle;  // opcional, para reporting
}
