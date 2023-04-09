package dimploma.rynok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CounterAllDataDTO {

    private Long id;

    private String pavilion;
    private String place;

    private String type;
    private Long number;

    private Long atomicity;

    private LocalDate dateManufac;

    private LocalDate dateInstall;

    private Long parent;

    private String surname;

    private String name;

    private String notes;
}
