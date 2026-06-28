package local.fabricarag.core.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    // Ideally this comes from application properties (e.g., fabricarag.storage.base-dir)
    private final String baseStorageDir = System.getProperty("user.home") + "/.ragcreator/storage";

    /**
     * Generates a storage URI for a new document.
     */
    public String generateStorageUri(UUID workspaceId, UUID documentId, String filename) {
        Path path = Paths.get(baseStorageDir, workspaceId.toString(), "documents", documentId.toString(), filename);
        return "file://" + path.toString().replace("\\", "/");
    }

    /**
     * Saves the content to the local filesystem.
     */
    public void saveFile(UUID workspaceId, UUID documentId, String filename, java.io.InputStream content) {
        try {
            Path path = Paths.get(baseStorageDir, workspaceId.toString(), "documents", documentId.toString(), filename);
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.copy(content, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
