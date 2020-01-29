package com.example.bs;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class BusController {
    @RequestMapping("/bus/{station}/{nr}")
    public BusDto bus(@PathVariable String station, @PathVariable int nr) {
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
}
