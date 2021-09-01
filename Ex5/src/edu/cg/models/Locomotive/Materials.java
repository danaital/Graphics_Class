package edu.cg.models.Locomotive;

import edu.cg.algebra.Vec;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL21.*;

/**
 * This class contains different material properties that can be used to color different surfaces of the locomotive
 * model. You need to use the static methods of the class inorder to define the color of each surface. Note, defining
 * the color using these static methods will be useful in the next assignment.
 *
 * For example:
 * If you want to render the locomotive back chassis, then the right way to do this would be:
 *     Materials.setMaterialChassis();
 *     chassis.render();
 * Instead of:
 *     glColor3d(r,g,b,1.0);
 *     chassis.render();
 *
 *
 *  Note: you would still want to call glColor3d(r,g,b,1.0);  in the definition of Materials.setMaterialChassis().
 */
public final class Materials {
    private static final Vec BLACK = new Vec(0f);
    private static final Vec WHITE = new Vec(1f);
    private static final Vec DARK_RED = new Vec(0.5f, 0.f, 0f);
    private static final Vec DARK_GREY = new Vec(25f / 255f, 25f / 255f, 25f / 255f);
    private static final Vec GREY = new Vec(125f / 255f, 125f / 255f, 125f / 255f);
    private static final Vec LIGHT_GREY = new Vec(225f / 255f, 225f / 255f, 225f / 255f);
    private static final Vec DARK_BLUE = new Vec(0f, 0f, 25f / 255f);

    private Materials() {
    }


    public static void setMaterialRoof() {
        glColor4fv(BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, WHITE.mult(0.2).toGLColor());
        glMaterialfv(GL_FRONT, GL_DIFFUSE, BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_SPECULAR, WHITE.mult(0.8).toGLColor());
        glMaterialfv(GL_FRONT, GL_EMISSION, BLACK.toGLColor());
    }

    public static void setMaterialChassis() {
        Vec col = new Vec(34f / 255f, 153f / 255f, 84f / 255f);
        glColor4fv(col.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, col.toGLColor());
        glMaterialfv(GL_FRONT, GL_DIFFUSE, col.toGLColor());
        glMaterialfv(GL_FRONT, GL_SPECULAR, WHITE.mult(0.7).toGLColor());
        glMaterialfv(GL_FRONT, GL_EMISSION, BLACK.toGLColor());
    }

    public static void setMaterialWheelTire() {
        glColor4fv(DARK_GREY.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, DARK_GREY.toGLColor());
        glMaterialfv(GL_FRONT, GL_DIFFUSE, DARK_GREY.toGLColor());
        glMaterialfv(GL_FRONT, GL_SPECULAR, BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_EMISSION, BLACK.toGLColor());
    }

    public static void setMaterialWheelRim() {
        Vec col = new Vec(149f / 255f, 165f / 255f, 166f / 255f);
        glColor4fv(col.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, col.mult(0.25).toGLColor());
        glMaterialfv(GL_FRONT, GL_DIFFUSE, col.toGLColor());
        glMaterialfv(GL_FRONT, GL_SPECULAR, BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_EMISSION, BLACK.toGLColor());
    }

    public static void setMaterialLightCase() {
        glColor3fv(GREY.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, DARK_GREY.mult(0.25).toGLColor());
        glMaterialfv(GL_FRONT, GL_DIFFUSE, DARK_GREY.toGLColor());
        glMaterialfv(GL_FRONT, GL_SPECULAR, BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_EMISSION, BLACK.toGLColor());
    }

    public static void setMaterialFrontLight() {
        Vec col = new Vec(184f / 255f, 149f / 255f, 11f / 255f);
        glColor4fv(col.toGLColor());
        glMaterialfv(GL_FRONT, GL_DIFFUSE, col.toGLColor());
        glMaterialfv(GL_FRONT, GL_SPECULAR, BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_EMISSION, col.toGLColor());
    }
}
