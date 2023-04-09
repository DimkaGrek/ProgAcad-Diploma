package dimploma.rynok.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String login;
    private List<RoleDTO> roles;

    // constructors, getters and setters
}

