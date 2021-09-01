package edu.cg.models.Locomotive;

import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL11.*;


public class SideBody implements IRenderable {
    private Window[] windows = new Window[3];
    private double rxWindow;
    private double ryWindow;
    private int nWindows;
    private boolean hasDoor;

    public SideBody(double rxWindow, double ryWindow, int nWindows, boolean hasDoor) {
        this.rxWindow = rxWindow;
        this.ryWindow = ryWindow;
        if (nWindows < 0) {
            this.nWindows = 0;
        } else if (nWindows > 3) {
            this.nWindows = 3;
        } else {
            this.nWindows = nWindows;
        }
        this.hasDoor = hasDoor;
        windowsUpdate();
    }

    private void windowsUpdate() {
        if (this.nWindows == 0) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            if (i == 1 && this.hasDoor && this.nWindows == 1) {
                this.windows[i] = new Window(this.rxWindow, this.ryWindow * 1.4);
                break;
            }
            if (this.nWindows == 2 && i == 1) {
                continue;
            }
            if (this.nWindows >= 2 && i == 2 && this.hasDoor) {
                this.windows[i] = new Window(this.rxWindow, this.ryWindow * 1.4);
            } else {
                this.windows[i] = new Window(this.rxWindow, this.ryWindow);
            }
        }
    }

    @Override
    public void render() {
        glPushMatrix();
        glTranslated(.265, .05, 0);
        for (int i = 0; i < 3; i++) {
            if (this.windows[i] != null) {
                this.windows[i].render();
            }
            if (this.hasDoor && i == 1) {
                glTranslated(-.25, -.05 , 0);
            } else {
                glTranslated(-.25, 0, 0);
            }
        }
        glPopMatrix();
    }

    @Override
    public void init() {
    }
}
