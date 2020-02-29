package com.example.client.eventHub;

import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

@Component
public class EventHubConsumer {
    private EventProcessorHost host;
    public EventHubConsumer(EventProcessorHost host) {
        this.host = host;
    }

    @PostConstruct
    public void setup() throws ExecutionException, InterruptedException {
        System.out.println("Registering host named " + host.getHostName());
        EventProcessorOptions options = new EventProcessorOptions();
        options.setExceptionNotification(new ErrorNotificationHandler());
        host.registerEventProcessor(EventProcessor.class, options).get();
        System.out.println("End of sample");
    }

}
