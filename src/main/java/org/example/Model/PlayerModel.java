package org.example.Model;

import com.jme3.math.Vector3f;

public class PlayerModel {

    public float maxSpeed = 10f;
    public float accelTime = 2f;
    public float acceleration = maxSpeed / accelTime;

    public Vector3f spawnPosition = new Vector3f(0, 1f, 0);
}
