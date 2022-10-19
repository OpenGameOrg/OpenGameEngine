package org.opengame.engine.scene;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static org.lwjgl.bgfx.BGFX.BGFX_STATE_PT_TRISTRIP;

/**
 * All required stuff to render something using backend
 */
@Builder
@Getter
@Setter
public class MeshInfo {
    private final Object[][] vertexData;
    private final int[] indexData;
    private String vertexShaderName;
    private String fragmentShaderName;
    private String textureFileName;
    private Color color;
    private boolean vertexWithColor = false;
    private boolean useTexture = true;
    private long drawType = BGFX_STATE_PT_TRISTRIP;
}
