package org.opengame.engine.object;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.opengame.engine.scene.Mesh;
import org.opengame.engine.scene.MeshInfo;

import java.awt.*;
import java.io.IOException;

import static org.lwjgl.bgfx.BGFX.BGFX_STATE_PT_LINES;

/**
 * Simple line with color that can be rendered
 */
@Getter
@Setter
public class Line extends Mesh {
    private Vector3f startPoint;
    private Vector3f endPoint;

    private Color color;

    public Line(Vector3f startPoint, Vector3f endPoint, Color color) throws IOException {
        super(createMeshInfo(startPoint, endPoint, color));

        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.color = color;
    }

    private static MeshInfo createMeshInfo(Vector3f startPoint, Vector3f endPoint, Color color) {
        var info = MeshInfo.builder();
        var vertices = new Object[][] {
                { startPoint.x, startPoint.y, startPoint.z },
                { endPoint.x, endPoint.y, endPoint.z },
        };
        var indices = new int[] { 0, 1 };

        return info.vertexData(vertices).indexData(indices).color(color).drawType(BGFX_STATE_PT_LINES)
                .vertexShaderName("vs_simple_color")
                .fragmentShaderName("fs_simple_color").build();
    }
}
