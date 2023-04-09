package dimploma.rynok.services;

import dimploma.rynok.dto.RoleDTO;
import dimploma.rynok.dto.UserDTO;
import dimploma.rynok.enums.Role;
import dimploma.rynok.model.CustomUser;
import dimploma.rynok.model.Roles;
import dimploma.rynok.repo.RolesRepository;
import dimploma.rynok.repo.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Data
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    @Transactional(readOnly = true)
    public List<RoleDTO> getUserRoles(Long userId) {
        CustomUser user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return user.getRoles().stream().map(role -> new RoleDTO(role.getId(), role.getRole())).collect(Collectors.toList());
    }

    @Transactional
    public List<Roles> getRolesById(Iterable ids) { return rolesRepository.findAllById(ids);}

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(){
        List<CustomUser> users = userRepository.findAll();

        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private UserDTO convertToDTO(CustomUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        List<RoleDTO> roleDTOs = user.getRoles().stream().map(role -> new RoleDTO(role.getId(), role.getRole())).collect(Collectors.toList());
        userDTO.setRoles(roleDTOs);
        return userDTO;
    }

    @Transactional(readOnly = true)
    public CustomUser findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Transactional
    public void deleteUsers(List<Long> ids) {

    }

    @Transactional
    public boolean addUser(String login, String passHash, String email, String realname, String position, String phone, String notes, Long permission, List<Roles> roles) {
        if (userRepository.existsByLogin(login)) return false;

        CustomUser user = new CustomUser(login, passHash, email, realname, position, phone, notes, permission);
        user.setRoles(roles);

        userRepository.save(user);

        return true;

    }

    @Transactional
    public boolean changePassword(Long userId, String passHash) {
        Optional<CustomUser> optionalCustomUser = userRepository.findById(userId);
        if(optionalCustomUser.isPresent()) {
            CustomUser user = optionalCustomUser.get();
            user.setPassword(passHash);
            userRepository.save(user);
            System.out.println("PASSWORD CHANGE!");
            return true;
        }
        return false;
    }

    @Transactional
    public List<RoleDTO> getAllRoles() {
        List<Roles> roles= rolesRepository.findAll();

        return roles.stream().map(role -> new RoleDTO(role.getId(), role.getRole())).collect(Collectors.toList());
    }

    @Transactional
    public boolean addRole(String role) {
        System.out.println("Find if exists Role: " + Role.valueOf(role));
        if (Role.valueOf(role) != null) {
//            System.out.println();
            if (rolesRepository.existsByRole(Role.valueOf(role))) {
                return false;
            }
        }

        System.out.println("Try to save Role");
        Roles r = new Roles(Role.valueOf(role));
        System.out.println("Role: " + r);
        rolesRepository.save(r);
        return true;
    }

    @Transactional
    public boolean addRolesToUser(Long id, List<Long> ids) {
        Optional<CustomUser> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()) {
            CustomUser user = userOptional.get();
            List<Roles> currentRoles = user.getRoles(); // получаем список ролей, который есть у пользователя
            List<Roles> sentRoles = rolesRepository.findAllById(ids); // получаем список полученных ролей
            List<Roles> roles = Stream.concat(currentRoles.stream(), sentRoles.stream())
                                    .distinct()
                                    .collect(Collectors.toList());
            user.setRoles(roles);
            return true;
        }
        return false;

    }


}
