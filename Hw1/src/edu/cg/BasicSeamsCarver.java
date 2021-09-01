package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class BasicSeamsCarver extends ImageProcessor {
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    int[][] greyscaled;
    double[][] dPdata;
    int[][] minCoordinates;
    Coordinate[][] originalCoordinates;
    Coordinate[][] vSeams;
    Coordinate[][] hSeams;
    int currentHeight;
    int currentWidth;
    int mode;

    public BasicSeamsCarver(Logger logger, BufferedImage workingImage,
                            int outWidth, int outHeight, RGBWeights rgbWeights) {
        super((s) -> logger.log("Seam carving: " + s), workingImage, rgbWeights, outWidth, outHeight);
        logger.log("Initing Seam Carving ");
        initSeamCarving();
        logger.log("Initing Seam Carving ");
    }

    public enum CarvingScheme {
        VERTICAL_HORIZONTAL("VERTICAL_HORIZONTAL", 0, "Vertical seams first"),
        HORIZONTAL_VERTICAL("HORIZONTAL_VERTICAL", 1, "Horizontal seams first"),
        INTERMITTENT("INTERMITTENT", 2, "Intermittent carving");

        public final String description;

        private CarvingScheme(final String name, final int ordinal, final String description) {
            this.description = description;
        }
    }

    protected class Coordinate {
        public int X;
        public int Y;

        public Coordinate(final int X, final int Y) {
            this.X = X;
            this.Y = Y;
        }
    }


    private void initSeamCarving() {
        currentHeight = inHeight;
        currentWidth = inWidth;
        hSeams = new Coordinate[Math.abs(outHeight - inHeight)][];
        vSeams = new Coordinate[Math.abs(outWidth - inWidth)][];
        addiontalResources();
    }

    private void addiontalResources() {
        dPdata = new double[inHeight][inWidth];
        minCoordinates = new int[inHeight][inWidth];
        originalCoordinates = new Coordinate[inHeight][inWidth];
        greyscaled = new int[inHeight][inWidth];
        BufferedImage bi = greyscale();
        for (int y = 0; y < inHeight; y++) {
            for (int x = 0; x < inWidth; x++) {
                greyscaled[y][x] = new Color(bi.getRGB(x, y)).getRed();
                originalCoordinates[y][x] = new Coordinate(x, y);
            }
        }
    }


    public BufferedImage carveImage(final CarvingScheme carvingScheme) {
        final int nVSeamsToCarve = Math.abs(this.outWidth - this.inWidth);
        final int nHSeamsToCarve = Math.abs(this.outHeight - this.inHeight);
        mode = carvingScheme == CarvingScheme.VERTICAL_HORIZONTAL ? 0 : 1;
        if (carvingScheme != CarvingScheme.INTERMITTENT) {
            int first = carvingScheme == CarvingScheme.VERTICAL_HORIZONTAL ? nVSeamsToCarve : nHSeamsToCarve;
            int second = carvingScheme != CarvingScheme.VERTICAL_HORIZONTAL ? nVSeamsToCarve : nHSeamsToCarve;
            mode = carvingScheme == CarvingScheme.VERTICAL_HORIZONTAL ? 0 : 1;
            carveSeams(first);
            mode = 1 - mode;
            carveSeams(second);
        } else {
            int minSeams = Math.min(nHSeamsToCarve, nVSeamsToCarve);
            int maxSeams = Math.max(nHSeamsToCarve, nVSeamsToCarve);
            int afterInterMode = nHSeamsToCarve < nVSeamsToCarve ? 0 : 1;
            mode = afterInterMode;
            for (int i = 0; i < minSeams; i++) {
                carveSeams(1);
                mode = Math.abs(mode - 1);
                carveSeams(1);
                mode = Math.abs(mode - 1);
            }
            mode = afterInterMode;
            carveSeams(maxSeams - minSeams);
        }
        final BufferedImage carvedImage = this.generateCarvedImage();
        return carvedImage;
    }


    private void carveSeams(int numOfSeamsToCarve) {
        this.logger.log("Carving " + numOfSeamsToCarve + " seams from the image.");
        for (int i = 0; i < numOfSeamsToCarve; i++) {
            this.logger.log("Carving seam no. " + (i + 1));
            this.findAndRemoveSeam();
        }
    }

    private void findAndRemoveSeam() {
        setDynamicProgrammingWithPixelEnergy();
        addDPdataWithMinCost();
        Coordinate[] optimalSeam = this.updateOptimalSeam();
        this.storeOptimalSeam(optimalSeam);
        this.carveSeamFromGreyscaleAndCoordinatesTable(optimalSeam);
        this.updateCarvedProportions();
    }

    private void setDynamicProgrammingWithPixelEnergy() {
        for (int y = 0; y < currentHeight; y++) {
            for (int x = 0; x < currentWidth; x++) {
                dPdata[y][x] = this.calcEnergyForPixel(y, x);
            }
        }
    }

    private double calcEnergyForPixel(int y, int x) {
        int nextX = (x + 1) < currentWidth ? x + 1 : x - 1;
        int nextY = (y + 1) < currentHeight ? y + 1 : y - 1;
        double verticalEnergy = Math.pow((this.greyscaled[y][nextX] - this.greyscaled[y][x]), 2.0);
        double horizontalEnergy = Math.pow((this.greyscaled[nextY][x] - this.greyscaled[y][x]), 2.0);
        return Math.sqrt(verticalEnergy + horizontalEnergy);
    }

    private void addDPdataWithMinCost() {
        if (mode == 0) {
            for (int y = 0; y < currentHeight; y++) {
                for (int x = 0; x < this.currentWidth; x++) {
                    calcAndSetMinCostAtPixel(y, x);
                }
            }
            return;
        }
        for (int x = 0; x < this.currentWidth; x++) {
            for (int y = 0; y < this.currentHeight; y++) {
                calcAndSetMinCostAtPixel(y, x);
            }
        }
    }

    private void calcAndSetMinCostAtPixel(int y, int x) {
        if (this.mode == 0) {
            this.calcAndSetVMinCostAtPixel(y, x);
        } else {
            this.calcAndSetHMinCostAtPixel(y, x);
        }
    }

    private void calcAndSetVMinCostAtPixel(int y, int x) {
        double min = 0;
        int minX = x;
        long leftCost = 0L;
        long vertCost = 0L;
        long rightCost = 0L;
        if (y > 0) {
            final double vertDetails = dPdata[y - 1][x];
            if (x > 0 & x + 1 < currentWidth) {
                rightCost = Math.abs(greyscaled[y][x - 1] - greyscaled[y][x + 1]);
                vertCost = Math.abs(greyscaled[y][x - 1] - greyscaled[y][x + 1]);
                leftCost = Math.abs(greyscaled[y][x - 1] - greyscaled[y][x + 1]);
            } else {
                rightCost = 255L;
                vertCost = 255L;
                leftCost = 255L;
            }
            double leftDetails;
            if (x > 0) {
                leftCost += Math.abs(greyscaled[y - 1][x] - greyscaled[y][x - 1]);
                leftDetails = dPdata[y - 1][x - 1];
            } else {
                leftCost = 0L;
                leftDetails = 2.147483647E9;
            }
            double rightDetails;
            if (x + 1 < currentWidth) {
                rightCost += Math.abs(greyscaled[y - 1][x] - greyscaled[y][x + 1]);
                rightDetails = dPdata[y - 1][x + 1];
            } else {
                rightCost = 0L;
                rightDetails = 2.147483647E9;
            }
            final double sumL = leftDetails + leftCost;
            final double sumV = vertDetails + vertCost;
            final double sumR = rightDetails + rightCost;
            min = Math.min(Math.min(sumL, sumV), sumR);
            if (min == sumR & x + 1 < currentWidth) {
                minX = x + 1;
            } else if (min == sumL & x > 0) {
                minX = x - 1;
            } else {
                minX = x;
            }
        }
        double[] array = this.dPdata[y];
        array[x] += min;
        minCoordinates[y][x] = minX;
    }

    private void calcAndSetHMinCostAtPixel(int y, int x) {
        double min = 0.0;
        int minY = y;
        long costDown = 0L;
        long costHorizontal = 0L;
        long costUp = 0L;
        if (x > 0) {
            double detailsHorizontal = dPdata[y][x - 1];
            if (y > 0 & y + 1 < currentHeight) {
                costHorizontal = Math.abs(greyscaled[y - 1][x] - greyscaled[y + 1][x]);
                costDown = Math.abs(greyscaled[y - 1][x] - greyscaled[y + 1][x]);
                costUp = Math.abs(greyscaled[y - 1][x] - greyscaled[y + 1][x]);
            } else {
                costHorizontal = 255L;
                costDown = 255L;
                costUp = 255L;
            }
            double md;
            if (y + 1 < currentHeight) {
                costDown += Math.abs(greyscaled[y + 1][x] - greyscaled[y][x - 1]);
                md = dPdata[y + 1][x - 1];
            } else {
                costDown = 0L;
                md = 2.147483647E9;
            }
            double mu;
            if (y > 0) {
                costUp += Math.abs(greyscaled[y - 1][x] - greyscaled[y][x - 1]);
                mu = dPdata[y - 1][x - 1];
            } else {
                costUp = 0L;
                mu = 2.147483647E9;
            }
            double sumD = md + costDown;
            double sumH = detailsHorizontal + costHorizontal;
            double sumU = mu + costUp;
            min = Math.min(Math.min(sumD, sumH), sumU);
            if (min == sumU & y > 0) {
                minY = y - 1;
            } else if (min == sumD & y + 1 < currentHeight) {
                minY = y + 1;
            } else {
                minY = y;
            }
        }
        double[] array = dPdata[y];
        array[x] += min;
        minCoordinates[y][x] = minY;
    }

    private Coordinate[] updateOptimalSeam() {
        Coordinate[] optimalSeam;
        if (mode == 0) {
            optimalSeam = new Coordinate[currentHeight];
            int minX = 0;
            for (int x = 0; x < currentWidth; x++) {
                if (dPdata[currentHeight - 1][x] < dPdata[currentHeight - 1][minX]) {
                    minX = x;
                }
            }
            for (int y = currentHeight - 1; y >= 0; y--) {
                optimalSeam[y] = new Coordinate(minX, y);
                minX = minCoordinates[y][minX];
            }
        } else {
            optimalSeam = new Coordinate[currentWidth];
            int minY = 0;
            for (int y = 0; y < currentHeight; y++) {
                if (dPdata[y][currentWidth - 1] < dPdata[minY][currentWidth - 1]) {
                    minY = y;
                }
            }
            for (int x = currentWidth - 1; x >= 0; x--) {
                optimalSeam[x] = new Coordinate(x, minY);
                minY = minCoordinates[minY][x];
            }
        }
        return optimalSeam;
    }

    private void storeOptimalSeam(final Coordinate[] optimalSeam) {
        Coordinate[][] seamsStored = mode == 0 ? vSeams : hSeams;
        int numOfSeamsFound = mode == 0 ? inWidth - currentWidth : inHeight - currentHeight;
        seamsStored[numOfSeamsFound] = new Coordinate[optimalSeam.length];
        for (int i = 0; i < optimalSeam.length; i++) {
            seamsStored[numOfSeamsFound][i] = originalCoordinates[optimalSeam[i].Y][optimalSeam[i].X];
        }
    }


    private void carveSeamFromGreyscaleAndCoordinatesTable(final Coordinate[] seam) {
        this.logger.log("Removing the optimal seam from the image");
        for (final Coordinate c : seam) {
            this.shiftAtCoordinate(c);
        }
    }

    private void shiftAtCoordinate(final Coordinate c) {
        if (this.mode == 0) {
            this.shiftLeft(c);
        } else {
            this.shiftUp(c);
        }
    }

    private void shiftLeft( Coordinate c) {
        int y = c.Y;
        for (int x = c.X + 1; x < this.currentWidth; ++x) {
            this.originalCoordinates[y][x - 1] = this.originalCoordinates[y][x];
            this.greyscaled[y][x - 1] = this.greyscaled[y][x];
        }
    }

    private void shiftUp( Coordinate c) {
        int x = c.X;
        for (int y = c.Y + 1; y < this.currentHeight; ++y) {
            this.originalCoordinates[y - 1][x] = this.originalCoordinates[y][x];
            this.greyscaled[y - 1][x] = this.greyscaled[y][x];
        }
    }

    private void updateCarvedProportions() {
        if (this.mode == 0) {
            this.currentWidth--;
        } else {
            this.currentHeight--;
        }
    }

    protected BufferedImage generateCarvedImage() {
        BufferedImage carvedImage = this.newEmptyOutputSizedImage();
        for (int y = 0; y < carvedImage.getHeight(); ++y) {
            for (int x = 0; x < carvedImage.getWidth(); ++x) {
                final Coordinate originalPixelCoordinate = this.originalCoordinates[y][x];
                final int pixelRGB = this.workingImage.getRGB(originalPixelCoordinate.X, originalPixelCoordinate.Y);
                carvedImage.setRGB(x, y, pixelRGB);
            }
        }
        return carvedImage;
    }

    public BufferedImage showSeams( boolean showVerticalSeams,  int seamColorRGB) {
        this.dPdata = new double[currentHeight][currentWidth];
        this.minCoordinates = new int[currentHeight][currentWidth];
        int numOfSeamsToRemove;
        if (showVerticalSeams) {
            this.mode = 0;
            numOfSeamsToRemove = Math.abs(outWidth - inWidth);
        } else {
            this.mode = 1;
            numOfSeamsToRemove = Math.abs(outHeight - inHeight);
        }
        carveSeams(numOfSeamsToRemove);
        return generateSeamImage(seamColorRGB);
    }

    private BufferedImage generateSeamImage( int seamColorRGB) {
        final BufferedImage seamImage = this.duplicateWorkingImage();
        Coordinate[][] seamList;
        if (this.mode == 0) {
            seamList = vSeams;
        } else {
            seamList = hSeams;
        }
        Coordinate[][] array;
        for (int length = (array = seamList).length, i = 0; i < length; ++i) {
            final Coordinate[] seam = array[i];
            Coordinate[] array2;
            for (int length2 = (array2 = seam).length, j = 0; j < length2; ++j) {
                final Coordinate XY = array2[j];
                seamImage.setRGB(XY.X, XY.Y, seamColorRGB);
            }
        }
        return seamImage;
    }
}
