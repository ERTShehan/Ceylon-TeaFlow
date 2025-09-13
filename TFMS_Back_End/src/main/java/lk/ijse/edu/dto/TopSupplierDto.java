package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopSupplierDto {
    private String supplierId;
    private String supplierName;
    private String teaCardNumber;
    private Double totalSupplied;
}
