package allright.springboot.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import allright.springboot.dto.TaskProgressDto;
import allright.springboot.dto.TaskType;

@Service
public class TaskTriggerService {

	public void requestNextTask(TaskProgressDto.Payload payload, String requestId) {
		if (!payload.getIsCompleted()) {
			return;
		}
		TaskType nextTask = payload.getTaskType().getNextTask();

		TaskProgressDto.Request data = TaskProgressDto.Request.builder()
			.requestId(requestId)
			.build();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<TaskProgressDto.Request> requestHttpEntity = new HttpEntity<>(data, headers);

		ResponseEntity<Void> voidResponseEntity = restTemplate.postForEntity(nextTask.getRequestUrl(),
			requestHttpEntity, Void.class);
	}
}
