package com.example.bs;


import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.RestPactRunner;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.target.MockMvcTarget;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@RunWith(RestPactRunner.class)
@Provider("BusService")
@PactBroker(host = "localhost", port="8115")
public class ProviderTest {
    @InjectMocks
    private BusController busController;

    @TestTarget
    public final MockMvcTarget target = new MockMvcTarget();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        target.setControllers(busController);
    }

    @State("This is a bus with number 613 arriving to HammerSmith bus station")
    public void getState() {
        target.setRunTimes(1);
    }
}
