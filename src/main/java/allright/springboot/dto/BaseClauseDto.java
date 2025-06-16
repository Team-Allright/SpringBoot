package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class BaseClauseDto {

	@Getter
	@ToString
	public static class Request {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		private String message;

		@JsonProperty("violated_clauses")
		private List<String> violatedClauseList;
	}

	@Getter
	@ToString
	@Builder
	public static class ClientResponse {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		private String message;

		@JsonProperty("contents")
		private List<String> violatedClauseList;
	}
}
