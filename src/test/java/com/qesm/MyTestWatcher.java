package com.qesm;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class MyTestWatcher implements TestWatcher {
    private boolean testFailed = false;

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if(!testFailed){
            Optional <Object> testIstance = context.getTestInstance();
            ProductGraphTest curTestIstance = (ProductGraphTest) testIstance.get();

            ProductGraph my_graph = curTestIstance.getProductGraph();

            testFailed = true;
        }
    }
}