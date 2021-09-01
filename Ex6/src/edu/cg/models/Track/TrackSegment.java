package edu.cg.models.Track;

import edu.cg.algebra.Point;
import edu.cg.models.Box;
import edu.cg.models.IRenderable;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL21.*;

public class TrackSegment implements IRenderable {
  public static final double ASPHALT_WIDTH = 20.0D;

  public static final double ASPHALT_LENGTH = 10.0D;

  public static final double GRASS_WIDTH = 10.0D;

  public static final double GRASS_LENGTH = 10.0D;

  public static final double TRACK_SEGMENT_LENGTH = 500.0D;

  public static final double BOX_LENGTH = 1.5D;

  private LinkedList<Point> boxesLocations; // Stores the boxes centroids (center points).

  private Box box; // Used to represent a wooden box.


  public void setDifficulty(double difficulty) {
    // Set the difficulty of the track segment. Here you decide what are the boxes locations.
    // We provide a simple implementation. You can change it if you want. But if you do decide to use it,
    // then it is your responsibility to understand the logic behind it.
    // Note: In our implementation, the difficulty is the probability of a box to appear in the scene.
    // We divide the scene into rows of boxes and we sample boxes according the difficulty probability.
    difficulty = Math.min(difficulty, Track.MAXIMUM_DIFFICULTY);
    difficulty = Math.max(difficulty, Track.MINIMUM_DIFFICULTY);
    double numberOfLanes = 4.0D;
    double deltaZ;
    if (difficulty < 0.25D) {
      deltaZ = 100.0D;
    } else if (difficulty < 0.5D) {
      deltaZ = 75.0D;
    } else {
      deltaZ = 50.0D;
    }
    this.boxesLocations = new LinkedList<>();
    for (double dz = deltaZ; dz < TRACK_SEGMENT_LENGTH - BOX_LENGTH / 2.0; dz += deltaZ) {
      int cnt = 0; // Number of boxes sampled at each row.
      boolean flag = false;
      for (int i = 0; i < 12; i++) {
        double dx = -((double) numberOfLanes / 2.0) * ((ASPHALT_WIDTH - 2.0) / numberOfLanes) + BOX_LENGTH / 2.0
                + i * BOX_LENGTH;
        if (Math.random() < difficulty) {
          boxesLocations.add(new Point(dx, BOX_LENGTH / 2.0, -dz));
          cnt += 1;
        } else if (!flag) {// The first time we don't sample a box then we also don't sample the box next to. We want enough space for the car to pass through.
          i += 1;
          flag = true;
        }
        if (cnt > difficulty * 10) {
          break;
        }
      }
    }
  }

  public TrackSegment(double difficulty) {
    this.box = new Box(BOX_LENGTH, true);
    setDifficulty(difficulty);
  }

  public void render() {
    //Render the track segment
    renderBoxes();
    renderAsphalt();
    renderGrass();
  }

  private void renderBoxes() {
    TrackMaterials.setWoodenBoxMaterial();
    for (Point p : this.boxesLocations) {
      glPushMatrix();
      glTranslated(p.x, BOX_LENGTH / 2.0D, p.z);
      this.box.render();
      glPopMatrix();
    }
  }

  private void renderAsphalt() {
    TrackMaterials.setAsphaltMaterial();
    glPushMatrix();
    renderQuadraticTexture(ASPHALT_WIDTH, ASPHALT_LENGTH, 20, TRACK_SEGMENT_LENGTH);
    glPopMatrix();
  }

  private void renderGrass() {

    glPushMatrix();
    TrackMaterials.setGreenMaterial();
    double dx = ASPHALT_WIDTH / 2.0 + GRASS_WIDTH / 2.0;
    glTranslated(dx, 0.0D, 0.0D);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    renderQuadraticTexture(GRASS_WIDTH, GRASS_LENGTH, 2, TRACK_SEGMENT_LENGTH);
    glTranslated(-2.0D * dx, 0.0D, 0.0D);
    renderQuadraticTexture(GRASS_WIDTH, GRASS_LENGTH, 2, TRACK_SEGMENT_LENGTH);
    glPopMatrix();
  }

  private void renderQuadraticTexture( double quadWidth, double quadDepth, int split, double totalDepth) {
    glColor3d(1.0D, 0.0D, 0.0D);
    glColor3d(1.0D, 0.0D, 0.0D);
    glNormal3d(0.0D, 1.0D, 0.0D);
    double d = 1.0D / split;
    double dz = quadDepth / split;
    double dx = quadWidth / split;
    for (double tz = 0.0D; tz < totalDepth; tz += quadDepth) {
      for (double i = 0.0D; i < split; i++) {
        glBegin(GL_TRIANGLE_STRIP);
        for (double j = 0.0D; j <= split; j++) {
          glTexCoord2d(j * d, (i + 1.0D) * d);
          glVertex3d(-quadWidth / 2.0D + j * dx, 0.0D, -tz - (i + 1.0D) * dz);
          glTexCoord2d(j * d, i * d);
          glVertex3d(-quadWidth / 2.0D + j * dx, 0.0D, -tz - i * dz);
        }
        glEnd();
      }
    }
  }

  public void init() {
    this.box.init();
  }
}