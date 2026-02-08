package quironconcursos.dto.api.admin;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record FindAllUsersResponseDTO(
        UUID id,
        String username,
        String email,
        Instant registrationDate,
        Instant lastLogin,
        boolean receiveEmails,
        String role,
        String accountType,
        String status
) implements Serializable {}
