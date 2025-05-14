package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class RiskClauseDto {

	@Getter
	public static class Request {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private boolean isViolated;

		private String message;

		@JsonProperty("risk_special_clauses")
		private List<RiskClause> riskClauses;
	}

	@Getter
	public static class RiskClause {
		private String text;
		private String reason;
	}
}
