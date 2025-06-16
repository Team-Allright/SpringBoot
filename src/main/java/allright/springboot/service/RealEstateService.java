package allright.springboot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Service
@RequiredArgsConstructor
public class RealEstateService {

	private final String BEARER_TOKEN     = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzZXJ2aWNlX3R5cGUiOiIxIiwic2NvcGUiOlsicmVhZCJdLCJzZXJ2aWNlX25vIjoiMDAwMDA1NTc4MDAyIiwiZXhwIjoxNzQ4MzI5MzQ4LCJhdXRob3JpdGllcyI6WyJJTlNVUkFOQ0UiLCJQVUJMSUMiLCJCQU5LIiwiRVRDIiwiU1RPQ0siLCJDQVJEIl0sImp0aSI6IjEyZjFjYjcxLWVlYjEtNDNkMC04OGY3LWZiYjY2MjkxMzE5NSIsImNsaWVudF9pZCI6IjJmYzA2NzA1LWU5NjEtNDU0MS04ZGIzLTI1MGM3MWEzMTQ0YiJ9.RTYVDltYB9Dzooctgc2ig1IqkDLJ-ZxmOYCWZxVRVMqfEBTjzHL-SqtfOBtNjUt4074qYze8jh0NAZG3kFCkc8DH-XkTW25S9vsC-hpIYnQZg5D1v_7ngg3Ot2AFV2mvvhwEikLoGCHQbU2fGWw2IN88ieRbIFxVrFEdarryS3Qju4S6PtmBjFsaX97cBf1biNo5RNM4p_pXBIc5AkzoT7V2cPD8TedLs9IoPqdxnV_NQ4YAjC7_oNsn8TRQvSzvBlCOPzpFr5AC6JOaXe2OgMBPvQ14fdXZGMHhYRjwYhpw5rJu6a1unLMoi7zPs03k7BMHCdIAPSEsGQUo6l-GtQ";
	private final String ENDPOINT   = "https://development.codef.io/v1/kr/public/ck/real-estate-register/status";

	private final String PUBLIC_KEY_PEM = """
        -----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmmSQhtImhLPZLTZhkkakjYIUQ2y20DMqqk9KvDBwKayPujJF0VCZIYapy1JP+XeGdR493JqpTJp4is+pYvYlaopLYCZKsxWTUUyrthiql9ftr29lKiiZwXD0m2/2X5BxMSk6nMAGGu89z0pEMuWPOIkq94ceT2bNg/XRdaqjAyCnO4FuqMJBOnYTuTaBEVsF+LmqwyfzILZb2kfXggpKY6RlZkAz0CPHVwifWNFnWQbjp0M6k3DC/VG5Is23JRLBXMf4en2T2gYgaNUl8SJmUW7/b+5hAvtoi+zaKBNBGxyuwL2fI3Q921jnhcABCr/4hOrunards8oJCp7LOMazaQIDAQAB
        -----END PUBLIC KEY-----
        """;
	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper mapper    = new ObjectMapper();
	private final MongoTemplate mongoTemplate;


	/**
	 * 1차(열람)→2차(인증) 요청을 순차적으로 실행하고, 2차 응답 전체 JsonNode를 리턴
	 *
	 * @param phoneNo 요청자 전화번호
	 * @param address 부동산 주소
	 * @return 2차 요청 디코딩 후 JsonNode
	 */
	public JsonNode lookupAndConfirm(String phoneNo, String address) throws Exception {
		MongoCollection<Document> collection = mongoTemplate.getCollection("real-estate");
		Document filter = new Document("address", address);
		FindIterable<Document> documents = collection.find(filter);
		if (documents.iterator().hasNext()) {
			// 이미 존재하는 데이터가 있으면, 해당 데이터를 리턴
			Document existingDoc = documents.first();
			return mapper.readTree(existingDoc.toJson());
		}
		// 1차 요청 실행
		TwoWayInfo info = doFirstInquiry(phoneNo, address, "1");
		if(info == null){
			// address 동호수빼기
			address = address.replaceAll("\\s+\\d+동\\s+\\d+호$|\\s+\\d+호$", "");
			filter.put("address", address);
			documents = collection.find(filter);
			if (documents.iterator().hasNext()) {
				// 이미 존재하는 데이터가 있으면, 해당 데이터를 리턴
				Document existingDoc = documents.first();
				return mapper.readTree(existingDoc.toJson());
			}
			info = doFirstInquiry(phoneNo, address, "3");
		}

		if (info == null) {
			return null;
		}

		System.out.println(info);

		// 2차 요청 실행 후 결과 리턴
		JsonNode result = doSecondInquiry(info);

		Document doc = Document.parse(result.toString());
		doc.put("address", address);
		mongoTemplate.insert(doc, "real-estate");

		return result;
	}

	private TwoWayInfo doFirstInquiry(String phoneNo, String address, String realtyType) throws Exception {
		// 1) 암호화
		String encryptedPassword = rsaEncrypt(PUBLIC_KEY_PEM, "1234");

		// 2) JSON 바디 준비
		ObjectNode body = mapper.createObjectNode()
			.put("organization", "0002")
			.put("phoneNo",      phoneNo)
			.put("password",     encryptedPassword)
			.put("inquiryType",  "1")
			.put("realtyType",   realtyType)
			.put("address",      address)
			.put("ePrepayNo",    "O79720539721")
			.put("ePrepayPass",  "a7337")
			.put("issueType",    "1");

		String jsonBody = mapper.writeValueAsString(body);

		// 3) HTTP 요청
		HttpRequest req = HttpRequest.newBuilder()
			.uri(URI.create(ENDPOINT))
			.header("Content-Type",  "application/json")
			.header("Authorization", "Bearer " + BEARER_TOKEN)
			.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
			.build();
		HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

		// 4) 디코딩 및 파싱
		String decoded = URLDecoder.decode(resp.body(), StandardCharsets.UTF_8.name());
		System.out.println(decoded);
		JsonNode root = mapper.readTree(decoded);
		if (!root.path("result").path("code").asText().equals("CF-03002")) {
			return null;
		}
		JsonNode data = root.path("data");

		// 5) 필수 정보 추출
		int    jobIndex        = data.path("jobIndex").asInt();
		int    threadIndex     = data.path("threadIndex").asInt();
		String jti             = data.path("jti").asText();
		long   twoWayTimestamp = data.path("twoWayTimestamp").asLong();
		boolean continue2Way   = data.path("continue2Way").asBoolean();

		// 6) extraInfo → resAddrList → commUniqueNo 추출
		JsonNode extraInfo = data.path("extraInfo").path("resAddrList");
		if (!extraInfo.isArray() || extraInfo.size() == 0) {
			throw new IllegalStateException("commUniqueNo 정보가 없습니다.");
		}
		String commUniqueNo = extraInfo.get(0).path("commUniqueNo").asText();

		Document doc = Document.parse(root.toString());
		mongoTemplate.insert(doc, "first");
		return new TwoWayInfo(phoneNo, address, "",
			commUniqueNo, jobIndex, threadIndex,
			jti, twoWayTimestamp, continue2Way);
	}

	private JsonNode doSecondInquiry(TwoWayInfo info) throws Exception {
		// twoWayInfo 객체 구성
		ObjectNode twoWayInfo = mapper.createObjectNode()
			.put("jobIndex",        info.jobIndex)
			.put("threadIndex",     info.threadIndex)
			.put("jti",             info.jti)
			.put("twoWayTimestamp", info.twoWayTimestamp);

		// 2차 요청 바디
		ObjectNode body = mapper.createObjectNode()
			.put("organization",  "0002")
			.put("phoneNo",       info.phoneNo)
			.put("password",      rsaEncrypt(PUBLIC_KEY_PEM, "1234"))
			.put("inquiryType",   "1")
			.put("realtyType",    "3")
			.put("address",       info.address)
			.put("ePrepayNo",     "O79720539721")
			.put("ePrepayPass",   "a7337")
			.put("ho",            info.ho)
			.put("issueType",     "1")
			.put("uniqueNo",  info.commUniqueNo)
			.put("is2Way",        info.continue2Way)
			.set("twoWayInfo",    twoWayInfo);

		String jsonBody = mapper.writeValueAsString(body);
		HttpRequest req = HttpRequest.newBuilder()
			.uri(URI.create(ENDPOINT))
			.header("Content-Type",  "application/json")
			.header("Authorization", "Bearer " + BEARER_TOKEN)
			.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
			.build();

		HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
		String decoded = URLDecoder.decode(resp.body(), StandardCharsets.UTF_8.name());
		return mapper.readTree(decoded);
	}

	// RSA 암호화 헬퍼
	private static String rsaEncrypt(String publicKeyPem, String plainText) throws Exception {
		String pem = publicKeyPem
			.replace("-----BEGIN PUBLIC KEY-----", "")
			.replace("-----END PUBLIC KEY-----", "")
			.replaceAll("\\s+", "");
		byte[] keyBytes = Base64.getDecoder().decode(pem);

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		PublicKey key = KeyFactory.getInstance("RSA").generatePublic(spec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
	}

	/** 1차 응답 정보를 담는 내부 DTO */
	@ToString
	private static class TwoWayInfo {
		final String phoneNo;
		final String address;
		final String ho;
		final String commUniqueNo;
		final int    jobIndex;
		final int    threadIndex;
		final String jti;
		final long   twoWayTimestamp;
		final boolean continue2Way;

		TwoWayInfo(String phoneNo,
			String address,
			String ho,
			String commUniqueNo,
			int jobIndex,
			int threadIndex,
			String jti,
			long twoWayTimestamp,
			boolean continue2Way) {
			this.phoneNo        = phoneNo;
			this.address        = address;
			this.ho             = ho;
			this.commUniqueNo   = commUniqueNo;
			this.jobIndex       = jobIndex;
			this.threadIndex    = threadIndex;
			this.jti            = jti;
			this.twoWayTimestamp= twoWayTimestamp;
			this.continue2Way   = continue2Way;
		}
	}
}
