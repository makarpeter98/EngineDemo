package org.example.View;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class CameraView {

    private final Camera cam;

    public CameraView(Camera cam) {
        this.cam = cam;
    }

    public void follow(Vector3f pos) {
        Vector3f offset = new Vector3f(0, 20, 15);  // magas + hátrébb
        cam.setLocation(pos.add(offset));
        cam.lookAt(pos, Vector3f.UNIT_Y);
    }

}
