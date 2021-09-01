package edu.cg;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.*;
import static org.lwjgl.opengl.GL21.*;

public class Controller {
    private Window window = null;
    private Viewer viewer = null;
    private GameState game;

    public Controller(Window window, Viewer viewer) {
        this.window = window;
        this.viewer = viewer;
        this.game = this.viewer.getGameState();
    }

    public void dispose() {
        game.dispose();
        game = null;

    }

    public void init() {
        this.window.registerKeyCallback(new KeyboardHandler());
        this.window.registerWindowSizeCallback(new WindowSizeHandler());
        this.window.registerFramebufferSizeCallback(new FrameSizeHandler());
    }

    private class KeyboardHandler implements GLFWKeyCallbackI {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            switch (action) {
                case GLFW_PRESS:
                case GLFW_REPEAT:
                    switch (key) {
                        case GLFW_KEY_UP:
                            game.updateAcceleration(GameState.AccelerationState.GAS);
                            break;
                        case GLFW_KEY_DOWN:
                            game.updateAcceleration(GameState.AccelerationState.BREAKS);
                            break;
                        case GLFW_KEY_LEFT:
                            game.updateSteering(GameState.SteeringState.LEFT);
                            break;
                        case GLFW_KEY_RIGHT:
                            game.updateSteering(GameState.SteeringState.RIGHT);
                            break;
                        case GLFW_KEY_L:
                            viewer.toggleNightMode();
                            break;
                        case GLFW_KEY_V:
                            viewer.changeViewMode();
                            int[] width = {0};
                            int[] height = {0};
                            glfwGetWindowSize(window, width, height);
                            viewer.reshape(0, 0, width[0], height[0]);
                            break;
                        default:
                            break;
                    }
                    break;
                case GLFW_RELEASE:
                    switch (key) {
                        case GLFW_KEY_UP:
                        case GLFW_KEY_DOWN:
                            game.updateAcceleration(GameState.AccelerationState.CRUISE);
                            break;
                        case GLFW_KEY_LEFT:
                        case GLFW_KEY_RIGHT:
                            game.updateSteering(GameState.SteeringState.STRAIGHT);
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }


    private class WindowSizeHandler implements GLFWWindowSizeCallbackI {
        @Override
        public void invoke(long window, int width, int height) {
            viewer.reshape(0, 0, width, height);
        }
    }

    private class FrameSizeHandler implements GLFWFramebufferSizeCallbackI {
        @Override
        public void invoke(long window, int width, int height) {
            glViewport(0,0, width, height);
        }
    }


}
