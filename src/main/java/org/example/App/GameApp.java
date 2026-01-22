package org.example.App;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import org.example.Controller.InputController;
import org.example.Controller.PlayerController;
import org.example.Model.PlayerModel;
import org.example.View.GroundView;
import org.example.View.PlayerView;

public class GameApp extends SimpleApplication {

    private BulletAppState physics;
    private PlayerController playerController;
    private PlayerView playerView;

    public static void main(String[] args) {
        GameApp app = new GameApp();

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

        physics = new BulletAppState();
        stateManager.attach(physics);

        // MODEL
        PlayerModel model = new PlayerModel();

        // VIEW
        playerView = new PlayerView(assetManager);
        GroundView groundView = new GroundView(assetManager);

        rootNode.attachChild(playerView.geom);
        rootNode.attachChild(groundView.geom);

        initLights();

        // ===================== PHYSICS =====================

        // PLAYER PHYSICS
        RigidBodyControl playerPhy = new RigidBodyControl(1f);
        playerView.geom.addControl(playerPhy);

        // Gravity
        playerPhy.setGravity(new Vector3f(0, -20f, 0));

        // Angular factor: engedi X/Z dőlést mindig, nem csak leesésnél
        playerPhy.setAngularFactor(new Vector3f(1,0,1)); // X/Z dőlés engedélyezve
        playerPhy.setLinearDamping(0.1f);               // X/Z csillapítás
        playerPhy.setAngularDamping(0.5f);              // forgás csillapítás


        physics.getPhysicsSpace().add(playerPhy);
        playerPhy.setPhysicsLocation(model.spawnPosition.clone());



        // GROUND PHYSICS (STABLE!)
        BoxCollisionShape groundShape =
                new BoxCollisionShape(new Vector3f(250f, 0.1f, 250f));

        RigidBodyControl groundPhy = new RigidBodyControl(groundShape, 0);
        groundPhy.setPhysicsLocation(new Vector3f(0, 0, 0));

        physics.getPhysicsSpace().add(groundPhy);


        // ===================== CONTROLLER =====================
        playerController = new PlayerController(model, playerPhy, playerView);

        // ===================== INPUT =====================
        InputController input = new InputController(playerController);

        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addListener(input, "Forward", "Backward", "Left", "Right", "Jump", "Reset");
    }

    @Override
    public void simpleUpdate(float tpf) {
        playerController.update(tpf);

        Vector3f pos = playerView.geom.getWorldTranslation();
        cam.setLocation(pos.add(0, 8, 8));
        cam.lookAt(pos, Vector3f.UNIT_Y);
    }

    private void initLights() {
        var sun = new com.jme3.light.DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -1).normalizeLocal());
        sun.setColor(com.jme3.math.ColorRGBA.White);
        rootNode.addLight(sun);

        var amb = new com.jme3.light.AmbientLight();
        amb.setColor(com.jme3.math.ColorRGBA.White.mult(0.3f));
        rootNode.addLight(amb);
    }


}
