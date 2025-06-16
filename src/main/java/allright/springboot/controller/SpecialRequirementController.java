package allright.springboot.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.SpecialRequirementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check_special_clause")
public class SpecialRequirementController {

	private final SimpMessagingTemplate messagingTemplate;

	@PostMapping
	public void specialRequirementTest(
		@RequestBody SpecialRequirementDto.Request request
	) {
		log.info("[Request From AI] /topic/check_special_clause/{} SpecialRequirementDto = {}", request.getRequestId(), request);
		SpecialRequirementDto.ClientResponse clientResponse = SpecialRequirementDto.ClientResponse.builder()
			.requestId(request.getRequestId())
			.isViolated(request.getIsViolated())
			.message(request.getMessage())
			.missingLabelList(request.getMissingLabelList())
			.succestionClauseList(request.getSuggestionCluaseList())
			.build();
		messagingTemplate.convertAndSend("/topic/check_special_clause/" + request.getRequestId() , clientResponse);
		log.info("[Send Message To Client]  topic/check_special_clause/{} ClientResponse = {}", request.getRequestId(),
			clientResponse);
	}
}
