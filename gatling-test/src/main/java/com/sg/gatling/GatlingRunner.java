package com.sg.gatling;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;

public class GatlingRunner {
    public static void main(String[] args) {
        // read the simulation class from system property or use the default
        String simulation = System.getProperty("gatling.simulation", "com.sg.gatling.simulation.ProductSimulation");

        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder();
        props.simulationClass(simulation);
        // optional: set results directory under module build folder
        props.resultsDirectory("gatling-test/build/reports/gatling");

        Gatling.fromMap(props.build());
    }
}