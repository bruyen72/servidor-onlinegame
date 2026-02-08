package quironconcursos.services.api;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import quironconcursos.entities.UserEntity;
import quironconcursos.exceptions.app.AuthenticationException;
import quironconcursos.repositories.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if (userEntity.getStatus().getName().equals("banned")) {
            throw new AuthenticationException("Banned user");
        }

        RoleSecurityEntity roleSecurityEntity = new RoleSecurityEntity(userEntity.getRole().getName());

        return new UserSecurityEntity(
                userEntity.getUsername(),
                userEntity.getPassword(),
                roleSecurityEntity
        );
    }

}

@AllArgsConstructor
class RoleSecurityEntity implements GrantedAuthority {

    private String name;

    @Override
    public String getAuthority() {
        return name;
    }

}

@AllArgsConstructor
class UserSecurityEntity implements UserDetails {

    private String username;
    private String password;

    private RoleSecurityEntity roleSecurityEntity;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(roleSecurityEntity);
    }

}
