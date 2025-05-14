package allright.springboot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Document
@Builder
public class MongoTest {
	@Id
	private String id;
	private Integer count;
}
