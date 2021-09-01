package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;

public class Sphere extends Shape {
    private Point center;
    private double radius;

    public Sphere(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Sphere() {
        this(new Point(0, -0.5, -6), 0.5);
    }

    @Override
    public String toString() {
        String endl = System.lineSeparator();
        return "Sphere:" + endl +
                "Center: " + center + endl +
                "Radius: " + radius + endl;
    }


    public Sphere initCenter(Point center) {
        this.center = center;
        return this;
    }

    public Sphere initRadius(double radius) {
        this.radius = radius;
        return this;
    }

    private Vec normalize(Point p) {
        return p.sub(this.center).normalize();
    }

    @Override
    public Hit intersect(Ray ray) {
        // we assume a = 1 , so we need to find the b and c
        double b = ray.direction().mult(2.0).dot(ray.source().sub(this.center));
        double c = ray.source().distSqr(this.center) - Math.pow(this.radius, 2);

        double discriminant = Math.sqrt(Math.pow(b, 2) - 4.0 * c);
        if (Double.isNaN(discriminant)) {
            return null;
        }
        double t1 = (-b - discriminant) / 2.0;
        double t2 = (-b + discriminant) / 2.0;
        if (t2 < Ops.epsilon) {
            return null;
        }

        boolean isWithin = t1 < Ops.epsilon;
        Vec normal = isWithin ? this.normalize(ray.add(t2)).neg() : this.normalize(ray.add(t1));
        double minT = isWithin ? t2 : t1;
        if (minT > Ops.infinity) {
            return null;
        }
        return new Hit(minT, normal).setIsWithin(isWithin);
    }
}
