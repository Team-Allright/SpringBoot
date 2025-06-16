package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class SpecialRequirementDto {

	@Getter
	@ToString
	public static class Request {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		private String message;

		@JsonProperty("missing_clauses")
		private List<Integer> missingClauseIndexList;

		@JsonProperty("missing_label")
		private List<String> missingLabelList;

		@JsonProperty("special_clauses")
		private List<SpecialClause> specialClauseList;

		@JsonProperty("suggestion_clauses")
		private List<SuggestionClause> suggestionCluaseList;
	}

	@Getter
	@ToString
	public static class SuggestionClause {
		private String label;
		private String suggestion;
	}

	@Getter
	@ToString
	public static class SpecialClause {
		private String text;
		private String label;
		private Integer pred;
	}

	@Getter
	@ToString
	@Builder
	public static class ClientResponse {
		@JsonProperty("session_id")
		private String requestId;

		private String message;

		@JsonProperty("is_violation")
		private Boolean isViolated;

		@JsonProperty("contents")
		private List<String> missingLabelList;

		@JsonProperty("suggestion_clauses")
		private List<SuggestionClause> succestionClauseList;
	}
}
