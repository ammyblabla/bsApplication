package com.example.bs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BsEventHubSender {
    private EventHubClient ehClient;
    private ScheduledExecutorService executorService;
    private ConnectionStringBuilder connStr;

    public BsEventHubSender() throws IOException, EventHubException {
        connStr = new ConnectionStringBuilder()
                .setNamespaceName("bus-application")
                .setEventHubName("bus")
                .setSasKeyName("bsProvider")
                .setSasKey("dvZFFWy8L5/TXbB7H8C+/leGbWolh5ZO6C47LlNT2M8=");
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


