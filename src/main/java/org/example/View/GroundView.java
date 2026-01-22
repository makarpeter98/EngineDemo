package org.example.View;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class GroundView {

    public Geometry geom;

    public GroundView(AssetManager assetManager) {

        float size = 500f;

        // A Quad XY síkban van → ezért XZ síkra tesszük fordítással
        Quad quad = new Quad(size, size);

        quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{
                0, 0,
                50, 0,
                50, 50,
                0, 50
        });

        geom = new Geometry("Ground", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/background.png");
        tex.setWrap(WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);

        geom.setMaterial(mat);
        geom.setShadowMode(RenderQueue.ShadowMode.Receive);

        // A Quad XY síkban van → így tesszük XZ síkra:
        geom.rotate(-FastMath.HALF_PI, 0, 0);

        // A Quad bal alsó sarka az origó → középre tesszük
        geom.setLocalTranslation(-size / 2f, 0, size / 2f);
    }
}
