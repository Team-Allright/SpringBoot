package allright.springboot.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebtorService {

	private final MongoTemplate mongoTemplate;
	private final ObjectMapper mapper;

	public JsonNode findDebtorByNameAndAddress(String name, String address) throws JsonProcessingException {
		FindIterable<Document> documents = mongoTemplate.getCollection("debtorList")
			.find(new Document("name", name));

		Document first = documents.first();
		if (first == null) {
			return null;
		}
		return mapper.readTree(first.toJson());
	}
}
