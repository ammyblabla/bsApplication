package com.example.client;

import com.example.client.eventHub.EventHubConfiguration;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class ConsumerController {
    @Autowired
    EventProcessorHost eventProcessorHost;

    @Autowired
    EventHubConfiguration eventHubConfiguration;

    private EventProcessorHost newCreatedEventProcessorHost;

    @GetMapping("/start")
    public void start() {
        eventHubConfiguration.createEventProcessorHost();
    }

    @GetMapping("/stop")
    public void stop() throws ExecutionException, InterruptedException {
        if (eventProcessorHost != null) {
            eventProcessorHost.unregisterEventProcessor().get();
            eventProcessorHost = null;
        } else {
            newCreatedEventProcessorHost.unregisterEventProcessor().get();
        }
    }
}
