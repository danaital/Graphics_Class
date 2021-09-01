package edu.cg;

import org.lwjgl.opengl.GLCapabilities;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL.createCapabilities;

public class Main {

    private Window window = new Window();
    private Viewer viewer = new Viewer(Window.WINDOW_INIT_WIDTH, Window.WINDOW_INIT_HEIGHT);
    private Controller controller = new Controller(window, viewer);
    private Timer timer = new Timer();

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLCapabilities caps = createCapabilities();


        // Run the rendering loop until the user has attempted to clos e
        // the window or has pressed the ESCAPE key.

        while (!window.shouldClose()) {
            viewer.render();
            window.refresh();
            sync(60);
        }
    }

    private void sync(int fps) {
        double lastLoopTime = timer.getLastLoopTime();
        double now = timer.getTime();
        float targetTime = 1f / fps;

        while (now - lastLoopTime < targetTime) {
            Thread.yield();

            /* This is optional if you want your game to stop consuming too much
             * CPU but you will loose some accuracy because Thread.sleep(1)
             * could sleep longer than 1 millisecond */
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {

            }
            glfwPollEvents();
            now = timer.getTime();
        }
    }

    public void init() {
        window.init();
        viewer.init();
        controller.init();
    }

    public void dispose() {
        window.dispose();
        controller.dispose();
    }

    public void run() {
        this.init();
        this.loop();
        this.dispose();
    }

    public static void main(String[] args) throws IOException {
        new Main().run();
    }
}
