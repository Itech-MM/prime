package org.flexitech.projects.erp.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtils {

	@Value("${file.storage.path}")
	private String storagePath;

	@Value("${file.storage.context.path}")
	private String storageContextPath;

	@Value("${server.servlet.context-path}")
	private String applicationContextPath;

	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

	public String uploadFile(MultipartFile file, String entityType, Long entityId) throws IOException {
		validateFile(file);
		String originalFileName = file.getOriginalFilename();
		String fileExtension = getFileExtension(originalFileName);
		String uniqueFileName = generateUniqueFileName(fileExtension);
		return writeBytes(file.getBytes(), entityType, entityId, uniqueFileName);
	}

	public String uploadFile(File file, String entityType, Long entityId) throws IOException {
		validateFile(file);
		String fileExtension = getFileExtension(file.getName());
		String uniqueFileName = generateUniqueFileName(fileExtension);
		return writeBytes(Files.readAllBytes(file.toPath()), entityType, entityId, uniqueFileName);
	}

	public String writeFile(String content, String entityType, Long entityId, String fileName) throws IOException {
		return writeBytes(content.getBytes(), entityType, entityId, fileName);
	}

	public String writeFile(byte[] content, String entityType, Long entityId, String fileName) throws IOException {
		return writeBytes(content, entityType, entityId, fileName);
	}
	
	public String writeKeyFile(String content, String entityType, Long entityId, String fileName) throws IOException {
		String entityDirectory = entityType.toLowerCase();
		String uploadPath = Paths.get(storagePath, entityDirectory, entityId.toString(), "keys").toString();
		File directory = new File(uploadPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path filePath = Paths.get(uploadPath, fileName);
		Files.write(filePath, content.getBytes());
		return Paths.get(storageContextPath, entityDirectory, entityId.toString(), "keys", fileName).toString()
				.replace("\\", "/");
	}

	public byte[] readFileBytes(String fileUrl) throws IOException {
		Path filePath = resolveAbsolutePath(fileUrl);
		if (!Files.exists(filePath)) {
			throw new IOException("File does not exist: " + filePath);
		}
		return Files.readAllBytes(filePath);
	}

	public String readFileAsString(String fileUrl) throws IOException {
		return new String(readFileBytes(fileUrl));
	}

	public void deleteFile(String fileUrl) throws IOException {
		if (fileUrl == null || !fileUrl.startsWith(storageContextPath)) {
			return;
		}
		Path filePath = resolveAbsolutePath(fileUrl);
		if (Files.exists(filePath)) {
			Files.delete(filePath);
			deleteEmptyParentDirectories(filePath.getParent());
		}
	}

	public void deleteAllEntityFiles(String entityType, Long entityId) throws IOException {
		String entityDirectory = entityType.toLowerCase() + "s";
		Path entityPath = Paths.get(storagePath, entityDirectory, entityId.toString());
		if (Files.exists(entityPath)) {
			Files.walk(entityPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	public String updateFile(MultipartFile newFile, String oldFileUrl, String entityType, Long entityId)
			throws IOException {
		if (oldFileUrl != null && !oldFileUrl.trim().isEmpty()) {
			deleteFile(oldFileUrl);
		}
		return uploadFile(newFile, entityType, entityId);
	}

	public String copyFile(String sourceFileUrl, String targetEntityType, Long targetEntityId) throws IOException {
		if (sourceFileUrl == null || sourceFileUrl.trim().isEmpty()) {
			throw new IllegalArgumentException("Source file URL cannot be null or empty");
		}
		Path sourcePath = resolveAbsolutePath(sourceFileUrl);
		if (!Files.exists(sourcePath)) {
			throw new IOException("Source file does not exist: " + sourcePath);
		}
		String fileExtension = getFileExtension(sourcePath.getFileName().toString());
		String newFileName = generateUniqueFileName(fileExtension);
		return writeBytes(Files.readAllBytes(sourcePath), targetEntityType, targetEntityId, newFileName);
	}

	public String uploadFileFromBase64(String base64Content, String entityType, Long entityId, String fileExtension)
			throws IOException {
		if (base64Content == null || base64Content.trim().isEmpty()) {
			throw new IllegalArgumentException("Base64 content cannot be null or empty");
		}
		String[] parts = base64Content.split(",");
		String data = parts.length == 2 ? parts[1] : base64Content;
		byte[] fileBytes;
		try {
			fileBytes = Base64.getDecoder().decode(data);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid base64 encoding", e);
		}
		if (fileBytes.length > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit");
		}
		String uniqueFileName = generateUniqueFileName(fileExtension);
		return writeBytes(fileBytes, entityType, entityId, uniqueFileName);
	}

	public String uploadFileFromUrl(String fileUrl, String entityType, Long entityId) throws IOException {
		if (fileUrl == null || fileUrl.trim().isEmpty()) {
			throw new IllegalArgumentException("File URL cannot be null or empty");
		}
		URL url = new URL(fileUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(10000);
		conn.setInstanceFollowRedirects(true);
		conn.connect();
		int responseCode = conn.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException("Failed to download file: HTTP " + responseCode);
		}
		long contentLength = conn.getContentLengthLong();
		if (contentLength > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit");
		}
		byte[] fileBytes;
		try (InputStream inputStream = conn.getInputStream()) {
			fileBytes = inputStream.readAllBytes();
		}
		if (fileBytes.length > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit");
		}
		String fileExtension = getFileExtension(fileUrl);
		String uniqueFileName = generateUniqueFileName(fileExtension);
		return writeBytes(fileBytes, entityType, entityId, uniqueFileName);
	}

	public String getFileUrl(String relativePath) {
		if (relativePath == null || relativePath.trim().isEmpty()) {
			return null;
		}
		return applicationContextPath + relativePath;
	}

	private String writeBytes(byte[] content, String entityType, Long entityId, String fileName) throws IOException {
		String entityDirectory = entityType.toLowerCase() + "s";
		String uploadPath = Paths.get(storagePath, entityDirectory, entityId.toString()).toString();
		File directory = new File(uploadPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Path filePath = Paths.get(uploadPath, fileName);
		Files.write(filePath, content);
		return Paths.get(storageContextPath, entityDirectory, entityId.toString(), fileName).toString()
				.replace("\\", "/");
	}

	private Path resolveAbsolutePath(String fileUrl) {
		String relativePath = fileUrl.substring(storageContextPath.length());
		return Paths.get(storagePath, relativePath);
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be null or empty");
		}
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit");
		}
	}

	private void validateFile(File file) {
		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("File cannot be null or does not exist");
		}
		if (file.length() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum limit");
		}
	}

	private String getFileExtension(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf("."));
	}

	private String generateUniqueFileName(String fileExtension) {
		return UUID.randomUUID().toString() + fileExtension;
	}

	private void deleteEmptyParentDirectories(Path directory) throws IOException {
		while (directory != null && Files.exists(directory)) {
			try (Stream<Path> stream = Files.list(directory)) {
				if (stream.findAny().isPresent()) {
					break;
				}
			}
			Files.delete(directory);
			directory = directory.getParent();
			if (directory != null && directory.toString().equals(storagePath)) {
				break;
			}
		}
	}
}