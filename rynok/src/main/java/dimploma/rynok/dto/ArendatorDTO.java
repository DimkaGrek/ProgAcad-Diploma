package dimploma.rynok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArendatorDTO {
    private Long id;
    private String name;
    private String surname;
    private String phone1;
    private String email;
    private String notes;

}
