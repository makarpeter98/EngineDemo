package org.example.Controller;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.example.Model.CarModel;
import org.example.View.CarView;

public class CarController {

    public boolean forward, backward, left, right;

    private final CarModel model;
    private final CarView view;
    private RigidBodyControl physics;

    public CarController(CarModel model, CarView view) {
        this.model = model;
        this.view = view;
    }

    public void initPhysics(BulletAppState physicsState) {

        // Bounding box félméretek a modellből
        Vector3f ext = view.getExtent();

        // BoxCollisionShape pontosan a modell mérete alapján
        BoxCollisionShape shape = new BoxCollisionShape(ext);

        // Fizika (800kg tömeg, teheted módosítani)
        physics = new RigidBodyControl(shape, 800f);

        view.model.addControl(physics);
        physicsState.getPhysicsSpace().add(physics);

        // Spawn: a box alja a talajon legyen
        physics.setPhysicsLocation(new Vector3f(0, ext.y, 0));

        // Stabilitás: csúszás, forgás csillapítás, tapadás
        physics.setDamping(0.2f, 1f); // lineáris, szögcsillapítás
        physics.setFriction(2.5f);
        physics.setRestitution(0f);

        physics.setLinearVelocity(Vector3f.ZERO);
        physics.setAngularVelocity(Vector3f.ZERO);
    }

    public void update(float tpf) {

        Vector3f vel = physics.getLinearVelocity();

        // Előre irány a fizikai forgatás alapján
        Vector3f forwardDir = physics.getPhysicsRotation().mult(Vector3f.UNIT_Z).negate();

        // Gáz / fék
        if (forward) vel = vel.add(forwardDir.mult(model.acceleration * tpf));
        if (backward) vel = vel.add(forwardDir.negate().mult(model.acceleration * tpf));

        // Sebesség limit
        if (vel.length() > model.maxSpeed)
            vel = vel.normalize().mult(model.maxSpeed);

        physics.setLinearVelocity(vel);

        // Kormányzás: Y tengely körül
        if (left) rotateY(model.steeringSpeed * tpf);
        if (right) rotateY(-model.steeringSpeed * tpf);
    }

    // Forgatás Y tengely körül
    private void rotateY(float angle) {
        Quaternion rot = physics.getPhysicsRotation();
        Quaternion add = new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y);
        physics.setPhysicsRotation(rot.mult(add));
    }

    public Vector3f getPosition() {
        return physics.getPhysicsLocation();
    }
}
