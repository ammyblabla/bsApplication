package com.example.client.eventHub;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClientOptions;
import com.microsoft.azure.eventhubs.TransportType;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Profile("!test")
public class EventHubConfiguration {

    @Value("${eventhub.namespace}")
    private String namespace;

    @Value("${eventhub.name}")
    private String name;

    @Value("${eventhub.sasKeyName}")
    private String sasKeyName;

    @Value("${eventhub.sasKey}")
    private String sasKey;

    @Value("${eventhub.storageConnectionString}")
    private String storageConnectionString;

    @Value("${eventhub.storageContainerName}")
    private String storageContainerName;

    @Value("${eventhub.hostNamePrefix}")
    private String hostNamePrefix;

    @Value("${eventhub.consumerGroup}")
    private String consumerGroup;

    @Bean
    @Qualifier("eventHubExecutor")
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(4);
    }


    @Bean
    public EventProcessorHost createEventProcessorHost() {
        ConnectionStringBuilder eventHubConnectionString = new ConnectionStringBuilder()
                .setNamespaceName(namespace)
                .setEventHubName(name)
                .setSasKeyName(sasKeyName)
                .setSasKey(sasKey);

        return EventProcessorHost.EventProcessorHostBuilder
                .newBuilder(EventProcessorHost.createHostName(hostNamePrefix), consumerGroup)
                .useAzureStorageCheckpointLeaseManager(storageConnectionString, storageContainerName, null)
                .useEventHubConnectionString(eventHubConnectionString.toString(), name)
                .build();
    }

    EventHubClientOptions getEventHubClientOptions() {
        EventHubClientOptions options = new EventHubClientOptions();
        options.setTransportType(TransportType.AMQP_WEB_SOCKETS);
        return options;
    }


}
