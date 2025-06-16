package allright.springboot.controller;

import java.util.Map;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import allright.springboot.dto.TaskProgressDto;
import allright.springboot.service.TaskTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TaskMessagingController {

	private final SimpMessagingTemplate messagingTemplate;
	private final TaskTriggerService taskTriggerService;
	private final MongoTemplate mongoTemplate;

	@MessageMapping("/task/{requestId}")
	public void handleTasProcess(
		@DestinationVariable String requestId,
		@Payload TaskProgressDto.Payload payload
	) {
		log.info("[Request From Client] /topic/task/{} TaskProgressDto = {}", requestId, payload);
		taskTriggerService.requestNextTask(payload, requestId);
	}

	@MessageMapping("/task/save/{requestId}")
	public void saveAll(@Payload Map<String, Object> payload, @DestinationVariable String requestId) {
		log.info("[Request From Client] /topic/task/save/{} payload = {}", requestId, payload);
		Document payloadDocument = new Document(payload);
		mongoTemplate.getCollection("taskResult")
			.insertOne(payloadDocument);
		log.info("[Save To MongoDB] /topic/task/save/{} payloadDocument = {}", requestId, payloadDocument);

		TaskProgressDto.ClientResponse clientResponse = TaskProgressDto.ClientResponse.builder()
			.requestId(requestId)
			.isCompleted(true)
			.build();
		messagingTemplate.convertAndSend("/topic/task/save/" + requestId, clientResponse);
		log.info("[Send Message To Client] /topic/task/save/{} ClientResponse = {}", requestId, clientResponse);
	}
}

