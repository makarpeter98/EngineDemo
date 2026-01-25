package org.example.App;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;
import org.example.Controller.CarController;
import org.example.Controller.InputController;
import org.example.Controller.CameraController;
import org.example.Model.CarModel;
import org.example.Model.WorldModel;
import org.example.View.CameraView;
import org.example.View.CarView;
import org.example.View.GroundView;
import org.example.View.LightningView;

public class GameApp extends SimpleApplication {

    private BulletAppState physics;
    private CarController carController;
    private CameraController cameraController;

    public static void main(String[] args) {
        GameApp app = new GameApp();

        AppSettings settings = new AppSettings(true);
        settings.setResolution(1200, 800);
        settings.setTitle("Engine Demo");
        settings.setVSync(true);

        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);

        // Fizikai világ
        physics = new BulletAppState();
        stateManager.attach(physics);

        cameraController = new CameraController(cam, inputManager);

        // MODELS
        CarModel carModel = new CarModel();
        WorldModel worldModel = new WorldModel();

        // VIEWS
        CarView carView = new CarView(assetManager);
        GroundView groundView = new GroundView(assetManager, worldModel);
        groundView.initPhysics(physics);
        LightningView lightningView = new LightningView(rootNode);

        rootNode.attachChild(carView.model);
        rootNode.attachChild(groundView.geom);

        // CONTROLLERS
        carController = new CarController(carModel, carView);
        carController.initPhysics(physics);

        // INPUT
        InputController input = new InputController(carController);
        input.register(inputManager);
    }

    @Override
    public void simpleUpdate(float tpf) {
        carController.update(tpf);
        cameraController.follow(carController.getPosition());
    }
}
