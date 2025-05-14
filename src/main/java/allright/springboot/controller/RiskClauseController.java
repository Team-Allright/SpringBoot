package allright.springboot.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.RiskClauseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/risk_special_clause")
public class RiskClauseController {

	private final SimpMessagingTemplate messagingTemplate;

	@PostMapping
	public void riskClauseTest(
		@RequestBody RiskClauseDto.Request request
	) {
		messagingTemplate.convertAndSend("/topic/risk_special_clause/" + request.getRequestId() , request);
	}


}
