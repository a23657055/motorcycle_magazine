package org.iii.SecBuzzer.template.service;

import java.io.IOException;

import org.iii.SecBuzzer.template.util.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

@Service
public class GCPService {
	final static Logger logger = LoggerFactory.getLogger(GCPService.class);

	private static Storage storage;

	static {
		try {
			GoogleCredentials credentials = GoogleCredentials.fromStream(File.readFileFromResource("gcp.json"))
					.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String googleStorageName;

	@Value("${google.storage.name}")
	public void setGoogleStorageName(String _googleStorageName) {
		googleStorageName = _googleStorageName;
	}

	private static String googleStorageUrl;

	@Value("${google.storage.url}")
	public void setGoogleStorageUrl(String _googleStorageUrl) {
		googleStorageUrl = _googleStorageUrl;
	}

	public static String uploadFile(MultipartFile file, String filePathAndName, boolean isPublic) throws IOException {
		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		String mimeType = file.getContentType();
		extension = (extension != null && !extension.isEmpty()) ? "." + extension : extension;
		BlobId blobId = BlobId.of(googleStorageName, filePathAndName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
		storage.create(blobInfo, file.getBytes());
		if (isPublic == true) {
			storage.createAcl(blobId, Acl.of(User.ofAllUsers(), Role.READER));
		}

		return googleStorageUrl + googleStorageName + "/" + filePathAndName;
	}
}
