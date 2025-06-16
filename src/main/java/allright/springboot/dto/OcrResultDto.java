package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

public class OcrResultDto {

	@Getter
	@ToString
	public static class Response {
		@JsonProperty("session_id")
		private String requestId;

		@JsonProperty("seller_info")
		private SellerInformation sellerInformation;

		@JsonProperty("clauses")
		private Clause clause;
	}

	@Getter
	@ToString
	public static class SellerInformation {
		private String name;

		@JsonProperty("registration_number")
		private String registrationNumber;

		@JsonProperty("phone")
		private String phoneNumber;

		private String propertyAddress;

		private String lessorAddress;
	}

	@Getter
	@ToString
	public static class Clause {
		@JsonProperty("base_clauses")
		private List<String> baseClauseList;

		@JsonProperty("special_clauses")
		private List<String> specialClauseList;
	}
}
