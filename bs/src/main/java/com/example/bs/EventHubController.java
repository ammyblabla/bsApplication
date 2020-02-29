package com.example.bs;

import com.microsoft.azure.eventhubs.EventHubException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class EventHubController {
    @Autowired
    BusService busService;

    @Autowired
    BsEventHubSender bsEventHubSender;

    @RequestMapping("/eventhub/{station}/{nr}")
    public BusDto sendBusToEventHub(@PathVariable String station, @PathVariable int nr) throws IOException, EventHubException {
        int eta = busService.getEtaBasedOnGpsAndOtherAdvancedStuff();
        BusDto bus = new BusDto(station, nr, eta);
        bsEventHubSender.sendData(bus);
        return bus;
    }

    @RequestMapping("/eventhub/start")
    public void startConnection() throws IOException, EventHubException {
        bsEventHubSender.startConnection();
    }

    @RequestMapping("eventHub/stop")
    public void stopConnection() throws EventHubException {
        bsEventHubSender.endConnection();
    }

}
