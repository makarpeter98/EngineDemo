package org.example.Controller;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import org.example.Model.PlayerModel;
import org.example.View.PlayerView;

public class PlayerController {

    public boolean forward, backward, left, right;

    private final PlayerModel model;
    private final PlayerView view;
    private RigidBodyControl physics;

    public PlayerController(PlayerModel model, PlayerView view, BulletAppState physicsState) {
        this.model = model;
        this.view = view;
    }

    public void initPhysics(BulletAppState physicsState) {
        physics = new RigidBodyControl(1f);
        view.geom.addControl(physics);
        physicsState.getPhysicsSpace().add(physics);
        physics.setPhysicsLocation(new Vector3f(0, 2, 0));
    }

    public void update(float tpf) {
        Vector3f force = new Vector3f();

        if (forward) force.z -= model.moveForce;
        if (backward) force.z += model.moveForce;
        if (left) force.x -= model.moveForce;
        if (right) force.x += model.moveForce;

        physics.applyCentralForce(force);
    }

    public Vector3f getPosition() {
        return view.geom.getWorldTranslation();
    }
}
