package edu.cg;

import edu.cg.algebra.Vec;
import edu.cg.models.Locomotive.Locomotive;
import edu.cg.models.Locomotive.Specification;
import edu.cg.models.Track.Track;
import edu.cg.models.Track.TrackSegment;
import edu.cg.util.glu.GLU;
import static edu.cg.util.glu.Project.gluPerspective;
import static org.lwjgl.opengl.GL21.*;

/**
 * An OpenGL model viewer
 */
public class Viewer {
    int canvasWidth, canvasHeight;
    private final GameState gameState; // Tracks the vehicle movement and orientation
    private final Locomotive car; // The locomotive we wish to render.
    private final Track gameTrack; // The track we wish to render.
    // driving direction, or looking down on the scene from above.
    private Vec carCameraTranslation; // The accumulated translation that should be applied on the car and camera.
    private boolean isModelInitialized = false; // Indicates whether initModel was called.
    private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
    private boolean isBirdseyeView = false; // Indicates whether the camera's perspective corresponds to the vehicle's

    private final double[] carInitialPosition = {0, 1, -4.5};
    private final double[] camInitPos3Person = {0, 3.5, 0};
    private final double[] camInitPosBirdseye = {0, 51.0, -22};
    private final double scale = 3.0;

    // - Camera initial position for standard 3rd person mode(should be fixed)
    // - Camera initial position for birdseye view)
    // - Light colors
    // Or in short anything reusable - this make it easier for your to keep track of your implementation.


    public Viewer(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.gameState = new GameState();
        this.gameTrack = new Track();
        this.carCameraTranslation = new Vec(0.0D);
        this.car = new Locomotive();

    }

    public void render() {
        if (!this.isModelInitialized)
            initModel();
        if (this.isDayMode) {
            // use gl.glClearColor() function.
            glClearColor(60f / 255f, 99f / 255f, 1f, 50f / 255f);
        } else {
            glClearColor(0f, 0f, 50f / 255f, 100f / 255f);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // Step (1) Update the accumulated translation that needs to be
        // applied on the car, camera and light sources.
        updateCarCameraTranslation();
        // Step (2) Position the camera and setup its orientation.
        setupCamera();
        // Step (3) setup the lights.
        setupLights();
        // Step (4) render the car.
        renderVehicle();
        // Step (5) render the track.
        renderTrack();
    }

    public void init() {
        glCullFace(GL_BACK);    // Set Culling Face To Back Face
        glEnable(GL_CULL_FACE); // Enable back face culling

        // Enable other flags for OPENGL.
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);


        reshape(0, 0, canvasWidth, canvasHeight);
    }

    private void updateCarCameraTranslation() {
        // Here we update the car and camera translation values (not the ModelView-Matrix).
        // - We always keep track of the car offset relative to the starting
        // point.
        // - We change the track segments here if necessary.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        Vec ret = this.gameState.getNextTranslation();
        this.carCameraTranslation = this.carCameraTranslation.add(ret);
        // Min and Max calls to make sure we do not exceed the lateral boundaries of the track.
        double dx = Math.max(this.carCameraTranslation.x, -TrackSegment.ASPHALT_LENGTH / 2 - 2);
        this.carCameraTranslation.x = (float) Math.min(dx, TrackSegment.ASPHALT_LENGTH / 2 + 2);
        // If the car reaches the end of the track segment, we generate a new segment.
        if (Math.abs(this.carCameraTranslation.z) >= TrackSegment.TRACK_SEGMENT_LENGTH - this.carInitialPosition[2]) {
            this.carCameraTranslation.z = -((float) (Math.abs(this.carCameraTranslation.z) % TrackSegment.TRACK_SEGMENT_LENGTH));
            this.gameTrack.changeTrackSegment();
        }
    }

    private void setupCamera() {
        GLU glu = new GLU();
        float eyeX = this.carCameraTranslation.x;
        float eyeY = this.carCameraTranslation.y;
        float eyeZ = this.carCameraTranslation.z;
        eyeX += this.isBirdseyeView ? (float) (this.camInitPosBirdseye[0]) : (float) (this.camInitPos3Person[0]);
        eyeY += this.isBirdseyeView ? (float) (this.camInitPosBirdseye[1]) : (float) (this.camInitPos3Person[1]);
        eyeZ += this.isBirdseyeView ? (float) (this.camInitPosBirdseye[2]) : (float) (this.camInitPos3Person[2]);
        float upX = 0f;
        float upY = this.isBirdseyeView ? 0f : 1f;
        float upZ = this.isBirdseyeView ? -1f : 0f;
        float centraly = this.isBirdseyeView ? eyeY - 1f : eyeY;
        float centralz = this.isBirdseyeView ? eyeZ : eyeZ - 10f;
        glu.gluLookAt(eyeX, eyeY, eyeZ, eyeX, centraly, centralz, upX, upY, upZ);
    }

    private void setupLights() {
        if (this.isDayMode) {
            // * Remember: switch-off any light sources that were used in night mode and are not use in day mode.
            float[] dayColor = new float[]{1f, 1f, 1f, 1f};
            Vec lightDirection = (new Vec(0d, 1d, 1d)).normalize();
            float[] position = new float[]{lightDirection.x, lightDirection.y, lightDirection.z, 0f};
            glLightfv(GL_LIGHT0, GL_SPECULAR, dayColor);
            glLightfv(GL_LIGHT0, GL_DIFFUSE, dayColor);
            glLightfv(GL_LIGHT0, GL_POSITION, position);
            glLightfv(GL_LIGHT0, GL_AMBIENT, new float[]{0.1f, 0.1f, 0.1f, 1f});
            glEnable(GL_LIGHT0);
            glDisable(GL_LIGHT1);
            glDisable(GL_LIGHT2);
        } else {
            glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[]{0.25f, 0.25f, 0.3f, 1.0f});
            glDisable(GL_LIGHT0);
        }
    }

    private void renderTrack() {
        glPushMatrix();
        this.gameTrack.render();
        glPopMatrix();
    }

    private void renderVehicle() {
        // * Remember: the vehicle's position should be the initial position + the accumulated translation.
        //             This will simulate the car movement.
        // * Remember: the car was modeled locally, you may need to rotate/scale and translate the car appropriately.
        // * Recommendation: it is recommended to define fields (such as car initial position) that can be used during rendering.
        // * You should set up the car lights right before you render the locomotive after the appropriate transformations
        // * have been applied. This ensures that the light sources are fixed to the locomotive (ofcourse all of this
        // * is only relevant to rendering the vehicle in night mode).
        glPushMatrix();
        glTranslated(this.carInitialPosition[0] + (double) this.carCameraTranslation.x,
                this.carInitialPosition[1] + (double) this.carCameraTranslation.y,
                this.carInitialPosition[2] + (double) this.carCameraTranslation.z);
        glRotated(180d - this.gameState.getCarRotation(), 0d, 1d, 0d);
        glScaled(this.scale, this.scale, this.scale);
        if (!this.isDayMode) {
            this.setupBothLights();
        }
        this.car.render();
        glPopMatrix();
    }

    private void setupBothLights() {
        float x = (float) (Specification.FRONT_BODY_WIDTH / 4);
        float y = (float) (Specification.FRONT_BODY_HEIGHT);
        float z = (float) (Specification.FRONT_BODY_DEPTH + Specification.EPS);
        float[] firstLight = new float[]{x, y, z, 1f};
        float[] secondLight = new float[]{-x, y, z, 1f};
        float[] direction = new float[]{0f, 0f, .5f, 0f};
        this.setLight(firstLight, direction, GL_LIGHT1);
        this.setLight(secondLight, direction, GL_LIGHT2);
    }

    private void setLight(float[] position, float[] directions, int color) {
        float[] lightColor = new float[]{184f / 255f, 149f / 255f, 11f / 255f, 1.0f};
        glLightfv(color, GL_POSITION, position);
        glLightf(color, GL_SPOT_CUTOFF, 90.0f);
        glLightfv(color, GL_SPOT_DIRECTION, directions);
        glLightfv(color, GL_SPECULAR, lightColor);
        glLightfv(color, GL_DIFFUSE, lightColor);
        glEnable(color);
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void initModel() {
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_SMOOTH);
        this.gameTrack.init();
        this.car.init();
        this.isModelInitialized = true;
    }


    public void reshape(int x, int y, int width, int height) {
        // We recommend using gluPerspective, which receives the field of view in the y-direction. You can use this
        // method by importing it via:
        // >> import static edu.cg.util.glu.Project.gluPerspective;
        // Further information about this method can be found in the recitation materials.
        glViewport(x, y, width, height);
        canvasWidth = width;
        canvasHeight = height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = (float) width / (float) height;
        float fovy;
        if (this.isBirdseyeView) {
            fovy = 46f;
            gluPerspective(fovy, aspect, (float) (this.camInitPosBirdseye[1] - 10.0), (float) this.camInitPosBirdseye[1] + 10.0f);
        } else {
            fovy = 120.0f;
            gluPerspective(fovy, aspect, .1f, 250f);
        }

    }

    public void toggleNightMode() {
        this.isDayMode = !this.isDayMode;
    }

    public void changeViewMode() {
        this.isBirdseyeView = !this.isBirdseyeView;
    }
}
