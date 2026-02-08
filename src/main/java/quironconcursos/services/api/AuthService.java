package quironconcursos.services.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quironconcursos.dto.api.auth.*;
import quironconcursos.entities.AccountTypeEntity;
import quironconcursos.entities.RoleEntity;
import quironconcursos.entities.StatusEntity;
import quironconcursos.entities.UserEntity;
import quironconcursos.exceptions.app.AuthenticationException;
import quironconcursos.exceptions.app.NotFoundException;
import quironconcursos.exceptions.app.ValidationException;
import quironconcursos.repositories.AccountTypeRepository;
import quironconcursos.repositories.RoleRepository;
import quironconcursos.repositories.StatusRepository;
import quironconcursos.repositories.UserRepository;
import quironconcursos.services.common.JWTService;
import quironconcursos.services.common.SMTPService;
import quironconcursos.util.SecurityUtil;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

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

    @Autowired
    private SMTPService SMTPService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new ValidationException("Username already exists");
        }

        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new ValidationException("E-mail already exists");
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(dto.username());
        userEntity.setEmail(dto.email());
        userEntity.setPassword(passwordEncoder.encode(dto.password()));
        userEntity.setRegistrationDate(Instant.now());
        userEntity.setLastLogin(Instant.now());
        userEntity.setLastRecoverPassword(Instant.now().minusMillis(validityInMilliSecondsResetPasswordToken));
        userEntity.setReceiveEmails(dto.receiveEmails());

        RoleEntity roleEntity = roleRepository
                .findByName("ROLE_USER")
                .orElseThrow(RuntimeException::new);

        AccountTypeEntity accountTypeEntity = accountTypeRepository
                .findByName("standard")
                .orElseThrow(RuntimeException::new);

        StatusEntity statusEntity = statusRepository
                .findByName("active")
                .orElseThrow(RuntimeException::new);

        userEntity.setRole(roleEntity);
        userEntity.setAccountType(accountTypeEntity);
        userEntity.setStatus(statusEntity);

        userRepository.save(userEntity);

        SMTPService.sendEmailRegister(userEntity.getUsername(), userEntity.getEmail());

        return new RegisterResponseDTO(
                jwtService.createAccessToken(
                        userEntity.getUsername(),
                        userEntity.getRole().getName()
                )
        );
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        UserEntity userEntity = userRepository
                .findByEmail(dto.email())
                .orElseThrow(() -> new AuthenticationException("Incorrect e-mail or password. Please try again"));

        if (!passwordEncoder.matches(dto.password(), userEntity.getPassword())) {
            throw new AuthenticationException("Incorrect e-mail or password. Please try again");
        }

        userEntity.setLastLogin(Instant.now());
        userRepository.save(userEntity);

        return new LoginResponseDTO(
                jwtService.createAccessToken(
                        userEntity.getUsername(),
                        userEntity.getRole().getName()
                )
        );
    }

    @Transactional
    public TicketResponseDTO ticketAnonymous() {
        String uuid = UUID.randomUUID().toString();
        String hash = Integer.toHexString(uuid.hashCode());
        String username = "Guest_" + hash;

        return new TicketResponseDTO(
                username,
                jwtService.createTicketToken(
                        username,
                        "ROLE_USER"
                )
        );
    }

    @Transactional
    public TicketResponseDTO ticket() {
        UserEntity userEntity = SecurityUtil.findUserLogged(userRepository);

        userEntity.setLastLogin(Instant.now());
        userRepository.save(userEntity);

        return new TicketResponseDTO(
                userEntity.getUsername(),
                jwtService.createTicketToken(
                        userEntity.getUsername(),
                        userEntity.getRole().getName()
                )
        );
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDTO dto) {
        UserEntity userEntity = SecurityUtil.findUserLogged(userRepository);

        if (!passwordEncoder.matches(dto.currentPassword(), userEntity.getPassword())) {
            throw new AuthenticationException("Incorrect password");
        }

        userEntity.setPassword(passwordEncoder.encode(dto.newPassword()));

        userRepository.save(userEntity);

        SMTPService.sendEmailChangePassword(userEntity.getUsername(), userEntity.getEmail());
    }

    @Transactional
    public void recoverPassword(RecoverPasswordRequestDTO dto) {
        UserEntity userEntity = userRepository
                .findByEmail(dto.email())
                .orElseThrow(() -> new NotFoundException("E-mail not found"));

        Instant cooldownPeriod = Instant.now().minusMillis(validityInMilliSecondsResetPasswordToken);

        if (userEntity.getLastRecoverPassword().isAfter(cooldownPeriod)) {
            throw new ValidationException("Please wait before requesting another password reset");
        }

        String resetPasswordToken = jwtService
                .createResetPasswordToken(userEntity.getUsername(), "ROLE_RESET_PASSWORD");

        userEntity.setLastRecoverPassword(Instant.now());
        userRepository.save(userEntity);

        SMTPService.sendEmailResetPassword(userEntity.getUsername(), resetPasswordToken, userEntity.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        if (!jwtService.validateResetPasswordToken(dto.resetPasswordToken())) {
            throw new AuthenticationException("Invalid or missing token. Please request a new password reset link");
        }

        String username = jwtService.getUsername(dto.resetPasswordToken());

        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(RuntimeException::new);

        userEntity.setPassword(passwordEncoder.encode(dto.newPassword()));

        userRepository.save(userEntity);

        SMTPService.sendEmailChangePassword(userEntity.getUsername(), userEntity.getEmail());
    }

}
