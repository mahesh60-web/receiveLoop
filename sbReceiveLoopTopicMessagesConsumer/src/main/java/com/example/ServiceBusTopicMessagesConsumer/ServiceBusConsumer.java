package com.example.ServiceBusTopicMessagesConsumer;

import com.google.gson.Gson;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.IMessageReceiver;
import com.microsoft.azure.servicebus.ISubscriptionClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;

import lombok.extern.log4j.Log4j2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
class ServiceBusConsumer implements Ordered {

    private ISubscriptionClient iSubscriptionClient1 ;
    private ISubscriptionClient iSubscriptionClient2 ;
    private ISubscriptionClient iSubscriptionClient3 ;
    private final Logger log = LoggerFactory.getLogger(ServiceBusConsumer.class);
    private String connectionString = "Endpoint=sb://sbreceiveloop.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=2JK0jVZInS430DzbeinWmyC2meUyvp+bEiv/0230Pjk=";
    static final Gson GSON = new Gson();
    ServiceBusConsumer(){
    try {
		iSubscriptionClient1 = new SubscriptionClient(new ConnectionStringBuilder(connectionString,"sbReceiveLoop"+"/subscriptions/receiveLoop"), ReceiveMode.PEEKLOCK);
		/*
		 * iSubscriptionClient2 = new SubscriptionClient(new
		 * ConnectionStringBuilder(connectionString,
		 * "topicsgettingstarted/subscriptions/Subscription2"), ReceiveMode.PEEKLOCK);
		 * iSubscriptionClient3 = new SubscriptionClient(new
		 * ConnectionStringBuilder(connectionString,
		 * "topicsgettingstarted/subscriptions/Subscription3"), ReceiveMode.PEEKLOCK);
		 */
	} catch (InterruptedException | ServiceBusException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     }
   
    
    
	@EventListener(ApplicationReadyEvent.class)
    public void consume() throws Exception {
		 CompletableFuture sb=receiveMessagesAsync(iSubscriptionClient1);

    	//recievingmessages(iSubscriptionClient1);
    	//recievingmessages(iSubscriptionClient2);
    	//recievingmessages(iSubscriptionClient3);
    	    	
    }

    @SuppressWarnings("deprecation")
	public void recievingmessages(ISubscriptionClient iSubscriptionClient) throws InterruptedException, ServiceBusException {


        iSubscriptionClient.registerMessageHandler(new IMessageHandler() {

            @Override
            public CompletableFuture<Void> onMessageAsync(IMessage message) {
                log.info("received message " + new String(message.getBody()) + " with body ID " + message.getMessageId());
                return CompletableFuture.completedFuture(null);
            }
            
            @Override
            public void notifyException(Throwable exception, ExceptionPhase phase) {
                log.error("eeks!", exception);
            }
        });
        
    
    	
    }
    CompletableFuture receiveMessagesAsync(ISubscriptionClient iSubscriptionClient12) {

        CompletableFuture currentTask = new CompletableFuture();

        try {
            CompletableFuture.runAsync(() -> {
                while (!currentTask.isCancelled()) {
                    try {
                        IMessage message = ((IMessageReceiver) iSubscriptionClient12).receive(Duration.ofSeconds(60));
                        if (message != null) {
                            // receives message is passed to callback
                            

                                byte[] body = message.getBody();
                                Map scientist = GSON.fromJson(new String(body, UTF_8), Map.class);

                                System.out.printf(
                                        "\n\t\t\t\tMessage received: \n\t\t\t\t\t\tMessageId = %s, \n\t\t\t\t\t\tSequenceNumber = %s, \n\t\t\t\t\t\tEnqueuedTimeUtc = %s," +
                                                "\n\t\t\t\t\t\tExpiresAtUtc = %s, \n\t\t\t\t\t\tContentType = \"%s\",  \n\t\t\t\t\t\tContent: [ firstName = %s, name = %s ]\n",
                                        message.getMessageId(),
                                        message.getSequenceNumber(),
                                        message.getEnqueuedTimeUtc(),
                                        message.getExpiresAtUtc(),
                                        message.getContentType(),
                                        scientist != null ? scientist.get("firstName") : "",
                                        scientist != null ? scientist.get("name") : "");
                            }
                            iSubscriptionClient12.completeAsync(message.getLockToken());
                        
                    } catch (Exception e) {
                        currentTask.completeExceptionally(e);
                    }
                }
                currentTask.complete(null);
            });
            return currentTask;
        } catch (Exception e) {
            currentTask.completeExceptionally(e);
        }
        return currentTask;
    }
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
