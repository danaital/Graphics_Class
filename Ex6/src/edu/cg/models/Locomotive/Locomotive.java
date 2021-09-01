package edu.cg.models.Locomotive;

import edu.cg.models.IRenderable;

import static edu.cg.models.Locomotive.Specification.EPS;
import static org.lwjgl.opengl.GL21.*;

/**
 * A 3D Renderable Locomotive model.
 */
public class Locomotive implements IRenderable {

    FrontBody frontBody = new FrontBody();
    BackBody backBody = new BackBody();
    SideBody rightBody = new SideBody(.2, .23, 3, true);
    SideBody leftBody = new SideBody(.2, .23, 3, false);

    public void render() {
        glPushMatrix();
        glPushMatrix();
        glTranslated(0, -Specification.BASE_UNIT, Specification.FRONT_BODY_DEPTH / 2);
        this.frontBody.render();
        glPopMatrix();
        glTranslated(0, 0, -Specification.BACK_BODY_DEPTH / 2);
        this.backBody.render();
        glTranslated(-.2 - Specification.EPS, 0, 0);
        glRotated(90, 0, 1, 0);
        this.rightBody.render();
        glTranslated(0, 0, 2 * Specification.EPS + .4);
        this.leftBody.render();
        glPopMatrix();
    }


    @Override
    public String toString() {
        return new String("Locomotive 3D model");
    }


    @Override
    public void init() {
     }

}
