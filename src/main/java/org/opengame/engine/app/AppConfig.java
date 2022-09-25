package org.opengame.engine.app;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Configuration of the application
 */
@RequiredArgsConstructor
@Getter
public class AppConfig {
    private String appName = "OpenEngineApp";
    private String appVersion = "1.0";

    private int windowWidth = 800;
    private int windowHeight = 600;
}
