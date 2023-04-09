package dimploma.rynok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CounterDTO {

    private Long id;

    private String pavilion;
    private String place;
    private Long number;

    private String surname;

    private String name;

    private String notes;
}
