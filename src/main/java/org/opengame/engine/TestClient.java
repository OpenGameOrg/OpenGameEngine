package org.opengame.engine;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.opengame.engine.app.AppConfig;
import org.opengame.engine.render.TestCube;
import org.opengame.engine.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log
public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("LOG TEST");
        // start engine
        var engine = new Engine();
        engine.Init(new AppConfig());

        var scene = new Scene();
        for (int i = 0; i < 1; i++) {
            scene.add(new TestCube());
        }

        engine.setCurrentScene(scene);

        engine.startLoop();
    }
}
