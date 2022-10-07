package org.opengame.engine.scene;

import lombok.extern.java.Log;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_OptimizeMeshes;

/**
 * Load mesh
 */
@Log
public class MeshLoader {
    public static Model loadModel(String modelPath) throws IOException {
        AIScene scene = aiImportFile(modelPath, aiProcess_OptimizeMeshes);

        if (scene == null) {
            throw new RuntimeException("Model " + modelPath + " is not loaded!");
        }

        log.info("Scene loaded");
        log.info("Name: " + scene.mName().dataString());
        log.info("Mesh count: " + scene.mNumMeshes());

        var meshesBuffer = scene.mMeshes();
        var meshes = new ArrayList<Mesh>();
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            meshes.add(createMesh(AIMesh.create(meshesBuffer.get(i))));
        }

        return new Model(meshes);
    }

    private static Mesh createMesh(AIMesh aiMesh) throws IOException {
        var vertices = aiMesh.mVertices();
        var meshVertices = vertices.stream().map((vert) ->
                new Object[] { vert.x(), vert.y(), vert.z(), 0xff0000ff}).toArray(Object[][]::new);

        var faces = aiMesh.mFaces();
        var meshIndices = new int[aiMesh.mNumFaces() * 3];
        for (int i = 0; i < aiMesh.mNumFaces(); i+=3) {
            var face = faces.get(i);
            meshIndices[i] = face.mIndices().get(0);
            meshIndices[i+1] = face.mIndices().get(1);
            meshIndices[i+2] = face.mIndices().get(2);
        }

        return new Mesh(meshVertices, meshIndices, null, null);
    }
}
