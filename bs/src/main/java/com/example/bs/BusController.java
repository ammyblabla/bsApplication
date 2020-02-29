package com.example.bs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class BusController {
    @Autowired
    BusService busService;

    @RequestMapping("/bus/{station}/{nr}")
    public BusDto sendBusToApi(@PathVariable String station, @PathVariable int nr) {
        int eta = busService.getEtaBasedOnGpsAndOtherAdvancedStuff();
        BusDto b = new BusDto(station, nr, eta);
        return b;
    }
}
