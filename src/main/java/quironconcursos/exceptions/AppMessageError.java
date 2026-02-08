package quironconcursos.exceptions;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record AppMessageError(
        String message,
        List<String> errors,
        Instant instant
) implements Serializable {

    public AppMessageError(String message, List<String> errors) {
        this(message, errors, Instant.now());
    }

}
