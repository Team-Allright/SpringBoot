package allright.springboot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
class RealEstateServiceTest {

	@Autowired
	RealEstateService realEstateService;
	@Autowired
	MongoTemplate mongoTemplate;

	@Test
	public void test() throws Exception {
		// given

		// when

		// then

		// JsonNode jsonNode = realEstateService.lookupAndConfirm("01033139341", "서울특별시 도봉구 시루봉로 71 106동 511호", "304");
		// System.out.println(jsonNode);
	}

	@Test
	public void dd() {
		// given
		mongoTemplate.getCollection("real-estate").find(new Document("address", "서울특별시 도봉구 시루봉로 71 106동 511호"))
			.forEach(document -> {
				System.out.println(document.toJson());
			});
		// when

		// then

	}
}
