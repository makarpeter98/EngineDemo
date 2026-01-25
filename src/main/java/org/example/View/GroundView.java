package org.example.View;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import org.example.Model.WorldModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class GroundView {

    public Geometry geom;

    public GroundView(AssetManager assetManager, WorldModel world) {
        BufferedImage image = null;
        try (InputStream is = getClass().getResourceAsStream("/Assets/Textures/background.png")) {
            image = ImageIO.read(is);
        } catch (Exception ignored) {}

        float w = image.getWidth() * world.scale * world.sizeMultiplier;
        float h = image.getHeight() * world.scale * world.sizeMultiplier;

        Quad quad = new Quad(w, h);

        float repeat = world.sizeMultiplier;
        quad.scaleTextureCoordinates(new Vector2f(repeat, repeat));

        geom = new Geometry("Ground", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Assets/Textures/background.png");
        tex.setWrap(WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);

        geom.setMaterial(mat);
        geom.setShadowMode(RenderQueue.ShadowMode.Receive);

        geom.rotate(-FastMath.HALF_PI, 0, 0);
        geom.setLocalTranslation(-w / 2f, 0, h / 2f);
    }

    public void initPhysics(com.jme3.bullet.BulletAppState physics) {
        RigidBodyControl groundPhy = new RigidBodyControl(0);
        geom.addControl(groundPhy);
        physics.getPhysicsSpace().add(groundPhy);
    }
}
