package org.opengame.engine.render;

import lombok.Getter;
import org.opengame.engine.scene.Mesh;

import java.io.IOException;

/**
 * Just a simple test cube
 */
@Getter
public class TestCube extends Mesh {

    public TestCube() throws IOException {
        super(getTestVertices(), getTestIndices(), "vs_cube", "fs_cube");
    }

    private static Object[][] getTestVertices() {
        return new Object[][]{
                { -1.0f, 1.0f, 1.0f, 0xff000000 },
                { 1.0f, 1.0f, 1.0f, 0xff0000ff },
                { -1.0f, -1.0f, 1.0f, 0xff00ff00 },
                { 1.0f, -1.0f, 1.0f, 0xff00ffff },
                { -1.0f, 1.0f, -1.0f, 0xffff0000 },
                { 1.0f, 1.0f, -1.0f, 0xffff00ff },
                { -1.0f, -1.0f, -1.0f, 0xffffff00 },
                { 1.0f, -1.0f, -1.0f, 0xffffffff }
        };
    }

    private static int[] getTestIndices() {
        return new int[]{
                0, 1, 2, // 0
                1, 3, 2,
                4, 6, 5, // 2
                5, 6, 7,
                0, 2, 4, // 4
                4, 2, 6,
                1, 5, 3, // 6
                5, 7, 3,
                0, 4, 1, // 8
                4, 5, 1,
                2, 3, 6, // 10
                6, 3, 7
        };
    }
}
