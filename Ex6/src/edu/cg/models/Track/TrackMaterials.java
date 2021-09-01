package edu.cg.models.Track;

import static org.lwjgl.opengl.GL21.*;

public class TrackMaterials {
  public static void setGreenMaterial() {
    float[] mat_ambient = { 0.0215f, 0.1745f, 0.0215f, 1f };
    float[] mat_diffuse = { 0.07568f, 0.61424f, 0.07568f, 1f };
    float[] mat_specular = { 0.633f, 0.727811f, 0.633f, 1f };
    float shine = 128f;
    glMaterialf(GL_FRONT, GL_SHININESS, shine);
    glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse);
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
  }

  public static void setAsphaltMaterial() {
    float[] mat_ambient = { 0.15375f, 0.15f, 0.16625f, 1f };
    float[] mat_diffuse = { 0.68275f, 0.67f, 0.72525f, 1f };
    float[] mat_specular = { 0.332741f, 0.328634f, 0.346435f, 1f };
    float shine = 38.4f;
    glMaterialf(GL_FRONT, GL_SHININESS, shine);
    glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse);
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
  }

  public static void setWoodenBoxMaterial() {
    float[] mat_ambient = { 0.4f, 0.4f, 0.4f, 1f };
    float[] mat_diffuse = { 0.714f, 0.4284f, 0.18144f, 1f };
    float[] mat_specular = { 0.393548f, 0.271906f, 0.166721f, 1f };
    float shine = 25.6f;
    glMaterialf(GL_FRONT, GL_SHININESS, shine);
    glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse);
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
  }
}
