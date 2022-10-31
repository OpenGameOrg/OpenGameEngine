package org.opengame.engine.scene;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.joml.*;
import org.lwjgl.bgfx.BGFXMemory;
import org.lwjgl.bgfx.BGFXReleaseFunctionCallback;
import org.lwjgl.bgfx.BGFXVertexLayout;
import org.lwjgl.system.MemoryUtil;
import org.opengame.engine.Engine;
import org.opengame.engine.object.MaterialObject;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

import static org.joml.Math.*;
import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Base class for all meshes
 */
@Log
public class Mesh extends MaterialObject {
    private static BGFXReleaseFunctionCallback releaseMemoryCb =
            BGFXReleaseFunctionCallback.create((_ptr, _userData) -> nmemFree(_ptr));
    private static final String TEST_TEXTURE = "textures/test.dds";

    @Getter
    @Setter
    private ByteBuffer vertices;
    @Getter
    @Setter
    private ByteBuffer indices;

    @Getter
    @Setter
    private int vertexCount;
    @Getter
    @Setter
    private int indexCount;

    private final short vertexBuffer;
    private final short indexBuffer;
    private final BGFXVertexLayout layout;
    private final short program;

    private short texture;
    private short textureUniform = -1;
    private float[] colorBuf;
    private short colorUniform = -1;
    private long drawType;

    private int vertexSize;

    private final Matrix4x3f model = new Matrix4x3f();

    private final FloatBuffer modelBuffer;

    public Mesh(MeshInfo info) throws IOException {
        this.vertexSize = info.isVertexWithColor() ? 4 * 4 : info.isUseTexture() ? 4 * 5 : 4 * 3;
        if (info.isUseNormals()) vertexSize += 3 * 4;
        this.drawType = info.getDrawType();

        layout = createVertexLayout(info.isUseNormals(), info.isVertexWithColor(), info.isUseTexture());
        vertexCount = info.getVertexData().length;
        vertices = memAlloc(info.getVertexData().length * vertexSize);
        vertexBuffer = createVertexBuffer(vertices, layout, info.getVertexData());
        indexCount = info.getIndexData().length;
        indices = memAlloc(info.getIndexData().length * 2);
        indexBuffer = createIndexBuffer(indices, info.getIndexData());

        if (info.getTextureFileName() != null) {
            texture = loadTexture(info.getTextureFileName());
            textureUniform = bgfx_create_uniform("s_texColor", BGFX_UNIFORM_TYPE_VEC4, 1);
        } else {
            texture = -1;
            textureUniform = -1;
        }
        if (info.getColor() != null) {
            colorBuf = createBufferForColor(info.getColor());
            colorUniform = bgfx_create_uniform("s_color", BGFX_UNIFORM_TYPE_VEC4, 1);
        }

        program = loadShaderProgram(info);

        modelBuffer = MemoryUtil.memAllocFloat(16);
    }

    private float[] createBufferForColor(Color color) {
        var components = color.getRGBColorComponents(new float[4]);
        components[3] = color.getAlpha() * 1.0f / 255f;

        return components;
    }

    /**
     * Create vertex layout for mesh
     * @param withNormals use normals
     * @param withColor use color
     * @param withTexture use texture
     * @return vertex layout
     */
    protected BGFXVertexLayout createVertexLayout(boolean withNormals, boolean withColor, boolean withTexture) {
        var layout = BGFXVertexLayout.calloc();

        bgfx_vertex_layout_begin(layout, Engine.getRenderer());
        bgfx_vertex_layout_add(layout, BGFX_ATTRIB_POSITION, 3, BGFX_ATTRIB_TYPE_FLOAT, false, false);

        if (withColor) {
            bgfx_vertex_layout_add(layout, BGFX_ATTRIB_COLOR0, 4, BGFX_ATTRIB_TYPE_UINT8, true, false);
        }
        if (withTexture) {
            bgfx_vertex_layout_add(layout, BGFX_ATTRIB_TEXCOORD0, 2, BGFX_ATTRIB_TYPE_FLOAT, true, true);
        }
        if (withNormals) {
            bgfx_vertex_layout_add(layout, BGFX_ATTRIB_NORMAL, 3, BGFX_ATTRIB_TYPE_FLOAT, false, false);
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

    private short loadShaderProgram(MeshInfo info) throws IOException {
        if (info.getVertexShaderName() != null && info.getFragmentShaderName() != null) {
            short vertexShader = loadShader(info.getVertexShaderName());
            short fragmentShader = loadShader(info.getFragmentShaderName());

            return bgfx_create_program(vertexShader, fragmentShader, true);
        } else {
            // default shader
            return 0;
        }
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

    private short loadTexture(String fileName) throws IOException {
        var textureDirPath = Engine.getWorkingDirectory();
        ByteBuffer textureData = loadResource(textureDirPath + fileName);
        BGFXMemory textureMemory = bgfx_make_ref_release(textureData, releaseMemoryCb, NULL);

        return bgfx_create_texture(Objects.requireNonNull(textureMemory), BGFX_TEXTURE_NONE, 0, null);
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

    public void setTexture(String textureName) throws IOException {
        if (texture != -1) {
            bgfx_destroy_texture(texture);
            bgfx_destroy_uniform(textureUniform);
        }

        texture = loadTexture(textureName);
        textureUniform = bgfx_create_uniform("s_texColor", BGFX_UNIFORM_TYPE_VEC4, 1);
    }

    public void lookAt(Vector3f lookAt) {
       var targetDir = new Vector3f(lookAt.x - getPosition().x,
                lookAt.y - getPosition().y,
                lookAt.z - getPosition().z).normalize();
       var currentDir = new Vector3f(0, 1, 0).rotate(getOrientation());

        getOrientation().rotationTo(currentDir, targetDir);
    }

    @Override
    public void frame(float time, float frameTime) {
        bgfx_dbg_text_printf(0, 2, 0x6f, "OpenGameEngine 0.0.1-SNAPSHOT");
        bgfx_dbg_text_printf(0, 3, 0x0f, String.format("Frame: %7.3f[ms]", frameTime));

        long encoder = bgfx_encoder_begin(false);

        bgfx_encoder_set_transform(encoder,
                    model.translation(getPosition())
                            .rotate(getOrientation())
                            .scale(getScale())
                            .get4x4(modelBuffer));

        bgfx_encoder_set_vertex_buffer(encoder, 0, vertexBuffer, 0, vertexCount);
        bgfx_encoder_set_index_buffer(encoder, indexBuffer, 0, indexCount);

        if (texture != -1) {
            bgfx_encoder_set_texture(encoder, 0, textureUniform, texture, 0xffffffff);
        }
        if (colorUniform != -1) {
            bgfx_encoder_set_uniform(encoder, colorUniform, colorBuf,1);
        }

        bgfx_encoder_set_state(encoder, BGFX_STATE_WRITE_RGB
                | BGFX_STATE_WRITE_A
                | BGFX_STATE_WRITE_Z
                | BGFX_STATE_DEPTH_TEST_LESS
                | drawType
                | BGFX_STATE_MSAA, 0);

        bgfx_encoder_submit(encoder, 0, program, 0, 0);
        bgfx_encoder_end(encoder);
    }

    public void dispose() {
        MemoryUtil.memFree(modelBuffer);

        bgfx_destroy_program(program);

        if (textureUniform != -1) {
            bgfx_destroy_texture(texture);
            bgfx_destroy_uniform(textureUniform);
        }
        if (colorUniform != -1) {
            bgfx_destroy_uniform(colorUniform);
        }

        bgfx_destroy_index_buffer(indexBuffer);
        bgfx_destroy_vertex_buffer(vertexBuffer);
        MemoryUtil.memFree(indices);
    }
}
