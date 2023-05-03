package dimploma.rynok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CalculationPrintDTO {

    private Long id;

    private String month;

    private LocalDateTime date;

    private String name;

    private String surname;

    private Long countNow;

    private Long countBefore;

    private Long difference;

    private Double rate;

    private Double amount;

    private String notes;

    private Double payment; // в этом поле будет сумма долга или переплаты или 0 (полностью оплачено), или null (полностью не оплачено)

    private String pavilion;

    private String place;

    private Long counterId;

    private Double totalCalcAmount;

    private Double totalPayAmount;
}
