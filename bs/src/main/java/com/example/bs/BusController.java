package com.example.bs;

import com.microsoft.azure.eventhubs.EventHubException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Random;

@RestController
public class BusController {
    @Autowired
    BsEventHubSender bsEventHubSender;

    @RequestMapping("/bus/{station}/{nr}")
    public BusDto sendBusToApi(@PathVariable String station, @PathVariable int nr) {
        int eta = getEtaBasedOnGpsAndOtherAdvancedStuff();
        BusDto b = new BusDto(station, nr, eta);
        return b;
    }

    private int getEtaBasedOnGpsAndOtherAdvancedStuff() {
        Random rn = new Random();
        int max = 7;
        int min = 1;
        return rn.nextInt(max - min +1) + min;
    }

    @RequestMapping("/eventhub/{station}/{nr}")
    public BusDto sendBusToEventHub(@PathVariable String station, @PathVariable int nr) throws IOException, EventHubException {
        int eta = getEtaBasedOnGpsAndOtherAdvancedStuff();
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
