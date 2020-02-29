package com.example.bs;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BusService {

    public int getEtaBasedOnGpsAndOtherAdvancedStuff() {
        Random rn = new Random();
        int max = 7;
        int min = 1;
        return rn.nextInt(max - min +1) + min;
    }
}
