package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;


public class BaseClauseDto {

	@Getter
	public static class Request {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		private String result;

		@JsonProperty("violated_clauses")
		private List<String> violatedClauseList;
	}
}
