package local.fabricarag.core.repository;

import local.fabricarag.core.domain.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByPublicIdAndSecretHash(String publicId, String secretHash);
    List<ApiKey> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
