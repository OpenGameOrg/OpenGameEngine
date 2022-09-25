package org.opengame.engine;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.PointerBuffer;
import org.lwjgl.bgfx.BGFXInit;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.opengame.engine.app.AppConfig;
import org.opengame.engine.scene.Scene;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Entry point for engine, init here
 */
@Log
public class Engine {
    private static Engine instance;
    private long windowHandle;
    @Getter
    private AppConfig config;
    @Getter
    @Setter
    private Scene currentScene;

    /**
     * Init Vulkan context and window
     */
    public void Init(AppConfig config) {
        log.info("Init");
        instance = this;
        this.config = config;
        initWindow(config);
    }

    private void initWindow(AppConfig config) {
        if (!glfwInit()) {
            throw new RuntimeException("Cannot initialize GLFW");
        }

        GLFWErrorCallback.createPrint().set();

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        windowHandle = glfwCreateWindow(config.getWindowWidth(), config.getWindowHeight(),
                config.getAppName(), NULL, NULL);

        glfwSetKeyCallback(windowHandle, (handle, key, scancode, action, mods) -> {
            if (action != GLFW_RELEASE) {
                return;
            }

            switch (key) {
                case GLFW_KEY_ESCAPE:
                    glfwSetWindowShouldClose(handle, true);
                    break;
            }
        });

        try (MemoryStack stack = MemoryStack.stackPush()) {
            BGFXInit init = BGFXInit.malloc();
            bgfx_init_ctor(init);
            init.resolution(it -> it
                    .width(config.getWindowWidth())
                    .height(config.getWindowHeight())
                    .reset(BGFX_RESET_VSYNC));

            switch (Platform.get()) {
                case LINUX:
                    init.platformData()
                            .ndt(GLFWNativeX11.glfwGetX11Display())
                            .nwh(GLFWNativeX11.glfwGetX11Window(windowHandle));
                    break;
                case MACOSX:
                    init.platformData()
                            .nwh(GLFWNativeCocoa.glfwGetCocoaWindow(windowHandle));
                    break;
                case WINDOWS:
                    init.platformData()
                            .nwh(GLFWNativeWin32.glfwGetWin32Window(windowHandle));
                    break;
            }

            if (!bgfx_init(init)) {
                throw new RuntimeException("Error initializing bgfx renderer");
            }
        }


        logAvailableDevices();
        logAvailableRenderers();

        log.info("bgfx renderer: " + bgfx_get_renderer_name(bgfx_get_renderer_type()));

        bgfx_set_debug(BGFX_DEBUG_TEXT);
        bgfx_set_view_clear(0, BGFX_CLEAR_COLOR | BGFX_CLEAR_DEPTH, 0x303030ff, 1.0f, 0);
    }

    private void logAvailableRenderers() {
        var types = new int[BGFX_RENDERER_TYPE_COUNT];

        var rendererCount = bgfx_get_supported_renderers(types);

        log.info("Supported renderers:");
        for (int i = 0; i < rendererCount; i++) {
            log.info(bgfx_get_renderer_name(types[i]));
        }
    }

    private void logAvailableDevices() {
    }

    public void startLoop() {
        long lastTime;
        long startTime = lastTime = glfwGetTimerValue();
        while (!glfwWindowShouldClose(windowHandle)) {
            glfwPollEvents();

            long now = glfwGetTimerValue();
            long frameTime = now - lastTime;
            lastTime = now;

            double freq = glfwGetTimerFrequency();
            double toMs = 1000.0 / freq;

            double time = (now - startTime) / freq;

            bgfx_set_view_rect(0, 0, 0, config.getWindowWidth(), config.getWindowHeight());
            bgfx_touch(0);

            bgfx_dbg_text_clear(0, false);
            bgfx_dbg_text_printf(0, 1, 0x1f, "Hello bgfx!");

            if (currentScene != null) {
                currentScene.render((float) time, (float) toMs * frameTime);
            }

            bgfx_frame(false);
        }

        bgfx_shutdown();
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    public static int getRenderer() {
        return bgfx_get_renderer_type();
    }

    public static String getWorkingDirectory() {
        return Objects.requireNonNull(Engine.class.getResource(".")).getPath() + "../../../";
    }

    public static Scene getCurrentScene() {
        return instance.currentScene;
    }

    public static void setScene(Scene scene) {
        instance.currentScene = scene;
    }

    public static int getScreenWidth() {
        return instance.config.getWindowWidth();
    }

    public static int getScreenHeight() {
        return instance.getConfig().getWindowHeight();
    }
}
