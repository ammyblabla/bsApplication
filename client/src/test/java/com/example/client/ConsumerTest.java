package com.example.client;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ConsumerTest {
    @Rule
    public PactProviderRule provider = new PactProviderRule("BusService", "locahost", 8111, this);

    @Pact(consumer = "BusServiceClient")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        DslPart etaResults = new PactDslJsonBody()
            .stringType("station", "HammerSmith")
            .stringType("nr", "613")
            .integerType("eta", 4)
            .asBody();

        return builder
                .given("This is a bus with number 613 arriving to HammerSmith bus station")
                .uponReceiving("A request for eta for bus number 613 to HammerSmith bus station")
                .path("/bus/HammerSmith/613")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(etaResults).toPact();
    }

//    @Test
//    @PactVerification
//    public void doTest() {
//
//    }
}
