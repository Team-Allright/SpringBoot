package allright.springboot.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import allright.springboot.dto.OcrResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractExtractionService {

	@Value("${ai.request.url}")
	private String requestUrl;

	// TODO: s3 url을 넘겨주는 방법으로 변경
	public OcrResultDto.Response requestOcrProcess(MultipartFile contractImage, String requestId, String contractImageUrl) {

		RestTemplate restTemplate = new RestTemplate();

		Resource contractImageResource = contractImage.getResource();

		URI requestUri = UriComponentsBuilder.fromUriString(requestUrl + "/analyze/contract-image")
			.build()
			.toUri();

		log.info("requestUri: {}", requestUri);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", contractImageResource);
		body.add("session_id", requestId);
		body.add("file_url", contractImageUrl);

		log.info("file: {}", contractImageResource);
		log.info("requestId: {}", requestId);
		log.info("body: {}", body);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<OcrResultDto.Response> ocrProcessEntity = restTemplate.postForEntity(requestUri, requestEntity,
			OcrResultDto.Response.class);

		return ocrProcessEntity.getBody();
	}
}
