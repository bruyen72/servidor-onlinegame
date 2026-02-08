package quironconcursos.controllers.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quironconcursos.dto.api.admin.FindAllUsersResponseDTO;
import quironconcursos.dto.api.admin.SendEmailRequestDTO;
import quironconcursos.services.api.AdminService;

@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping(path = "/users")
    public ResponseEntity<Page<FindAllUsersResponseDTO>> findAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        Page<FindAllUsersResponseDTO> response = adminService.findAllUsers(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/send-email")
    public ResponseEntity<Void> sendEmail(@RequestBody @Valid SendEmailRequestDTO dto) {
        adminService.sendEmail(dto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
