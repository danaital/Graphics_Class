package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;
import org.lwjgl.opengl.GL21;

import static edu.cg.models.Locomotive.Specification.*;
import static edu.cg.models.Locomotive.Specification.BACK_BODY_WIDTH;
import static org.lwjgl.opengl.GL21.*;


/***
 * A 3D locomotive back body renderer. The back-body of the locomotive model is composed of a chassis, two back wheels,
 * , a roof, windows and a door.
 */
public class BackBody implements IRenderable {
    // The back body is composed of one box that represents the locomotive front body.
    private Box chassis = new Box(Specification.BACK_BODY_WIDTH, Specification.BACK_BODY_HEIGHT, Specification.BACK_BODY_DEPTH);
    // The back body is composed of two back wheels.
    private Wheel wheel = new Wheel();
    // The back body is composed of a roof that lies on-top of the locomotive chassis.
    private Roof roof = new Roof();
    private Window backWindow = new Window(.305,.185);
    @Override
    public void render() {
        glPushMatrix();
        Materials.setMaterialChassis();
        this.chassis.render();
        glPushMatrix();
        glTranslated(0, Specification.BACK_BODY_HEIGHT * 0.5, Specification.EPS - (Specification.BACK_BODY_DEPTH * 0.5));
        this.roof.render();
        glPopMatrix();
        glTranslated(0, -Specification.BACK_BODY_HEIGHT * 0.5, -Specification.BACK_BODY_HEIGHT * 0.5);
        glTranslated(Specification.BACK_BODY_WIDTH * 0.5, 0, 0);
        this.wheel.render();
        glTranslated(-Specification.BACK_BODY_WIDTH, 0, 0);
        this.wheel.render();
        glTranslated(0.2, 0.25, -BACK_WINDOW_DEPTH-EPS);
        this.backWindow.render();
        glPopMatrix();
    }


    @Override
    public void init() {

    }
}
