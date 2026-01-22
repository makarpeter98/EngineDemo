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
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Main extends SimpleApplication {

    private BulletAppState physics;
    private Geometry playerGeom;
    private RigidBodyControl playerPhysics;

    private boolean forward, backward, left, right;
    private final float moveForce = 15f;

    public static void main(String[] args) {
        new Main().start();
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
        BufferedImage image = null;
        try (InputStream is = getClass().getResourceAsStream("/assets/Textures/background.png")) {
            if (is == null) throw new RuntimeException("Nem található a kép!");
            image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image == null) return;

        float imgWidth = image.getWidth();
        float imgHeight = image.getHeight();
        float scale = 0.01f;

        float groundWidth = imgWidth * scale;
        float groundDepth = imgHeight * scale;

        Quad groundQuad = new Quad(groundWidth, groundDepth);
        Geometry ground = new Geometry("Ground", groundQuad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/background.png");
        tex.setWrap(WrapMode.Clamp);
        mat.setTexture("ColorMap", tex);

        ground.setMaterial(mat);
        ground.setShadowMode(RenderQueue.ShadowMode.Receive);

        // XZ síkba forgatás
        ground.rotate(-FastMath.HALF_PI, 0, 0);
        ground.setLocalTranslation(-groundWidth / 2f, 0, groundDepth / 2f);

        rootNode.attachChild(ground);

        RigidBodyControl groundPhy = new RigidBodyControl(0);
        ground.addControl(groundPhy);
        physics.getPhysicsSpace().add(groundPhy);
    }

    // ===================== KARAKTER =====================
    private void initPlayer() {
        // Box állítva: Y a magasság
        Box box = new Box(0.25f, 0.5f, 0.25f); // 0.5 m magas
        playerGeom = new Geometry("Player", box);

        // Forgatás, hogy ne “feküdjön”
        playerGeom.rotate(0, 0, 0); // ha már jó a méret, nem kell

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

        inputManager.addListener(actionListener, "Forward", "Backward", "Left", "Right");
    }

    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        switch (name) {
            case "Forward" -> forward = isPressed;
            case "Backward" -> backward = isPressed;
            case "Left" -> left = isPressed;
            case "Right" -> right = isPressed;
        }
    };

    // ===================== GAME LOOP =====================
    @Override
    public void simpleUpdate(float tpf) {
        Vector3f force = new Vector3f();
        if (forward) force.z -= moveForce;
        if (backward) force.z += moveForce;
        if (left) force.x -= moveForce;
        if (right) force.x += moveForce;

        playerPhysics.applyCentralForce(force);

        Vector3f pos = playerGeom.getWorldTranslation();
        cam.setLocation(pos.add(0, 8, 8));
        cam.lookAt(pos, Vector3f.UNIT_Y);

        if (pos.y < -10) respawn();
    }

    private void respawn() {
        playerPhysics.setPhysicsLocation(new Vector3f(0, 2, 0));
        playerPhysics.setLinearVelocity(Vector3f.ZERO);
        playerPhysics.setAngularVelocity(Vector3f.ZERO);
    }
}
