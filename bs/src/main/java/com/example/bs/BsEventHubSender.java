package com.example.bs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class BsEventHubSender {
    @Autowired
    EventHubConfiguration eventHubConfiguration;

    private EventHubClient ehClient;
    private ScheduledExecutorService executorService;
    private ConnectionStringBuilder connStr;

    public void startConnection() throws IOException, EventHubException {
        System.out.println("namespace");
        System.out.println(eventHubConfiguration.getName());
        connStr = new ConnectionStringBuilder()
                .setNamespaceName(eventHubConfiguration.getNamespace())
                .setEventHubName(eventHubConfiguration.getName())
                .setSasKeyName(eventHubConfiguration.getSasKeyName())
                .setSasKey(eventHubConfiguration.getSasKey());
        executorService = Executors.newScheduledThreadPool(4);
        ehClient = EventHubClient.createFromConnectionStringSync(connStr.toString(), executorService);

    }

    public void endConnection() throws EventHubException {
        this.ehClient.closeSync();
        this.executorService.shutdown();
    }

    public void sendData(BusDto payload) throws EventHubException {
        final Gson gson = new GsonBuilder().create();
        byte[] payloadBytes = gson.toJson(payload).getBytes(Charset.defaultCharset());
        EventData sendEvent = EventData.create(payloadBytes);

        this.ehClient.sendSync(sendEvent);
        System.out.println(payload);

    }
}


