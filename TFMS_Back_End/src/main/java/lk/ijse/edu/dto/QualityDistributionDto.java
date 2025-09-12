package lk.ijse.edu.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QualityDistributionDto {
    private double excellentPercentage;
    private double goodPercentage;
    private double averagePercentage;
    private double poorPercentage;
}
