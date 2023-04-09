package dimploma.rynok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArendatorAllDataDTO {
    private Long id;
    private String name;
    private String surname;

    private String evidence;
    private String passport;
    private String address;
    private String typeCompany;
    private String phone1;

    private String phone2;
    private String email;
    private String notes;

}
