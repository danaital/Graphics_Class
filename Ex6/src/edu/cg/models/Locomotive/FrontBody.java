package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;
import org.lwjgl.opengl.GL21;

import static edu.cg.models.Locomotive.Specification.*;
import static org.lwjgl.opengl.GL21.*;

/***
 * A 3D locomotive front-body model renderer.
 */
public class FrontBody implements IRenderable {
    // The front body is composed of one box that represents the locomotive front body.
    private Box chassis = new Box(Specification.FRONT_BODY_WIDTH,
            Specification.FRONT_BODY_HEIGHT,
            Specification.FRONT_BODY_DEPTH);
    // The front body is composed of two front wheels.
    // Use a single wheel renderer along with affine transformations to render the two wheels.
    private Wheel wheel = new Wheel();
    // The front body is composed of a chimney model.
    private Chimney chimney = new Chimney();
    // The front body is composed of two front lights.
    // Use a single car light renderer along with affine transformations to render the two car lights.
    private CarLight carLight = new CarLight();
    private Window frontWindow = new Window(.325,.15);

    @Override
    public void render() {
        Materials.setMaterialChassis();
        this.chassis.render();
        glPushMatrix();
        glTranslated(0, -Specification.WHEEL_RADIUS, Specification.WHEEL_RADIUS * .5);
        glPushMatrix();
        glTranslated(Specification.FRONT_BODY_WIDTH * .5, 0, 0);
        this.wheel.render();
        glPopMatrix();
        glTranslated(-Specification.FRONT_BODY_WIDTH * .5, 0, 0);
        this.wheel.render();
        glPopMatrix();
        glPushMatrix();
        glTranslated(0, Specification.CHIMNEY_SECOND_TUBE_RADIUS * 2, 0);
        this.chimney.render();
        glPopMatrix();
        glTranslated(0, 0, Specification.CHIMNEY_SECOND_TUBE_RADIUS * 2);
        glTranslated(Specification.CHIMNEY_FIRST_TUBE_RADIUS, 0, 0);
        this.carLight.render();
        glTranslated(-Specification.CHIMNEY_FIRST_TUBE_HEIGHT, 0, 0);
        this.carLight.render();
        glTranslated(.1, .18, Specification.EPS - .5);
        this.frontWindow.render();
        glPopMatrix();
    }

    @Override
    public void init() {
    }
}
