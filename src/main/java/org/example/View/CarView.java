package org.example.View;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;

public class CarView {

    public Spatial model;
    public BoundingBox bounds;

    public CarView(AssetManager assetManager) {
        model = assetManager.loadModel("Assets/Models/Car/vintage_racing_car.glb");
        model.setLocalScale(0.4f);

        model.updateModelBound();
        bounds = (BoundingBox) model.getWorldBound();
    }

    public Vector3f getExtent() {
        return bounds.getExtent(null);
    }
}
