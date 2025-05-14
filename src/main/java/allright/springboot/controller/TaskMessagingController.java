package allright.springboot.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.TaskProgressDto;
import allright.springboot.service.TaskTriggerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaskMessagingController {

	private final TaskTriggerService taskTriggerService;

	@MessageMapping("/task/{requestId}")
	public void handleTasProcess(
		@DestinationVariable String requestId,
		@Payload TaskProgressDto.Payload payload
	) {
		taskTriggerService.requestNextTask(payload, requestId);
	}
}

