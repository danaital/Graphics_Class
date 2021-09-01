package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;


public class AxisAlignedBox extends Shape {
    private final static int NDIM = 3; // Number of dimensions
    private Point a = null;
    private Point b = null;
    private double[] aAsArray;
    private double[] bAsArray;

    public AxisAlignedBox(Point a, Point b) {
        this.a = a;
        this.b = b;
        // We store the points as Arrays - this could be helpful for more elegant implementation.
        aAsArray = a.asArray();
        bAsArray = b.asArray();
        assert (a.x <= b.x && a.y <= b.y && a.z <= b.z);
    }

    @Override
    public String toString() {
        String endl = System.lineSeparator();
        return "AxisAlignedBox:" + endl +
                "a: " + a + endl +
                "b: " + b + endl;
    }

    public AxisAlignedBox initA(Point a) {
        this.a = a;
        aAsArray = a.asArray();
        return this;
    }

    public AxisAlignedBox initB(Point b) {
        this.b = b;
        bAsArray = b.asArray();
        return this;
    }

    @Override
    public Hit intersect(Ray ray) {
        double[][] intervals = new double[2][NDIM];
        boolean[] shouldNegateNormal = new boolean[NDIM];

        for (int i = 0; i < NDIM; i++) {
            double vectorDim = ray.direction().getCoordinate(i);
            double pointDim = ray.source().getCoordinate(i);
            if (Math.abs(vectorDim) <= Ops.epsilon) {
                if (pointDim <= this.aAsArray[i] || pointDim >= this.bAsArray[i]) {
                    return null;
                }
                intervals[0][i] = Double.NEGATIVE_INFINITY;
                intervals[1][i] = Double.POSITIVE_INFINITY;
            } else {
                double t1 = (this.aAsArray[i] - pointDim) / vectorDim;
                double t2 = (this.bAsArray[i] - pointDim) / vectorDim;
                shouldNegateNormal[i] = t1 <= Ops.epsilon || t2 > Ops.epsilon && t2 < t1;
                intervals[0][i] = Math.min(t1, t2);
                intervals[1][i] = Math.max(t1, t2);
            }
        }
        int maxDim = this.findMaxDim(intervals[0]);
        int minDim = this.findMinDim(intervals[1]);
        double minWithinT = intervals[0][maxDim];
        double maxWithinT = intervals[1][minDim];
        if (minWithinT > maxWithinT || maxWithinT <= Ops.epsilon) {
            return null;
        }
        Vec norm;
        if (minWithinT > Ops.epsilon) {
            norm = this.getUnitVector(maxDim);
            if (!shouldNegateNormal[maxDim]) {
                norm = norm.neg();
            }
        } else {
            minWithinT = maxWithinT;
            norm = this.getUnitVector(minDim);
            if (shouldNegateNormal[minDim]) {
                norm = norm.neg();
            }
        }
        return new Hit(minWithinT, norm).setIsWithin(!(minWithinT > Ops.epsilon));
    }


    private int findMinDim(double[] val) {
        int minIndex = 0;
        for (int i = 0; i < NDIM; i++) {
            if (val[minIndex] > val[i]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    private int findMaxDim(double[] val) {
        int maxIndex = 0;
        for (int i = 0; i < NDIM; i++) {
            if (val[maxIndex] < val[i]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private Vec getUnitVector(int dim) {
        if (dim < 0 || dim > 2) {
            return null;
        }
        double[] unitVector = new double[NDIM];
        for (int i = 0; i < unitVector.length; i++) {
            unitVector[i] = dim == i ? 1.0 : 0.0;
        }
        return new Vec(unitVector[0], unitVector[1], unitVector[2]);
    }
}

