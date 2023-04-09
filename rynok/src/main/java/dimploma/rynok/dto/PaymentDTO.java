package dimploma.rynok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentDTO {

    private Long id;

    private Long receiptId;

    private Double amount;

    private LocalDateTime date;

    private String notes;
}
