package quironconcursos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quironconcursos.entities.AccountTypeEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountTypeEntity, UUID> {

    Optional<AccountTypeEntity> findByName(String name);

}
