package org.example.View;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.asset.AssetManager;

public class PlayerView {

    public Geometry geom;

    public PlayerView(AssetManager assetManager) {
        Box box = new Box(0.25f, 0.5f, 0.25f);
        geom = new Geometry("Player", box);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Blue);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 16f);

        geom.setMaterial(mat);
        geom.setShadowMode(RenderQueue.ShadowMode.Cast);
    }
}
