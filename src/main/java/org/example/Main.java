package org.example;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    private BulletAppState physics;
    private Geometry playerGeom;
    private RigidBodyControl playerPhysics;

    private boolean forward, backward, left, right;
    private final float moveForce = 15f;

    private final float maxSpeed = 10f;      // m/s
    private final float accelTime = 3f;      // 3 másodperc
    private final float acceleration = maxSpeed / accelTime;


    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setResolution(1600, 900);
        settings.setTitle("Engine Demo");
        settings.setVSync(true);

        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);

        initPhysics();
        initGround();
        initPlayer();
        initLights();
        initCamera();
        initInput();
    }

    // ===================== PHYSICS =====================
    private void initPhysics() {
        physics = new BulletAppState();
        stateManager.attach(physics);
        physics.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));
    }

    // ===================== TALAJ =====================
    private void initGround() {
        float groundSize = 500f;

        Quad quad = new Quad(groundSize, groundSize);

        // UV ismétlés
        quad.setBuffer(
                VertexBuffer.Type.TexCoord,
                2,
                new float[]{
                        0, 0,
                        50, 0,
                        50, 50,
                        0, 50
                }
        );

        Geometry ground = new Geometry("Ground", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/background.png");
        tex.setWrap(WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);

        ground.setMaterial(mat);
        ground.setShadowMode(RenderQueue.ShadowMode.Receive);

        // XZ síkra forgatás
        ground.rotate(-FastMath.HALF_PI, 0, 0);
        ground.setLocalTranslation(-groundSize / 2f, 0, groundSize / 2f);

        rootNode.attachChild(ground);

        RigidBodyControl groundPhy = new RigidBodyControl(0);
        ground.addControl(groundPhy);
        physics.getPhysicsSpace().add(groundPhy);
    }

    // ===================== KARAKTER =====================
    private void initPlayer() {
        Box box = new Box(0.25f, 0.5f, 0.25f);
        playerGeom = new Geometry("Player", box);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Blue);
        mat.setColor("Specular", ColorRGBA.White);

        playerGeom.setMaterial(mat);
        playerGeom.setShadowMode(RenderQueue.ShadowMode.Cast);
        rootNode.attachChild(playerGeom);

        playerPhysics = new RigidBodyControl(1f);
        playerGeom.addControl(playerPhysics);
        physics.getPhysicsSpace().add(playerPhysics);

        playerPhysics.setPhysicsLocation(new Vector3f(0, 1f, 0));
    }

    // ===================== FÉNY =====================
    private void initLights() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -1).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(amb);
    }

    // ===================== KAMERA =====================
    private void initCamera() {
        cam.setLocation(new Vector3f(0, 8, 8));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    // ===================== INPUT =====================
    private void initInput() {
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Respawn", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addListener(actionListener,
                "Forward", "Backward", "Left", "Right", "Jump", "Respawn");
    }

    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        switch (name) {
            case "Forward" -> forward = isPressed;
            case "Backward" -> backward = isPressed;
            case "Left" -> left = isPressed;
            case "Right" -> right = isPressed;
            case "Jump" -> { if (isPressed) jump(); }
            case "Respawn" -> { if (isPressed) respawn(); }
        }
    };

    private boolean isOnGround() {
        Vector3f origin = playerGeom.getWorldTranslation();
        Vector3f direction = new Vector3f(0, -1, 0);

        com.jme3.bullet.collision.PhysicsRayTestResult result =
                physics.getPhysicsSpace()
                        .rayTest(origin, origin.add(direction.mult(0.6f)))
                        .stream()
                        .findFirst()
                        .orElse(null);

        return result != null;
    }


    private float approach(float current, float target, float delta) {
        if (current < target) {
            current += delta;
            if (current > target) current = target;
        } else if (current > target) {
            current -= delta;
            if (current < target) current = target;
        }
        return current;
    }


    @Override
    public void simpleUpdate(float tpf) {

        Vector3f vel = playerPhysics.getLinearVelocity();
        Vector3f targetVel = new Vector3f();

        if (forward)  targetVel.z -= maxSpeed;
        if (backward) targetVel.z += maxSpeed;
        if (left)     targetVel.x -= maxSpeed;
        if (right)    targetVel.x += maxSpeed;

        // Ha nincs input, lassuljon le
        if (!forward && !backward) targetVel.z = 0;
        if (!left && !right)       targetVel.x = 0;

        // Gyorsulás a target sebesség felé
        Vector3f newVel = new Vector3f();

        newVel.x = approach(vel.x, targetVel.x, acceleration * tpf);
        newVel.z = approach(vel.z, targetVel.z, acceleration * tpf);
        newVel.y = vel.y; // a gravitációt nem bántjuk

        playerPhysics.setLinearVelocity(newVel);

        // Kamera követés
        Vector3f pos = playerGeom.getWorldTranslation();
        cam.setLocation(pos.add(0, 8, 8));
        cam.lookAt(pos, Vector3f.UNIT_Y);

        if (pos.y < -10) respawn();
    }


    // ===================== RESPAWN =====================
    private void respawn() {
        playerPhysics.setPhysicsLocation(new Vector3f(0, 1f, 0));
        playerPhysics.setLinearVelocity(Vector3f.ZERO);
        playerPhysics.setAngularVelocity(Vector3f.ZERO);
        playerPhysics.setPhysicsRotation(new com.jme3.math.Quaternion());
    }

    // ===================== JUMP =====================
    private void jump() {
        if (isOnGround()) {
            playerPhysics.applyImpulse(new Vector3f(0, 5f, 0), Vector3f.ZERO);
        }
    }
}
