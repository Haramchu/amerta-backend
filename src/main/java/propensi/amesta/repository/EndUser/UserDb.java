package propensi.amesta.repository.EndUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.EndUser.User;

@Repository
public interface UserDb extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

}
