package local.fabricarag.core.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "public_id", nullable = false, unique = true, length = 50)
    private String publicId;

    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(nullable = false, length = 50)
    private String role = "agent";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> capabilities;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public ApiKey() {}

    public ApiKey(String publicId, String secretHash, Workspace workspace, List<String> capabilities, OffsetDateTime expiresAt, User createdByUser) {
        this.publicId = publicId;
        this.secretHash = secretHash;
        this.workspace = workspace;
        this.capabilities = capabilities;
        this.expiresAt = expiresAt;
        this.createdByUser = createdByUser;
    }

    public UUID getId() { return id; }
    public String getPublicId() { return publicId; }
    public String getSecretHash() { return secretHash; }
    public Workspace getWorkspace() { return workspace; }
    public String getRole() { return role; }
    public List<String> getCapabilities() { return capabilities; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public OffsetDateTime getRevokedAt() { return revokedAt; }
    public User getCreatedByUser() { return createdByUser; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setRevokedAt(OffsetDateTime revokedAt) { this.revokedAt = revokedAt; }
}
