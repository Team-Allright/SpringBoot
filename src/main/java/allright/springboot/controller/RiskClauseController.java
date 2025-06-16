package allright.springboot.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.RiskClauseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/risk_special_clause")
public class RiskClauseController {

	private final SimpMessagingTemplate messagingTemplate;

	@PostMapping
	public void riskClauseTest(
		@RequestBody RiskClauseDto.Request request
	) {
		log.info("[Request From AI] /topic/risk_special_clause/{} RiskClauseDto = {}", request.getRequestId(), request);
		RiskClauseDto.ClientResponse clientResponse = RiskClauseDto.ClientResponse.builder()
			.requestId(request.getRequestId())
			.isViolated(request.getIsViolated())
			.riskClauses(request.getRiskClauses())
			.message(request.getMessage())
			.build();
		messagingTemplate.convertAndSend("/topic/risk_special_clause/" + request.getRequestId() , clientResponse);
		log.info("[Send Message To Client]  topic/risk_special_clause/{} ClientResponse = {}", request.getRequestId(), clientResponse);
	}
}
