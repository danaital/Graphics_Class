package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageProcessor extends FunctioalForEachLoops {

    //MARK: Fields
    public final Logger logger;
    public final BufferedImage workingImage;
    public final RGBWeights rgbWeights;
    public final int inWidth;
    public final int inHeight;
    public final int workingImageType;
    public final int outWidth;
    public final int outHeight;

    //MARK: Constructors
    public ImageProcessor(Logger logger, BufferedImage workingImage,
                          RGBWeights rgbWeights, int outWidth, int outHeight) {
        super(); //Initializing for each loops...

        this.logger = logger;
        this.workingImage = workingImage;
        this.rgbWeights = rgbWeights;
        inWidth = workingImage.getWidth();
        inHeight = workingImage.getHeight();
        workingImageType = workingImage.getType();
        this.outWidth = outWidth;
        this.outHeight = outHeight;
        setForEachInputParameters();
    }

    public ImageProcessor(Logger logger,
                          BufferedImage workingImage,
                          RGBWeights rgbWeights) {
        this(logger, workingImage, rgbWeights,
                workingImage.getWidth(), workingImage.getHeight());
    }

    //MARK: Change picture hue - example
    public BufferedImage changeHue() {
        logger.log("Prepareing for hue changing...");

        int r = rgbWeights.redWeight;
        int g = rgbWeights.greenWeight;
        int b = rgbWeights.blueWeight;
        int max = rgbWeights.maxWeight;

        BufferedImage ans = newEmptyInputSizedImage();

        forEach((y, x) -> {
            Color c = new Color(workingImage.getRGB(x, y));
            int red = r * c.getRed() / max;
            int green = g * c.getGreen() / max;
            int blue = b * c.getBlue() / max;
            Color color = new Color(red, green, blue);
            ans.setRGB(x, y, color.getRGB());
        });

        logger.log("Changing hue done!");

        return ans;
    }

    //MARK: Nearest neighbor - example
    public BufferedImage nearestNeighbor() {
        logger.log("applies nearest neighbor interpolation.");
        BufferedImage ans = newEmptyOutputSizedImage();
        pushForEachParameters();
        setForEachOutputParameters();
        forEach((y, x) -> {
            int imgX = (int) Math.round((x * inWidth) / ((float) outWidth));
            int imgY = (int) Math.round((y * inHeight) / ((float) outHeight));
            imgX = Math.min(imgX, inWidth - 1);
            imgY = Math.min(imgY, inHeight - 1);
            ans.setRGB(x, y, workingImage.getRGB(imgX, imgY));
        });
        popForEachParameters();
        return ans;
    }

    //MARK: Unimplemented methods
    public BufferedImage greyscale() {
        logger.log("Prepareing for greyscaling ...");

        int r = rgbWeights.redWeight;
        int g = rgbWeights.greenWeight;
        int b = rgbWeights.blueWeight;
        BufferedImage ans = newEmptyInputSizedImage();

        forEach((y, x) -> {
            Color c = new Color(workingImage.getRGB(x, y));
            int red = r * c.getRed() / rgbWeights.weightsSum;
            int green = g * c.getGreen() / rgbWeights.weightsSum;
            int blue = b * c.getBlue() / rgbWeights.weightsSum;
            int greyscaled = red + green + blue;
            Color color = new Color(greyscaled, greyscaled, greyscaled);
            ans.setRGB(x, y, color.getRGB());
        });

        logger.log("Grayscaled done!");

        return ans;
    }

    public BufferedImage gradientMagnitude() {
        if (workingImage.getWidth() < 2 && workingImage.getHeight() < 2) {
            throw new RuntimeException("Image is too small!");
        }
        logger.log("Prepareing for gradient magnitude ...");

        int r = rgbWeights.redWeight;
        int g = rgbWeights.greenWeight;
        int b = rgbWeights.blueWeight;
        int max = rgbWeights.maxWeight;
        BufferedImage greyscaled = greyscale();
        BufferedImage gradient = greyscale();
        BufferedImage ans = newEmptyInputSizedImage();
        forEach((y, x) -> {
            ans.setRGB(x, y, gmHelper(gradient, x, y));
        });
        logger.log("Gradient magnitude done!");
        return ans;
    }

    private static int gmHelper(BufferedImage image, int x, int y) {
        // First thing is to take care of the edge cases
        int isXEdge = x == (image.getWidth() - 1) ? -1 : 1;
        int isYEdge = y == (image.getHeight() - 1) ? -1 : 1;

        // Since this is a greyscaled picture we can extract only one of the colors
        int originColor = new Color(image.getRGB(x, y)).getRed();

        // We will measure it to the previous one while taking into consideration the edges
        int xColor = new Color(image.getRGB(x + isXEdge, y)).getRed();
        int yColor = new Color(image.getRGB(x, y + isYEdge)).getRed();

        // Now we will calculate the factor
        int dx = xColor - originColor;
        int dy = yColor - originColor;

        // Next we will calculate the magnidute
        int magnitude = 255 - Math.min(((int) (Math.sqrt(dx * dx + dy * dy) / Math.sqrt(2))), 255);
        return new Color(magnitude, magnitude, magnitude).getRGB();
    }

    public BufferedImage bilinear() {
        this.logger.log("applies bilinear interpolation.");
        BufferedImage ans = this.newEmptyOutputSizedImage();
        pushForEachParameters();
        setForEachOutputParameters();

        forEach((y, x) -> {
            // rescaling the image to given input
            double imgX = ((x * inWidth) / ((double) outWidth));
            double imgY = ((y * inHeight) / ((double) outHeight));
            // cast to int for future parts
            int imgX1 = (int) imgX;
            int imgY1 = (int) imgY;
            // edges cases
            int imgX2 = Math.min(((int) imgX) + 1, inWidth - 1);
            int imgY2 = Math.min(((int) imgY) + 1, inHeight - 1);
            // calculate the factor of distance between the origin
            double dx = imgX2 - imgX;
            double dy = imgY2 - imgY;
            // part 1 of LI
            Color color1 = linearInterpolationY(workingImage, imgX1, imgY1, imgY2, dy);
            Color color2 = linearInterpolationY(workingImage, imgX2, imgY1, imgY2, dy);
            // part 2 of LI
            Color color3 = weigthedAverage(color1, color2, dx);
            // set ans to pic
            ans.setRGB(x, y, color3.getRGB());
        });
        popForEachParameters();
        return ans;
    }

    private static Color linearInterpolationY(BufferedImage image, int x, int y1, int y2, double dy) {
        Color c1 = new Color(image.getRGB(x, y1));
        Color c2 = new Color(image.getRGB(x, y2));
        return weigthedAverage(c1, c2, dy);
    }

    private static Color weigthedAverage(Color c1, Color c2, double factor) {
        double red1 = c1.getRed();
        double green1 = c1.getBlue();
        double blue1 = c1.getGreen();
        double red2 = c2.getRed();
        double green2 = c2.getBlue();
        double blue2 = c2.getGreen();
        int red3 = Math.min(Math.max((int) (red1 * factor + red2 * (1.0 - factor)), 0), 255);
        int green3 = Math.min(Math.max((int) (green1 * factor + green2 * (1.0 - factor)), 0), 255);
        int blue3 = Math.min(Math.max((int) (blue1 * factor + blue2 * (1.0 - factor)), 0), 255);
        return new Color(red3, green3, blue3);
    }

    //MARK: Utilities
    public final void setForEachInputParameters() {
        setForEachParameters(inWidth, inHeight);
    }

    public final void setForEachOutputParameters() {
        setForEachParameters(outWidth, outHeight);
    }

    public final BufferedImage newEmptyInputSizedImage() {
        return newEmptyImage(inWidth, inHeight);
    }

    public final BufferedImage newEmptyOutputSizedImage() {
        return newEmptyImage(outWidth, outHeight);
    }

    public final BufferedImage newEmptyImage(int width, int height) {
        return new BufferedImage(width, height, workingImageType);
    }

    public final BufferedImage duplicateWorkingImage() {
        BufferedImage output = newEmptyInputSizedImage();

        forEach((y, x) ->
                output.setRGB(x, y, workingImage.getRGB(x, y))
        );

        return output;
    }
}
