package allright.springboot.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.BaseClauseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check_clause")
public class BaseClauseController {

	private final SimpMessagingTemplate messagingTemplate;

	@PostMapping
	public void clauseTest(
		@RequestBody BaseClauseDto.Request request
	) {
		log.info("[Request From AI] /topic/check_clause/{} BaseClauseDto = {}", request.getRequestId(), request);
		BaseClauseDto.ClientResponse clientResponse = BaseClauseDto.ClientResponse.builder()
			.requestId(request.getRequestId())
			.isViolated(request.getIsViolated())
			.message(request.getMessage())
			.violatedClauseList(request.getViolatedClauseList())
			.build();
		messagingTemplate.convertAndSend("/topic/check_clause/" + request.getRequestId(), clientResponse);
		log.info("[Send Message To Client] requestId = {} ClientResponse = {}", request.getRequestId(), clientResponse);
	}
}
