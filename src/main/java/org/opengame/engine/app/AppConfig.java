package org.opengame.engine.app;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.opengame.engine.Engine;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration of the application
 */
@RequiredArgsConstructor
@Getter
@Setter
public class AppConfig {
    private String appName = "OpenEngineApp";
    private String appVersion = "1.0";

    private int windowWidth = 800;
    private int windowHeight = 600;

   // private int fpsLimit = -1;
    private int ups = 40;

    private String workingDirectory;

    public String getWorkingDirectory() {
        if (workingDirectory == null) {
            return Objects.requireNonNull(Engine.class.getResource(".")).getPath() + "../../../";
        }

        return workingDirectory;
    }

    public void setWorkingDirectory(String fullPath) {
        workingDirectory = fullPath;
    }
}
