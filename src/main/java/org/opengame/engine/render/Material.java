package org.opengame.engine.render;

import lombok.Data;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;

import static org.lwjgl.assimp.Assimp.*;

/**
 * Representing material
 */
@Data
public class Material {
    private float[] ambienceColor;
    private float[] diffuseColor;
    private float[] specularColor;

    public static Material from(AIMaterial aiMaterial) {
        var material = new Material();

        var mAmbienceColor = AIColor4D.create();
        if (aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT,
                aiTextureType_NONE, 0, mAmbienceColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        var mDiffuseColor = AIColor4D.create();
        if (aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE,
                aiTextureType_NONE, 0, mDiffuseColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        var mSpecularColor = AIColor4D.create();
        if (aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR,
                aiTextureType_NONE, 0, mSpecularColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }

        material.ambienceColor = new float[] { mAmbienceColor.r(), mAmbienceColor.g(), mAmbienceColor.b(), mAmbienceColor.a()};
        material.diffuseColor = new float[] { mDiffuseColor.r(), mDiffuseColor.g(), mDiffuseColor.b(), mDiffuseColor.a()};
        material.specularColor = new float[] { mSpecularColor.r(), mSpecularColor.g(), mSpecularColor.b(), mSpecularColor.a()};

        return material;
    }
}
