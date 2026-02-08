package quironconcursos.services.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import quironconcursos.dto.api.admin.FindAllUsersResponseDTO;
import quironconcursos.dto.api.admin.SendEmailRequestDTO;
import quironconcursos.entities.UserEntity;
import quironconcursos.exceptions.app.ValidationException;
import quironconcursos.repositories.UserRepository;
import quironconcursos.services.common.SMTPService;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SMTPService SMTPService;

    public Page<FindAllUsersResponseDTO> findAllUsers(Pageable pageable) {
        if (pageable.getPageSize() != 10) {
            throw new ValidationException("Page size must be 10");
        }

        int maxPage = Integer.MAX_VALUE / pageable.getPageSize();

        if (pageable.getPageNumber() > maxPage) {
            throw new ValidationException("Page number exceeds the maximum allowed value");
        }

        Page<UserEntity> usersEntities = userRepository
                .findByUsernameNot("ROOT", pageable);

        return usersEntities
                .map(userEntity -> new FindAllUsersResponseDTO(
                        userEntity.getId(),
                        userEntity.getUsername(),
                        userEntity.getEmail(),
                        userEntity.getRegistrationDate(),
                        userEntity.getLastLogin(),
                        userEntity.isReceiveEmails(),
                        userEntity.getRole().getName(),
                        userEntity.getAccountType().getName(),
                        userEntity.getStatus().getName()
                ));
    }

    public void sendEmail(SendEmailRequestDTO dto) {
        List<String> emails = userRepository.findAllEmails();

        SMTPService.sendEmailAdmin(emails, dto.title(), dto.content());
    }

}
