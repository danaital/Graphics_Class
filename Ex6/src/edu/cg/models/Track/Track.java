package edu.cg.models.Track;

import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL21.*;

public class Track implements IRenderable {
  private TrackSegment currentTrackSegment;
  private TrackSegment nextTrackSegment;
  private double currentDifficulty = 0.2D;
  private final double DIFFICULTY_DELTA = 0.025;
  public static final double MAXIMUM_DIFFICULTY = 0.75;
  public static final double MINIMUM_DIFFICULTY = 0.2;

  public Track() {
    // Initialize the track
    this.currentTrackSegment = new TrackSegment(this.currentDifficulty);
    this.nextTrackSegment = new TrackSegment(this.currentDifficulty + DIFFICULTY_DELTA);
  }

  public void render() {
    // Render the track by rendering the current and next segment.
    glPushMatrix();
    this.currentTrackSegment.render();
    glTranslated(0.0D, 0.0D, -TrackSegment.TRACK_SEGMENT_LENGTH);
    this.nextTrackSegment.render();
    glPopMatrix();
  }

  public void changeTrackSegment() {
    // Change the current track by switching the current and next track.
    // - We provided an implementation, you can change it if you want.
    TrackSegment tmp = this.currentTrackSegment;
    this.currentTrackSegment = this.nextTrackSegment;
    this.currentDifficulty += DIFFICULTY_DELTA;
    this.currentDifficulty = Math.min(this.currentDifficulty, MAXIMUM_DIFFICULTY);
    tmp.setDifficulty(this.currentDifficulty + DIFFICULTY_DELTA);
    this.nextTrackSegment = tmp;
  }

  public void init() {
    // Initialize the track segments.
    // The init method for both segments will load the textures of the models.
    this.currentTrackSegment.init();
    this.nextTrackSegment.init();
  }
}
