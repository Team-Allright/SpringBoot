package allright.springboot.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

	private final S3Operations s3Operations;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	public String uploadFileAndGetSignedUrl(MultipartFile multipartFile, String requestId) {
		try {
			InputStream fileInputStream = multipartFile.getInputStream();
			String fileName = multipartFile.getOriginalFilename();
			s3Operations.upload(bucket, requestId, fileInputStream, ObjectMetadata.builder().contentType(multipartFile.getContentType()).build());
			URL signedGetURL = s3Operations.createSignedGetURL(bucket, fileName, Duration.of(1, ChronoUnit.HOURS));
			return signedGetURL.toString();
		} catch (IOException e) {
			log.error("Error uploading file to S3: {}", e.getMessage());
			return null;
		}
	}
}
