package dimploma.rynok.controllers;

import dimploma.rynok.dto.RoleDTO;
import dimploma.rynok.dto.UserDTO;
import dimploma.rynok.model.Roles;
import dimploma.rynok.services.SharedService;
import dimploma.rynok.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/api/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/api/users")
    public ResponseEntity<String> createUser(@RequestParam String login,
                                             @RequestParam String password,
                                             @RequestParam String email,
                                             @RequestParam String realname,
                                             @RequestParam String position,
                                             @RequestParam String phone,
                                             @RequestParam String notes,
                                             @RequestParam Long permission,
                                             @RequestParam List<Long> roleIds) {
        String passHash = passwordEncoder.encode(password);

        List<Roles> roles = userService.getRolesById(roleIds);

        if ( ! userService.addUser(login, passHash, email, realname, position, phone, notes, permission, roles)) {
            String error = "User with this login already exist";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/api/users/password/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestParam String password) {
        String passHash = passwordEncoder.encode(password);
        if ( ! userService.changePassword(userId, passHash)) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/api/users/{id}/roles")
    public List<RoleDTO> getUserRoles(@PathVariable Long id) {return userService.getUserRoles(id);}

    @PostMapping("/api/users/{id}/roles") // add roles to user
    public ResponseEntity<String> addRolesToUser(@PathVariable Long id, @RequestParam List<Long> ids) {
        if (userService.addRolesToUser(id, ids)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(null);
    }

    @GetMapping("/api/roles")
    public List<RoleDTO> getAllRoles() {
        System.out.println("Below ALl Roles");
        return userService.getAllRoles();
    }

    @PostMapping("/api/roles")
    public ResponseEntity<String> createRole(@RequestParam String role) {
        System.out.println("API/ROLES");
        if ( ! userService.addRole(role)) {
            String error = "{\"This role already exists\"}";
            System.out.println(error);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/api/current")
    public ResponseEntity<User> showCurrentUser() {
        User user = SharedService.getCurrentUser();
        System.out.print("isAdmin: ");
        System.out.println(isAdmin(user));
        System.out.println(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK.value()).body(user);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            // тут можно использовать userDetails
//            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
//            System.out.println("User roles:");
//            for (GrantedAuthority authority : authorities) {
//                System.out.println(authority.getAuthority());
//            }
//        } else {
//            // пользователь не аутентифицирован
//            System.out.println("пользователь не аутентифицирован");
//        }
    }

    @GetMapping("/api/logout")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }

//    @PostMapping("/logout")
//
//    public void logoutPage(HttpServletRequest request, HttpServletResponse response) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            new SecurityContextLogoutHandler().logout(request, response, auth);
//        }
//        return "redirect:/";
//    }

    public boolean isAdmin(User user) {
        Collection<GrantedAuthority> roles = user.getAuthorities();

        for (GrantedAuthority auth : roles) {
            if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
