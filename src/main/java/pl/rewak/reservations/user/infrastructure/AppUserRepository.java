package pl.rewak.reservations.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rewak.reservations.user.domain.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByKeycloakId(String keycloakId);
}
