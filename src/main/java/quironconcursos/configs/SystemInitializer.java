package quironconcursos.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import quironconcursos.entities.AccountTypeEntity;
import quironconcursos.entities.RoleEntity;
import quironconcursos.entities.StatusEntity;
import quironconcursos.entities.UserEntity;
import quironconcursos.repositories.AccountTypeRepository;
import quironconcursos.repositories.RoleRepository;
import quironconcursos.repositories.StatusRepository;
import quironconcursos.repositories.UserRepository;

import java.time.Instant;

@Configuration
public class SystemInitializer implements CommandLineRunner {

    @Value("${app.security.root-email}")
    private String rootEmail;

    @Value("${app.security.token-jwt.reset-password-token.expire-length}")
    private Long validityInMilliSecondsResetPasswordToken;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        this.createUserROOT();
    }

    @Transactional
    private void createUserROOT() {
        if (userRepository.findByUsername("ROOT").isEmpty()) {
            
            // Ensure ROLE_ROOT exists
            RoleEntity roleEntity = roleRepository.findByName("ROLE_ROOT")
                    .orElseGet(() -> {
                        RoleEntity newRole = new RoleEntity();
                        newRole.setName("ROLE_ROOT");
                        return roleRepository.save(newRole);
                    });

            // Ensure Account Type exists
            AccountTypeEntity accountTypeEntity = accountTypeRepository.findByName("premium")
                    .orElseGet(() -> {
                        AccountTypeEntity newAccountType = new AccountTypeEntity();
                        newAccountType.setName("premium");
                        return accountTypeRepository.save(newAccountType);
                    });

            // Ensure Status exists
            StatusEntity statusEntity = statusRepository.findByName("active")
                    .orElseGet(() -> {
                        StatusEntity newStatus = new StatusEntity();
                        newStatus.setName("active");
                        return statusRepository.save(newStatus);
                    });

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername("ROOT");
            userEntity.setEmail(rootEmail);
            userEntity.setPassword(""); // Consider encoding this if using an encoder
            userEntity.setRegistrationDate(Instant.now());
            userEntity.setLastLogin(Instant.now());
            userEntity.setLastRecoverPassword(Instant.now().minusMillis(validityInMilliSecondsResetPasswordToken));
            userEntity.setReceiveEmails(true);

            userEntity.setRole(roleEntity);
            userEntity.setAccountType(accountTypeEntity);
            userEntity.setStatus(statusEntity);

            userRepository.save(userEntity);
        }
    }

}
