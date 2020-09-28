package com.example.ServiceBusTopicMessagesProducer;

import com.google.gson.Gson;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.ITopicClient;
import com.microsoft.azure.servicebus.Message;

import lombok.extern.log4j.Log4j2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Clock;
import java.util.HashMap;


@Log4j2
@Component
@RestController
class ServiceBusProducer implements Ordered {

    private final ITopicClient iTopicClient;
    private final Logger log = LoggerFactory.getLogger(ServiceBusProducer.class);
    static final Gson GSON = new Gson();
	
    ServiceBusProducer(ITopicClient iTopicClient) {
		this.iTopicClient = iTopicClient;
	}
    
    @SuppressWarnings("deprecation")
	@PostMapping("/messages")
    public void produce(@RequestParam String message) throws Exception {
    	Message message1 = new Message(message);
		message1.setScheduledEnqueuedTimeUtc(Clock.systemUTC().instant().plusSeconds(120));
		this.iTopicClient.send(message1);
    }
    
    @SuppressWarnings("serial")
	@PostMapping("/User")
    public void producer(@RequestBody UserDetails user) throws Exception {
    	final String messageId = Integer.toString(12);
    	
    	log.info("Username - " + user.getUserName());
		log.info("Address - " + user.getUAddress());
		log.info("State - " + user.getUstate());
         
    	IMessage message = new Message(GSON.toJson(user, UserDetails.class).getBytes(UTF_8));
        message.setContentType("application/json");
        message.setMessageId(messageId);
        message.setProperties(new HashMap<String, Object>() {{
            put("UserName", user.getUserName());
            put("Address", user.getUAddress());
            put("State", user.getUstate());
            
        }});
        iTopicClient.send(message);
    }
 
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
