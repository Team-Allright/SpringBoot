package allright.springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import allright.springboot.dto.OcrResultDto;
import allright.springboot.service.ContractExtractionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
public class ContractOcrController {

	private final ContractExtractionService contractExtractionService;
	private final SimpMessagingTemplate messagingTemplate;

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
		// s3 업로드

		// s3 url을 통해 ocr 처리 요청
		OcrResultDto.Response data = contractExtractionService.requestOcrProcess(contractImage, requestId);

		messagingTemplate.convertAndSend("/topic/ocr/" + requestId, data);

		return ResponseEntity.ok(data);
	}

	// faskapi ocr 처리결과 수신 및 클라이언트 전송
	@PostMapping("/ocr/result")
	public ResponseEntity<?> getAndSendOcrResult(
		@RequestBody OcrResultDto.Response ocrResultDto
	) {
		messagingTemplate.convertAndSend("/topic/ocr/" + ocrResultDto.getRequestId(), ocrResultDto);

		return ResponseEntity.ok(null);
	}

	@MessageMapping("/ocr/seller/{requestId}")
	public void handleSellerInformation(
		@Payload OcrResultDto.SellerInformation sellerInformation,
		@DestinationVariable String requestId
	) {
		// 등기부등본 api 호출 및 mongodb 저장

		// 결과 생성 -> 악성임대인 정보 조회(mongodb) , 등기부등본 조회

		// 클라이언트에게 등기부등본 및 악성 임대인 결과 전송
	}
}

