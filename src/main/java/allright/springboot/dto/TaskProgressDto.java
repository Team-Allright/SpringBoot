package allright.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class TaskProgressDto {

	@Getter
	@ToString
	public static class Payload {
		private TaskType taskType;
		private Boolean isCompleted;
	}

	@Getter
	@Builder
	@ToString
	public static class Request {
		@JsonProperty("session_id")
		private String requestId;
	}

	@Getter
	@Builder
	@ToString
	public static class ClientResponse {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_completed")
		private Boolean isCompleted;
	}
}
