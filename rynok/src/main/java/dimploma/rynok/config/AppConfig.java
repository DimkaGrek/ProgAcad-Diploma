package dimploma.rynok.config;

import dimploma.rynok.enums.Role;
import dimploma.rynok.model.Roles;
import dimploma.rynok.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends GlobalMethodSecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public CommandLineRunner demo(final UserService userService,
                                  final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                userService.addRole("ADMIN");
                userService.addRole("USER");
                Roles roleAdmin = new Roles(Role.ADMIN);
                List<Roles> roles = new ArrayList<>();
                roles.add(roleAdmin);
                userService.addUser("admin",
                        encoder.encode("password"),
                        "dmytrozinkovsky@gmail.com", "Dmytro Zinkovsky", "CEO", "+380687016066", "this is admin", 0L, roles);

            }
        };
    }
}
