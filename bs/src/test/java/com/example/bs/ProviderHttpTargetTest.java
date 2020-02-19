package com.example.bs;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import org.junit.runner.RunWith;

@RunWith(PactRunner.class)
@Provider("BusService")
@PactBroker(host = "localhost", port="8115")
public class ProviderHttpTargetTest {
    @TestTarget
    public final Target target = new HttpTarget("http", "localhost", 8111, "");

    @State("This is a bus with number 613 arriving to HammerSmith bus station")
    public void getState() {
    }


}

