package com.example.client;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ConsumerTest {
    int port = 8080;

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("BusService", "localhost", port, this);



    @Pact(consumer = "BusServiceClient")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", String.valueOf(MediaType.APPLICATION_JSON));

        DslPart etaResults = new PactDslJsonBody()
            .stringType("station", "HammerSmith")
            .stringType("nr", "613")
            .integerType("eta", new Integer(4))
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

    @Test
    @PactVerification()
    public void doTest() {
        System.setProperty("pact.rootDir","../pacts");
        Integer eta = new Consumer(mockProvider.getPort()).getEta("HammerSmith", "613");
        System.out.println("According to test eta = " + eta);
        assertTrue(eta >= 0);
    }
}
