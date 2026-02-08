package quironconcursos.controllers.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quironconcursos.dto.api.auth.*;
import quironconcursos.services.api.AuthService;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        RegisterResponseDTO response = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/ticket-anonymous")
    public ResponseEntity<TicketResponseDTO> ticketAnonymous() {
        TicketResponseDTO response = authService.ticketAnonymous();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/ticket")
    public ResponseEntity<TicketResponseDTO> ticket() {
        TicketResponseDTO response = authService.ticket();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping(path = "/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequestDTO dto) {
        authService.changePassword(dto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(path = "/recover-password")
    public ResponseEntity<Void> recoverPassword(@RequestBody @Valid RecoverPasswordRequestDTO dto) {
        authService.recoverPassword(dto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(path = "/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
