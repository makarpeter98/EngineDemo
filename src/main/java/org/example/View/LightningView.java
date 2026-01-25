package org.example.View;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class LightningView {

    public LightningView(Node root) {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -1).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        root.addLight(sun);

        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White.mult(0.3f));
        root.addLight(amb);
    }
}
