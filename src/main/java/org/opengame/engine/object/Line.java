package org.opengame.engine.object;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.opengame.engine.scene.Mesh;
import org.opengame.engine.scene.MeshInfo;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * Simple line with color that can be rendered
 */
@Getter
@Setter
public class Line extends LineStrip {
    private Vector3f startPoint;
    private Vector3f endPoint;

    private Color color;

    public Line(Vector3f startPoint, Vector3f endPoint, Color color) throws IOException {
        super(Arrays.asList(startPoint, endPoint), color, 1);

        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.color = color;
    }
}
