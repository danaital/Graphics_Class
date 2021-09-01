package edu.cg.models.Locomotive;

import edu.cg.models.Box;


/**
 * A simple 3D Box renderer. The box is centered at the origin in its local coordinate system.
 * The box can have different lengths along each of the main axes.
 */
public class Window extends Box {

    public Window(double rx, double ry) {
        super(rx, ry, Specification.EPS);
    }

    @Override
    public void render() {
        Materials.setMaterialRoof();
        super.render();
    }

}
