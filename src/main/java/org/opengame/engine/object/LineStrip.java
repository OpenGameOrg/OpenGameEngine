package org.opengame.engine.object;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.opengame.engine.Engine;
import org.opengame.engine.scene.Mesh;
import org.opengame.engine.scene.MeshInfo;
import org.opengame.engine.scene.MeshLoader;
import org.opengame.engine.scene.Model;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.bgfx.BGFX.BGFX_STATE_PT_LINES;

/**
 * Simple line with color that can be rendered
 */
@Getter
@Setter
public class LineStrip extends Model {
    protected List<Vector3f> points;

    private Color color;
    private float width;

    public LineStrip(List<Vector3f> points, Color color, float width) throws IOException {
        super(createMeshes(points, color, width));

        this.points = points;
        this.color = color;
        this.width = width;
    }

    private static List<Mesh> createMeshes(List<Vector3f> points, Color color, float width) throws IOException {
        var meshes = new ArrayList<Mesh>(points.size() - 1);
        for (int i = 0; i < points.size(); i++) {
            var point = points.get(i);
            var second = points.size() > i + 1 ? points.get(i+1) : null;

            meshes.add(createMesh(point, second, color, width));
        }

        return meshes;
    }

    private static Mesh createMesh(Vector3f point, Vector3f second, Color color, float width) throws IOException {
        var info = MeshInfo.builder().useTexture(false).color(color).build();
        var mesh = MeshLoader.loadMeshes("models/cylinder.obj", info).get(0);

        mesh.setPosition(point);
        if (second != null) {
            mesh.setScale(new Vector3f(
                    Math.max(Math.abs(second.x - point.x), width),
                    Math.max(Math.abs(second.y - point.y), width),
                    Math.max(Math.abs(second.z - point.z), width)));
        }

        return mesh;
    }

    public void addPoint(Vector3f point) throws IOException {
        var lastMesh = getMeshes().get(getMeshes().size() - 1);
        var lastPoint = lastMesh.getPosition();
        var mesh = createMesh(point, null, color, width);
        getMeshes().add(mesh);
        getPoints().add(point);

        var zDiff = point.z - lastPoint.z;
        var yDiff = point.y - lastPoint.y;
        var xDiff = point.x - lastPoint.x;
        var direction = new Vector3f(zDiff, yDiff, xDiff);

        //lastMesh.setLookAtVector(mesh.getPosition());
        //mesh.setLookAtVector(new Vector3f(mesh.getPosition().x + direction.x,
                //mesh.getPosition().y + direction.y, mesh.getPosition().z + direction.z));

        var mainAxis = 0;
        var maxDiff = xDiff;
        if (yDiff > maxDiff) {
            maxDiff = yDiff;
            mainAxis = 1;
        }
        if (zDiff > maxDiff) {
            mainAxis = 2;
        }

        var length = new Vector3f(xDiff, yDiff, zDiff).length();
        if (mainAxis == 0) {
            //lastMesh.setScale(new Vector3f(width, length, width));
        }
        if (mainAxis == 1) {
            //lastMesh.setScale(new Vector3f(length, width, width));
        }
        if (mainAxis == 2) {
            //lastMesh.setScale(new Vector3f(width, width, length));
        }

        if (points.size() > 100) {
            points.remove(0);
            Engine.getCurrentScene().remove(getMeshes().get(0));
            getMeshes().remove(0);
        }

        Engine.getCurrentScene().add(mesh);
    }
}
