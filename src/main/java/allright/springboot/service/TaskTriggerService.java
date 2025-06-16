package allright.springboot.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import allright.springboot.dto.TaskProgressDto;
import allright.springboot.dto.TaskType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskTriggerService {

	@Value("${ai.request.url}")
	private String requestUrl;

	public void requestNextTask(TaskProgressDto.Payload payload, String requestId) {
		if (!payload.getIsCompleted()) {
			return;
		}
		TaskType nextTask = payload.getTaskType().getNextTask();
		if (nextTask == null) {
			log.info("[TaskTriggerService] Task Process Finished in RequestId = {}", requestId);
			return;
		}

		TaskProgressDto.Request data = TaskProgressDto.Request.builder()
			.requestId(requestId)
			.build();

		RestTemplate restTemplate = new RestTemplate();

		Map<String, Object> params = Map.of(
			"session_id", requestId
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

		ResponseEntity<Void> voidResponseEntity = restTemplate.postForEntity(requestUrl + nextTask.getRequestUrl(),
			requestEntity, Void.class);
		log.info("[TaskTriggerService] Request Next Task = {}, RequestId = {}", nextTask, requestId);
	}
}
