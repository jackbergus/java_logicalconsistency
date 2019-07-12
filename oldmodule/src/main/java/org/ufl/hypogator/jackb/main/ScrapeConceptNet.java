package org.ufl.hypogator.jackb.main;

import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.scraper.MultiConceptScraper;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;

import java.io.IOException;

public class ScrapeConceptNet {

    public static void main(String args[]) throws IOException {
        // Legacy Istantiation
        Concept5ClientConfigurations legacyConfiguration = Concept5ClientConfigurations.instantiate();

        // New Instantiation
        ConfigurationEntrypoint conf = ConfigurationEntrypoint.getInstance();

        // Actually performing the scraping process
        MultiConceptScraper.conceptNetScraper().multiScrape(conf.conceptnetScraper);
    }

}
