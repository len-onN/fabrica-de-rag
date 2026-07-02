package local.fabricarag.core.repository;

import local.fabricarag.core.domain.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"user"})
    Optional<AuthSession> findBySessionHash(String sessionHash);
    void deleteByExpiresAtBefore(OffsetDateTime time);
    void deleteByUserId(UUID userId);
}
