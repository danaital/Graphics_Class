package edu.cg;

import java.util.Timer;
import java.util.TimerTask;


import edu.cg.algebra.Vec;
/**
 * This class is already implemented. Use it to track the car movement when the user interacts with the game.
 * @author MOAB
 *
 */
public class GameState {
  public void dispose() {
    timer.cancel();
  }

  public enum SteeringState {
    LEFT, STRAIGHT, RIGHT;
  }

  public enum AccelerationState {
    GAS, CRUISE, BREAKS;
  }

  private SteeringState steeringState;
  private AccelerationState accelerationState;
  private double carVelocity;
  private Vec nextTranslation;
  private Timer timer;
  private final long TIMER_INTERVAL_MS = (long) (1000.0 / 120.0);
  private final double MAX_ROTATION = 20.0;
  private final double MAX_TRANSLATION_X = 5;
  private final double MAX_VELOCITY = 40.0; // m / sec
  private final double CAR_ACCELERATION = 7.0; // m / sec^2

  public GameState() {
    steeringState = SteeringState.STRAIGHT;
    accelerationState = AccelerationState.CRUISE;
    carVelocity = 0.0;
    nextTranslation = new Vec(0.0, 0.0, 0.0);
    timer = new Timer();
    timer.schedule(new UpdateTranslation(), 0, TIMER_INTERVAL_MS);

  }

  private synchronized double getCarVelocity() {
    return carVelocity;
  }

  private synchronized void updateCarVelocity(double newVelocity) {
    carVelocity = Math.max(0.0, newVelocity);
    carVelocity = Math.min(MAX_VELOCITY, carVelocity);
  }

  private synchronized double getCarAcceleration() {
    switch (accelerationState) {
      case GAS:
        return CAR_ACCELERATION;
      case CRUISE:
        return -2.0 * CAR_ACCELERATION;
      case BREAKS:
        return -5.0 * CAR_ACCELERATION;
    }
    return 0.0;
  }

  /**
   *
   * @return The rotation of the car about its axis. The rotation value
   * 		   depends on the steering state (RIGHT, LEFT or Straight).
   */
  public synchronized double getCarRotation() {
    switch (steeringState) {
      case LEFT:
        return -MAX_ROTATION;
      case RIGHT:
        return MAX_ROTATION;
      case STRAIGHT:
        return 0.0;
    }
    return 0.0;
  }

  public synchronized void updateSteering(SteeringState newState) {
    this.steeringState = newState;
  }

  public synchronized void updateAcceleration(AccelerationState newState) {
    this.accelerationState = newState;
  }

  private synchronized void updateNextTranslation(Vec deltaTranslation) {
    this.nextTranslation = this.nextTranslation.add(deltaTranslation);
    this.nextTranslation.x = (float) Math.max(nextTranslation.x, -1.0 * MAX_TRANSLATION_X);
    this.nextTranslation.x = (float) Math.min(nextTranslation.x, MAX_TRANSLATION_X);
  }

  /**
   *
   * The method returns the offset by which the car moved, relative to the last time this method was invoked.
   * So if the method was invoked at t1 and t2. And assume that car position at t1 is p.x, p.y and p.z. So the new
   * car position at t2 is p.x+d.x, p.y+d.y and p.z+d.z, where d is the returned value of getNextTranslation at t2.
   *
   * @return The offset by which the car moved.
   */
  public synchronized Vec getNextTranslation() {
    Vec retVal = new Vec(this.nextTranslation);
    this.nextTranslation = new Vec(0.0);
    return retVal;
  }

  class UpdateTranslation extends TimerTask {

    @Override
    public void run() {
      double theta = (getCarRotation() * Math.PI) / 180.0;
      double cosTheta = Math.cos(theta);
      double sinTheta = Math.sin(theta);
      double currentCarVelocity = getCarVelocity();
      double currentCarAcceleration = getCarAcceleration();
      currentCarAcceleration += -.1 * Math.abs(sinTheta) * CAR_ACCELERATION;
      double dt = (double) TIMER_INTERVAL_MS / 1000.0; // move from msec -> sec
      double dr = (currentCarVelocity * dt + currentCarAcceleration * dt * dt);
      double dz = Math.min(0.0, -cosTheta * dr);
      double dx = sinTheta * dr;
      double newVelocity = Math.min(MAX_VELOCITY, currentCarVelocity + cosTheta * currentCarAcceleration * dt);
      updateCarVelocity(newVelocity);
      updateNextTranslation(new Vec(dx, 0.0, dz));
    }

  }
}
