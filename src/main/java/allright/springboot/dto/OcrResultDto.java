package allright.springboot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class OcrResultDto {

	@Getter
	public static class Response {
		private String requestId;

		@JsonProperty("seller_info")
		private SellerInformation sellerInformation;

		@JsonProperty("clauses")
		private Clause clause;
	}

	@Getter
	public static class SellerInformation {
		private String name;

		@JsonProperty("registration_number")
		private String registrationNumber;

		@JsonProperty("phone")
		private String phoneNumber;

		private String address;
	}

	@Getter
	public static class Clause {
		@JsonProperty("base_clauses")
		private List<String> baseClauseList;

		@JsonProperty("special_clauses")
		private List<String> specialClauseList;
	}

	public static class Parameter {

	}
}
