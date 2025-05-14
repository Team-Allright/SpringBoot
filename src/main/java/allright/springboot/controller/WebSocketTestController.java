package allright.springboot.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketTestController {

	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/echo/{id}")
	@SendTo("/topic/echo")
	public String test(String message, @DestinationVariable String id) {
		log.info("message sent by id: {}", id);
		return message;
	}

	@PostMapping("/echo/{id}")
	public String messageSendTest(@RequestParam String message, @PathVariable String id) {
		sendMessage(message, id);
		return message;
	}

	public void sendMessage(String message, @PathVariable String id) {
		messagingTemplate.convertAndSend("/topic/echo/" + id, message);
	}
}
