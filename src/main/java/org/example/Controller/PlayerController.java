package org.example.Controller;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import org.example.Model.PlayerModel;
import org.example.View.PlayerView;

public class PlayerController {

    private final PlayerModel model;
    private final RigidBodyControl physics;
    private final PlayerView playerView;

    public boolean forward, backward, left, right, jump, reset;

    private boolean onGround = false;

    public PlayerController(PlayerModel model, RigidBodyControl physics, PlayerView playerView) {
        this.model = model;
        this.physics = physics;
        this.playerView = playerView;
    }

    public void update(float tpf) {

        Vector3f vel = physics.getLinearVelocity();
        Vector3f moveDir = new Vector3f();

        // irányok
        if (forward)  moveDir.z -= 1;
        if (backward) moveDir.z += 1;
        if (left)     moveDir.x -= 1;
        if (right)    moveDir.x += 1;

        if (moveDir.lengthSquared() > 0) moveDir.normalizeLocal();

        // Csak talajon mozgatunk
        if (checkGround()) {
            Vector3f desiredVel = moveDir.mult(model.maxSpeed);
            Vector3f deltaVel = desiredVel.subtract(new Vector3f(vel.x, 0, vel.z));
            // finom impulzus
            physics.applyCentralImpulse(deltaVel.mult(physics.getMass() * 0.5f));
        }

        // Ugrás
        if (jump && checkGround()) {
            physics.applyImpulse(new Vector3f(0, 7f * physics.getMass(), 0), Vector3f.ZERO);
        }

        // Leesés és respawn
        if (playerView.geom.getWorldTranslation().y < -10 || reset) {
            respawn();
            reset = false;
        }

        // Geometria dőlése
        Vector3f horizontalVel = new Vector3f(vel.x, 0, vel.z);
        float tiltX = -horizontalVel.z * 0.1f;
        float tiltZ = horizontalVel.x * 0.1f;
        playerView.geom.setLocalRotation(new Quaternion().fromAngles(tiltX, 0, tiltZ));
    }


    // Talajdetektálás raycast-tel
    private boolean checkGround() {
        Vector3f origin = physics.getPhysicsLocation().add(0, 0.01f, 0);
        Vector3f down = new Vector3f(0, -1, 0);

        var results = physics.getPhysicsSpace().rayTest(
                origin,
                origin.add(down.mult(0.55f))
        );

        return results.stream().anyMatch(r -> r.getCollisionObject() != physics);
    }

    // Reset / respawn a spawn pozícióra
    private void respawn() {
        physics.setPhysicsLocation(model.spawnPosition.clone());
        physics.setLinearVelocity(Vector3f.ZERO);
        physics.setAngularVelocity(Vector3f.ZERO);
    }

    // Simított gyorsulás
    private float approach(float current, float target, float delta) {
        if (current < target) return Math.min(current + delta, target);
        if (current > target) return Math.max(current - delta, target);
        return current;
    }
}
