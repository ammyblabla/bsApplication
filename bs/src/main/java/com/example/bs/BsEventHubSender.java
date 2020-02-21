package com.example.bs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.eventhubs.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BsEventHubSender {
    public static void main(String[] args) throws IOException, EventHubException {
        final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
                .setNamespaceName("bus-application")
                .setEventHubName("bus")
                .setSasKeyName("bsProvider")
                .setSasKey("dvZFFWy8L5/TXbB7H8C+/leGbWolh5ZO6C47LlNT2M8=");


        final Gson gson = new GsonBuilder().create();
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        final EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(connStr.toString(), executorService);

        try {
            for (int i = 0; i < 100; i++) {

                String payload = "Message " + i;
//                BusDto payload = new BusDto("station", 3, 4);

                byte[] payloadBytes = gson.toJson(payload).getBytes(Charset.defaultCharset());
                EventData sendEvent = EventData.create(payloadBytes);

                ehClient.sendSync(sendEvent);
                System.out.println(payload);
            }

            System.out.println(Instant.now() + ": Send Complete...");
            System.out.println("Press Enter to stop.");
            System.in.read();
        } finally {
            ehClient.closeSync();
            executorService.shutdown();
        }
    }

}


