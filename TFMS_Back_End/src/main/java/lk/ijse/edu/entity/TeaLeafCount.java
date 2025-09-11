package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tea_leaf_count")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeaLeafCount {
    @Id
    private String id;

    @Column(nullable = false)
    private String teaCardNumber;
    private String supplierName;
    private String grossWeight;
    private String sackWeight;
    private String moistureWeight;
    private String netWeight;
    private String date;
    private String time;

    @Enumerated(EnumType.STRING)
    private QualityAssessment quality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private TeaLeafSupplier supplier;

    private String note;
}
