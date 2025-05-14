package allright.springboot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum TaskType {
	OCR(""),
	REAL_ESTATE(""),
	BASE_CLAUSE("/analyze/base-clauses"),
	SPECIAL_REQUIREMENT("/analyze/special-required"),
	RISK_CLAUSE("/analyze/risk-clauses");

	private final String requestUrl;

	@Setter
	private TaskType nextTask;

	static {
		OCR.setNextTask(REAL_ESTATE);
		REAL_ESTATE.setNextTask(BASE_CLAUSE);
		BASE_CLAUSE.setNextTask(SPECIAL_REQUIREMENT);
		SPECIAL_REQUIREMENT.setNextTask(RISK_CLAUSE);
		RISK_CLAUSE.setNextTask(null); // 마지막 Task의 nextTask는 null
	}
}
