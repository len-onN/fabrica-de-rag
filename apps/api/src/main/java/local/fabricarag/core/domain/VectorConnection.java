package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vector_connections")
public class VectorConnection {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "owner_user_id")
    private String ownerUserId;

    @Column(name = "owner_scope", nullable = false)
    private String ownerScope;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "mode", nullable = false)
    private String mode;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "auth_secret_ref")
    private String authSecretRef;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "capabilities", nullable = false)
    private String capabilities;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    protected VectorConnection() {}

    public VectorConnection(UUID id, String publicId, String workspaceId, String ownerUserId, String ownerScope,
                            String name, String provider, String mode, String endpoint, String authSecretRef,
                            String capabilities) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.ownerUserId = ownerUserId;
        this.ownerScope = ownerScope;
        this.name = name;
        this.provider = provider;
        this.mode = mode;
        this.endpoint = endpoint;
        this.authSecretRef = authSecretRef;
        this.capabilities = capabilities;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public String getPublicId() { return publicId; }
    public String getWorkspaceId() { return workspaceId; }
    public String getOwnerUserId() { return ownerUserId; }
    public String getOwnerScope() { return ownerScope; }
    public String getName() { return name; }
    public String getProvider() { return provider; }
    public String getMode() { return mode; }
    public String getEndpoint() { return endpoint; }
    public String getAuthSecretRef() { return authSecretRef; }
    public String getCapabilities() { return capabilities; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
}
