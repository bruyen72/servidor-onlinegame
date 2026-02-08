package quironconcursos.dto.api.auth;

import java.io.Serializable;

public record TicketResponseDTO(
        String username,
        String ticketToken
) implements Serializable {}
