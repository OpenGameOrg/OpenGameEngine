package org.opengame.engine.render;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.lwjgl.bgfx.BGFX;
import org.lwjgl.bgfx.BGFXReleaseFunctionCallback;
import org.lwjgl.bgfx.BGFXVertexLayout;
import org.lwjgl.system.MemoryUtil;
import org.opengame.engine.Engine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Base class for all meshes
 */
@Log4j2
public class Mesh implements Rendarable {
    private static final int VERTEX_SIZE = (3 * 4 + 4);
    private static BGFXReleaseFunctionCallback releaseMemoryCb =
            BGFXReleaseFunctionCallback.create((_ptr, _userData) -> nmemFree(_ptr));

    private final ByteBuffer vertices;
    private final ByteBuffer indices;

    private final int vertexCount;
    private final int indexCount;

    private final short vertexBuffer;
    private final short indexBuffer;
    private final BGFXVertexLayout layout;
    private final short program;

    private final Matrix4x3f model = new Matrix4x3f();
    private final FloatBuffer modelBuffer;

    public Mesh(Object[][] vertexData, int[] indexData, String vertexShaderName, String fragmentShaderName) throws IOException {
        layout = createVertexLayout(false, true, 0);
        vertexCount = vertexData.length;
        vertices = memAlloc(vertexData.length * VERTEX_SIZE);
        vertexBuffer = createVertexBuffer(vertices, layout, vertexData);
        indexCount = indexData.length;
        indices = memAlloc(indexData.length * 2);
        indexBuffer = createIndexBuffer(indices, indexData);

        short vertexShader = loadShader(vertexShaderName);
        short fragmentShader = loadShader(fragmentShaderName);

        this.program = bgfx_create_program(vertexShader, fragmentShader, true);

        modelBuffer = MemoryUtil.memAllocFloat(16);
    }

    /**
     * Create vertex layout for mesh
     * @param withNormals use normals
     * @param withColor use color
     * @param numUVs uv count
     * @return vertex layout
     */
    protected BGFXVertexLayout createVertexLayout(boolean withNormals, boolean withColor, int numUVs) {
        var layout = BGFXVertexLayout.calloc();

        bgfx_vertex_layout_begin(layout, Engine.getRenderer());
        bgfx_vertex_layout_add(layout, BGFX_ATTRIB_POSITION, 3, BGFX_ATTRIB_TYPE_FLOAT, false, false);

        if (withNormals) {
            bgfx_vertex_layout_add(layout, BGFX_ATTRIB_NORMAL, 3, BGFX_ATTRIB_TYPE_FLOAT, false, false);
        }
        if (withColor) {
            bgfx_vertex_layout_add(layout, BGFX_ATTRIB_COLOR0, 4, BGFX_ATTRIB_TYPE_UINT8, true, false);
        }

        bgfx_vertex_layout_end(layout);

        return layout;
    }

    protected short createVertexBuffer(ByteBuffer buffer, BGFXVertexLayout layout, Object[][] vertices) {
        Arrays.stream(vertices).forEach(
                (vertex) -> Arrays.stream(vertex).forEach(
                        (attribute) -> {
                    if (attribute instanceof Float) {
                        buffer.putFloat((float) attribute);
                    } else if (attribute instanceof Integer) {
                        buffer.putInt((int) attribute);
                    } else throw new RuntimeException("Invalid parameter type");
                }));

        if (buffer.remaining() != 0) throw new RuntimeException("ByteBuffer size and number of arguments do not match");

        buffer.flip();

        return bgfx_create_vertex_buffer(Objects.requireNonNull(bgfx_make_ref(buffer)), layout, BGFX_BUFFER_NONE);
    }

    protected short createIndexBuffer(ByteBuffer buffer, int[] indices) {
        Arrays.stream(indices).forEachOrdered((index) -> buffer.putShort((short) index));

        if (buffer.remaining() != 0) throw new RuntimeException("ByteBuffer size and number of arguments do not match");

        buffer.flip();

        return bgfx_create_index_buffer(Objects.requireNonNull(bgfx_make_ref(buffer)), BGFX_BUFFER_NONE);
    }

    private short loadShader(String shaderName) throws IOException {
        String resourcePath = Engine.getWorkingDirectory() + "shaders/";

        switch (bgfx_get_renderer_type()) {
            case BGFX_RENDERER_TYPE_DIRECT3D11:
            case BGFX_RENDERER_TYPE_DIRECT3D12:
                resourcePath += "dx11/";
                break;
            case BGFX_RENDERER_TYPE_DIRECT3D9:
                resourcePath += "dx9/";
                break;
            case BGFX_RENDERER_TYPE_OPENGL:
                resourcePath += "glsl/";
                break;

            case BGFX_RENDERER_TYPE_METAL:
                resourcePath += "metal/";
                break;
            case BGFX_RENDERER_TYPE_VULKAN:
                resourcePath += "vulkan/";
                break;

            default:
                throw new IOException("No shaders supported for " + bgfx_get_renderer_name(bgfx_get_renderer_type())
                        + " renderer");
        }

        ByteBuffer shaderCode = loadResource(resourcePath + shaderName + ".bin");

        return bgfx_create_shader(Objects.requireNonNull(bgfx_make_ref_release(shaderCode, releaseMemoryCb, NULL)));
    }

    private ByteBuffer loadResource(String resourcePath) throws IOException {
        var file = new File(resourcePath);

        if (!file.exists()) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        var url = file.toURI().toURL();
        int resourceSize = url.openConnection().getContentLength();

        log.info("Loading resource '" + url.getFile() + "' (" + resourceSize + " bytes)");

        ByteBuffer resource = memAlloc(resourceSize);

        try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
            int b;
            do {
                b = bis.read();
                if (b != -1) {
                    resource.put((byte) b);
                }
            } while (b != -1);
        }

        resource.flip();

        return resource;
    }

    @Override
    public void frame(float time, float frameTime) {
        bgfx_dbg_text_printf(0, 15, 0x4f, "testClient - OpenGameEngine");

        long encoder = bgfx_encoder_begin(false);
        for (int yy = 0; yy < 11; ++yy) {
            for (int xx = 0; xx < 11; ++xx) {
                bgfx_encoder_set_transform(encoder,
                        model.translation(
                                        -15.0f + xx * 3.0f,
                                        -15.0f + yy * 3.0f,
                                        0.0f)
                                .rotateXYZ(
                                        time + xx * 0.21f,
                                        time + yy * 0.37f,
                                        0.0f)
                                .get4x4(modelBuffer));

                bgfx_encoder_set_vertex_buffer(encoder, 0, vertexBuffer, 0, vertexCount);
                bgfx_encoder_set_index_buffer(encoder, indexBuffer, 0, indexCount);

                bgfx_encoder_set_state(encoder, BGFX_STATE_DEFAULT, 0);

                bgfx_encoder_submit(encoder, 0, (short) 0, 0, 0);
            }
        }
        bgfx_encoder_end(encoder);
    }

    public void dispose() {
        MemoryUtil.memFree(modelBuffer);

        bgfx_destroy_program(program);

        bgfx_destroy_index_buffer(indexBuffer);
        MemoryUtil.memFree(indices);
    }

}
