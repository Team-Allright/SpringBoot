package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class SpecialRequirementDto {

	@Getter
	public static class Request {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		@JsonProperty("result")
		private String message;

		@JsonProperty("missing_clauses")
		private List<Integer> missingClauseIndexList;

		@JsonProperty("missing_label")
		private List<String> missingLabelList;

		@JsonProperty("special_clauses")
		private List<SpecialClause> specialClauseList;
	}

	@Getter
	public static class SpecialClause {
		private String text;
		private String label;
		private Integer pred;
	}
}
