package dimploma.rynok.services;

import dimploma.rynok.model.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        CustomUser customUser = userService.findByLogin(login);
        //System.out.println("Login: " + customUser.getLogin());
        //System.out.println("Pass: " + customUser.getPassword());
        if (customUser == null)
            throw new UsernameNotFoundException(login + " not found");

        System.out.print("Userroles is not null: ");
        System.out.println(customUser.getRoles() != null);
        System.out.print("quantity of roles: ");
        System.out.println(customUser.getRoles().size());


        if (customUser.getRoles() != null && !customUser.getRoles().isEmpty()) {
            // у пользователя есть роли
            System.out.println("User has roles");
            List<GrantedAuthority> roles = customUser.getRoles().stream().
                map(role -> new SimpleGrantedAuthority(role.getRole().toString())).collect(Collectors.toList());
            return new User(customUser.getLogin(), customUser.getPassword(), roles);
        } else {
            // у пользователя нет ролей
            return new User(customUser.getLogin(), customUser.getPassword(), Collections.emptyList());
        }
//        List<GrantedAuthority> roles = customUser.getRoles().stream().
//                map(role -> new SimpleGrantedAuthority(role.getRole().toString())).collect(Collectors.toList());
//
//        System.out.println(roles);
//        System.out.println("Try to create User by UserDetails");
//        UserDetails u = new User(customUser.getLogin(), customUser.getPassword(), roles);
//        Collection<? extends GrantedAuthority> authorities = u.getAuthorities();
//        System.out.println("User roles:");
//        for (GrantedAuthority authority : authorities) {
//            System.out.println(authority.getAuthority());
//        }
//
//        return u;
    }
}
