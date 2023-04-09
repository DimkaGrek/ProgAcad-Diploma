package dimploma.rynok.dto;

import dimploma.rynok.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private Role role;

    // constructors, getters and setters
}

