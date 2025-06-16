package allright.springboot.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import allright.springboot.dto.OcrResultDto;
import allright.springboot.service.ContractExtractionService;
import allright.springboot.service.DebtorService;
import allright.springboot.service.RealEstateService;
import allright.springboot.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
public class ContractOcrController {

	private final ContractExtractionService contractExtractionService;
	private final RealEstateService realEstateService;
	private final SimpMessagingTemplate messagingTemplate;
	private final ObjectMapper objectMapper;
	private final S3Service s3Service;
	private final DebtorService debtorService;

	/**
	 * <h3>(테스트용 임시 api -> 수정 필요) 계약서 이미지 수신 및 OCR 처리 요청</h3>
	 * </br> 1. 계약서 이미지 파일 수신
	 * <br> 2. OCR 처리 요청
	 * <br> 3. 처리 결과를 WebSocket을 통해 클라이언트에 전송 (/topic/ocr/{requestId})
	 * @param contractImage 계약서 이미지 파일
	 * @param requestId     요청 ID
	 * @return OCR 처리 결과
	 */
	@PostMapping("/ocr")
	public ResponseEntity<?> requestContractAnalyze(
		@RequestPart MultipartFile contractImage,
		@RequestParam String requestId
	) {
		log.info("[Request From Client] /contract/ocr/{} MultipartFile = {}", requestId,
			contractImage.getOriginalFilename());
		// s3 업로드
		String uploadedFileUrl = s3Service.uploadFileAndGetSignedUrl(contractImage, requestId);

		// s3 url을 통해 ocr 처리 요청
		OcrResultDto.Response data = contractExtractionService.requestOcrProcess(contractImage, requestId,
			uploadedFileUrl);

		log.info("[Response From AI] /contract/ocr/{} OcrResultDto.Response = {}", requestId, data);

		messagingTemplate.convertAndSend("/topic/ocr/" + requestId, data);

		log.info("[Send Message To Client] /topic/ocr/{} OcrResultDto.Response = {}", requestId, data);

		return ResponseEntity.ok(data);
	}

	@PostMapping("/ocr/test")
	public ResponseEntity<?> requestContractAnalyzeTest(
		@RequestPart MultipartFile contractImage,
		@RequestParam String requestId
	) throws IOException {
		log.info("requet");
		// File ocrResult = new File("src/main/resources/static/ocr-result.json");
		// OcrResultDto.Response data = objectMapper.readValue(ocrResult, OcrResultDto.Response.class);

		// messagingTemplate.convertAndSend("/topic/ocr/" + requestId, data);

		// String signedGetUrl = s3Service.uploadFile(contractImage);

		// return ResponseEntity.ok(signedGetUrl);
		return null;
	}

	// faskapi ocr 처리결과 수신 및 클라이언트 전송
	@PostMapping("/ocr/result")
	public ResponseEntity<?> getAndSendOcrResult(
		@RequestBody OcrResultDto.Response ocrResultDto
	) {
		log.info("[Request From AI] /ocr/result OcrResultDto = {}", ocrResultDto);
		messagingTemplate.convertAndSend("/topic/ocr/" + ocrResultDto.getRequestId(), ocrResultDto);
		log.info("[Send Message To Client] /topic/ocr/{} OcrResultDto = {}", ocrResultDto.getRequestId(), ocrResultDto);

		return ResponseEntity.ok(null);
	}

	@MessageMapping("/ocr/seller/{requestId}")
	@SendToUser
	public boolean handleSellerInformation(
		@Payload OcrResultDto.SellerInformation sellerInformation,
		@DestinationVariable String requestId
	) throws Exception {
		log.info("[Request From Client] /ocr/seller/{} SellerInformation = {}", requestId, sellerInformation);
		// 등기부등본 api 호출 및 mongodb 저장

		JsonNode realEstate = realEstateService.lookupAndConfirm(sellerInformation.getPhoneNumber(),
			sellerInformation.getPropertyAddress());
		// 결과 생성 -> 악성임대인 정보 조회(mongodb) , 등기부등본 조회
		JsonNode debtor = debtorService.findDebtorByNameAndAddress(
			sellerInformation.getName(), sellerInformation.getLessorAddress());

		ObjectNode data = objectMapper.createObjectNode();
		data.set("realEstate", realEstate);
		data.set("debtor", debtor);

		// 클라이언트에게 등기부등본 및 악성 임대인 결과 전송
		messagingTemplate.convertAndSend("/topic/seller_risk/" + requestId, data);
		log.info("[Send Message To Client] /topic/seller_risk/{} Data = {}", requestId, data);
		return true;
	}
}

