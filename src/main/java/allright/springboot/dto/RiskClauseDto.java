package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class RiskClauseDto {

	@Getter
	@ToString
	public static class Request {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		private String message;

		@JsonProperty("risk_special_clauses")
		private List<RiskClause> riskClauses;
	}

	@Getter
	@ToString
	public static class RiskClause {
		private String text;
		private String reason;
	}

	@Getter
	@ToString
	@Builder
	public static class ClientResponse {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		@JsonProperty("message")
		private String message;

		@JsonProperty("contents")
		private List<RiskClause> riskClauses;
	}
}
