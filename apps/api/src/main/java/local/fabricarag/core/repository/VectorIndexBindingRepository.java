package local.fabricarag.core.repository;

import local.fabricarag.core.domain.VectorIndexBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VectorIndexBindingRepository extends JpaRepository<VectorIndexBinding, UUID> {
}
