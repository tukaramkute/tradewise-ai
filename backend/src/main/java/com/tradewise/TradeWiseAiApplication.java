package com.tradewise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TradeWise AI — AI-powered Trading Journal and Portfolio Analyzer.
 *
 * <p>Phase 1: in-memory persistence behind repository interfaces. The database
 * layer can be plugged in later without changing business logic.</p>
 */
@SpringBootApplication
public class TradeWiseAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeWiseAiApplication.class, args);
    }
}
