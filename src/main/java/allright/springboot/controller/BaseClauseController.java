package allright.springboot.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.BaseClauseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check_clause")
public class BaseClauseController {

	private final SimpMessagingTemplate messagingTemplate;

	@PostMapping
	public void clauseTest(
		@RequestBody BaseClauseDto.Request request
	) {
		messagingTemplate.convertAndSend("/topic/check_clause/" + request.getRequestId(), request);
	}
}
