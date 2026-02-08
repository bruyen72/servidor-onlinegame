package quironconcursos.util;

import org.springframework.security.core.context.SecurityContextHolder;
import quironconcursos.entities.UserEntity;
import quironconcursos.repositories.UserRepository;

public class SecurityUtil {

    public static UserEntity findUserLogged(UserRepository userRepository) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
    }

}
