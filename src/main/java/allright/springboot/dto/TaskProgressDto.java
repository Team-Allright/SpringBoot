package allright.springboot.dto;

import lombok.Builder;
import lombok.Getter;

public class TaskProgressDto {

	@Getter
	public static class Payload {
		private String requestId;
		private TaskType taskType;
		private Boolean isCompleted;
	}

	@Getter
	@Builder
	public static class Request {
		private String requestId;
	}
}
