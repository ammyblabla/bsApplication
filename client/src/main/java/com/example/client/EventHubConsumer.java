package com.example.client;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.ExceptionReceivedEventArgs;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class EventHubConsumer
{
    public static void main(String args[]) throws InterruptedException, ExecutionException
    {
        String consumerGroupName = "$Default";
        String namespaceName = "bus-application";
        String eventHubName = "bus";
        String sasKeyName = "RootManageSharedAccessKey";
        String sasKey = "KJ4WpqZcFN7DkSHHuz/4oKIKz+4t2V9Yq/rQnutRA48=";
        String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=busapplication;AccountKey=hqhgXB9jRNFjRUGKo6M7t/2ctLtcSkCXFMn3hTr24Y4P9AiYYMAYZfoMAOSsTi7n8L/3b3MBQasNUAPYBxuHzw==;EndpointSuffix=core.windows.net";
        String storageContainerName = "buscontainer";
        String hostNamePrefix = "busapplication";

        ConnectionStringBuilder eventHubConnectionString = new ConnectionStringBuilder()
                .setNamespaceName(namespaceName)
                .setEventHubName(eventHubName)
                .setSasKeyName(sasKeyName)
                .setSasKey(sasKey);

        EventProcessorHost host = EventProcessorHost.EventProcessorHostBuilder
                .newBuilder(EventProcessorHost.createHostName(hostNamePrefix), consumerGroupName)
                .useAzureStorageCheckpointLeaseManager(storageConnectionString, storageContainerName, null)
                .useEventHubConnectionString(eventHubConnectionString.toString(), eventHubName)
                .build();

        System.out.println("Registering host named " + host.getHostName());
        EventProcessorOptions options = new EventProcessorOptions();
        options.setExceptionNotification(new ErrorNotificationHandler());

        host.registerEventProcessor(EventProcessor.class, options)
                .whenComplete((unused, e) ->
                {
                    if (e != null)
                    {
                        System.out.println("Failure while registering: " + e.toString());
                        if (e.getCause() != null)
                        {
                            System.out.println("Inner exception: " + e.getCause().toString());
                        }
                    }
                })
                .thenAccept((unused) ->
                {
                    // This stage will only execute if registerEventProcessor succeeded.
                    // If it completed exceptionally, this stage will be skipped.
                    System.out.println("Press enter to stop.");
                    try
                    {
                        System.in.read();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Keyboard read failed: " + e.toString());
                    }
                })
                .thenCompose((unused) ->
                {
                    return host.unregisterEventProcessor();
                })
                .exceptionally((e) ->
                {
                    System.out.println("Failure while unregistering: " + e.toString());
                    if (e.getCause() != null)
                    {
                        System.out.println("Inner exception: " + e.getCause().toString());
                    }
                    return null;
                })
                .get(); // Wait for everything to finish before exiting main!

        System.out.println("End of sample");
    }

    public static class ErrorNotificationHandler implements Consumer<ExceptionReceivedEventArgs>
    {
        @Override
        public void accept(ExceptionReceivedEventArgs t)
        {
            System.out.println("SAMPLE: Host " + t.getHostname() + " received general error notification during " + t.getAction() + ": " + t.getException().toString());
        }
    }

    public static class EventProcessor implements IEventProcessor
    {
        private int checkpointBatchingCount = 0;

        // OnOpen is called when a new event processor instance is created by the host. In a real implementation, this
        // is the place to do initialization so that events can be processed when they arrive, such as opening a database
        // connection.
        @Override
        public void onOpen(PartitionContext context) throws Exception
        {
            System.out.println("SAMPLE: Partition " + context.getPartitionId() + " is opening");
        }

        // OnClose is called when an event processor instance is being shut down. The reason argument indicates whether the shut down
        // is because another host has stolen the lease for this partition or due to error or host shutdown. In a real implementation,
        // this is the place to do cleanup for resources that were opened in onOpen.
        @Override
        public void onClose(PartitionContext context, CloseReason reason) throws Exception
        {
            System.out.println("SAMPLE: Partition " + context.getPartitionId() + " is closing for reason " + reason.toString());
        }

        // onError is called when an error occurs in EventProcessorHost code that is tied to this partition, such as a receiver failure.
        // It is NOT called for exceptions thrown out of onOpen/onClose/onEvents. EventProcessorHost is responsible for recovering from
        // the error, if possible, or shutting the event processor down if not, in which case there will be a call to onClose. The
        // notification provided to onError is primarily informational.
        @Override
        public void onError(PartitionContext context, Throwable error)
        {
            System.out.println("SAMPLE: Partition " + context.getPartitionId() + " onError: " + error.toString());
        }

        // onEvents is called when events are received on this partition of the Event Hub. The maximum number of events in a batch
        // can be controlled via EventProcessorOptions. Also, if the "invoke processor after receive timeout" option is set to true,
        // this method will be called with null when a receive timeout occurs.
        @Override
        public void onEvents(PartitionContext context, Iterable<EventData> events) throws Exception
        {
            System.out.println("SAMPLE: Partition " + context.getPartitionId() + " got event batch");
            int eventCount = 0;
            for (EventData data : events)
            {
                // It is important to have a try-catch around the processing of each event. Throwing out of onEvents deprives
                // you of the chance to process any remaining events in the batch.
                try
                {
                    System.out.println("SAMPLE (" + context.getPartitionId() + "," + data.getSystemProperties().getOffset() + "," +
                            data.getSystemProperties().getSequenceNumber() + "): " + new String(data.getBytes(), "UTF8"));
                    eventCount++;

                    // Checkpointing persists the current position in the event stream for this partition and means that the next
                    // time any host opens an event processor on this event hub+consumer group+partition combination, it will start
                    // receiving at the event after this one. Checkpointing is usually not a fast operation, so there is a tradeoff
                    // between checkpointing frequently (to minimize the number of events that will be reprocessed after a crash, or
                    // if the partition lease is stolen) and checkpointing infrequently (to reduce the impact on event processing
                    // performance). Checkpointing every five events is an arbitrary choice for this sample.
                    this.checkpointBatchingCount++;
                    if ((checkpointBatchingCount % 5) == 0)
                    {
                        System.out.println("SAMPLE: Partition " + context.getPartitionId() + " checkpointing at " +
                                data.getSystemProperties().getOffset() + "," + data.getSystemProperties().getSequenceNumber());
                        context.checkpoint(data).get();
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Processing failed for an event: " + e.toString());
                }
            }
            System.out.println("SAMPLE: Partition " + context.getPartitionId() + " batch size was " + eventCount + " for host " + context.getOwner());
        }
    }
}


