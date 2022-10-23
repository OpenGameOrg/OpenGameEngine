package org.opengame.engine.scene;

import lombok.extern.java.Log;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_OptimizeMeshes;

/**
 * Load mesh
 */
@Log
public class MeshLoader {
    public static List<Mesh> loadMeshes(String modelPath) throws IOException {
        var defaultMeshInfo = MeshInfo.builder()
                .useTexture(true)
                .vertexShaderName("vs_simple_textured")
                .fragmentShaderName("fs_simple_textured").build();

        return loadMeshes(modelPath, defaultMeshInfo);
    }

    public static List<Mesh> loadMeshes(String modelPath, MeshInfo meshInfo) throws IOException {
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
            meshes.add(createMesh(AIMesh.create(meshesBuffer.get(i)), meshInfo));
        }

        return meshes;
    }

    public static Model loadModel(String modelPath) throws IOException {
        return new Model(loadMeshes(modelPath));
    }

    private static Mesh createMesh(AIMesh aiMesh, MeshInfo meshInfo) throws IOException {
        var vertices = aiMesh.mVertices();
        var texCoords = aiMesh.mTextureCoords(0);

        assert texCoords != null;

        var meshVertices = new Object[aiMesh.mNumVertices()][5];
        for (int i = 0; i < meshVertices.length; i++) {
            var vert = vertices.get(i);
            meshVertices[i] = meshInfo.isUseTexture() ?
                    new Object[]{ vert.x(), vert.y(), vert.z(), texCoords.get(i).x(), texCoords.get(i).y()}
                    :
                    new Object[]{ vert.x(), vert.y(), vert.z() };
        }

        var faces = aiMesh.mFaces();
        var meshIndices = new int[aiMesh.mNumFaces() * 3];
        for (int i = 0; i < aiMesh.mNumFaces(); i++) {
            var face = faces.get(i);
            meshIndices[i * 3] = face.mIndices().get(0);
            meshIndices[i * 3 + 1] = face.mIndices().get(1);
            meshIndices[i * 3 + 2] = face.mIndices().get(2);
        }

        return new Mesh(MeshInfo.builder()
                .vertexData(meshVertices)
                .indexData(meshIndices)
                .useTexture(meshInfo.isUseTexture())
                .vertexShaderName(meshInfo.getVertexShaderName())
                .fragmentShaderName(meshInfo.getFragmentShaderName())
                .color(meshInfo.getColor()).build());
    }
}
