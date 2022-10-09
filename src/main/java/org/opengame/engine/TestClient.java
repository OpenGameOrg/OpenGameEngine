package org.opengame.engine;

import lombok.extern.java.Log;
import org.joml.Vector3f;
import org.opengame.engine.app.AppConfig;
import org.opengame.engine.scene.MeshLoader;
import org.opengame.engine.render.TestCube;
import org.opengame.engine.scene.Scene;

import java.io.IOException;
import java.net.URISyntaxException;

@Log
public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        log.info("LOG TEST");
        // start engine
        var engine = new Engine();
        var config = new AppConfig();
        if (args.length > 0) {
            config.setWorkingDirectory(args[0]);
        }
        engine.Init(config);

        var scene = new Scene();
//        for (int i = 0; i < 1; i++) {
//            scene.add(new TestCube());
//        }

        var model = MeshLoader.loadModel( Engine.getWorkingDirectory() + "models/cube.obj");
        scene.add(model);
        model.setRotation(new Vector3f(0, 1.4f, 0));
        model.setPosition(new Vector3f(0, 1, 20));

        engine.setCurrentScene(scene);

        log.info("Current scene stats: " + scene.getStats());

        engine.startLoop();
    }
}
