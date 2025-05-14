package allright.springboot.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.SpecialRequirementDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check_special_clause")
public class SpecialRequirementController {

	private final SimpMessagingTemplate messagingTemplate;

	@PostMapping
	public void specialRequirementTest(
		@RequestBody SpecialRequirementDto.Request request
	) {
		messagingTemplate.convertAndSend("/topic/check_special_clause/" + request.getRequestId() , request);
	}
}
