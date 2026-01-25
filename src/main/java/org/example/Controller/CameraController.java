package org.example.Controller;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class CameraController implements AnalogListener, ActionListener {

    private final Camera cam;
    private final InputManager input;

    private float distance = 20f;
    private float minDistance = 5f;
    private float maxDistance = 60f;

    private float horizontalAngle = 0f;
    private float verticalAngle = 0.5f;

    private boolean rotating = false;

    private Vector3f target = new Vector3f();

    public CameraController(Camera cam, InputManager input) {
        this.cam = cam;
        this.input = input;
        registerInput();
    }

    private void registerInput() {
        input.addMapping("CamRotate", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        input.addMapping("CamLeft", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        input.addMapping("CamRight", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        input.addMapping("CamUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        input.addMapping("CamDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        input.addMapping("CamZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        input.addMapping("CamZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        input.addListener(this, "CamRotate");
        input.addListener(this, "CamLeft", "CamRight", "CamUp", "CamDown");
        input.addListener(this, "CamZoomIn", "CamZoomOut");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("CamRotate")) rotating = isPressed;
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (rotating) {
            if (name.equals("CamLeft")) horizontalAngle += value * 2f;
            if (name.equals("CamRight")) horizontalAngle -= value * 2f;
            if (name.equals("CamUp")) verticalAngle += value * 2f;
            if (name.equals("CamDown")) verticalAngle -= value * 2f;

            verticalAngle = FastMath.clamp(verticalAngle, 0.1f, 1.2f);
        }

        if (name.equals("CamZoomIn")) distance -= value * 20f;
        if (name.equals("CamZoomOut")) distance += value * 20f;

        distance = FastMath.clamp(distance, minDistance, maxDistance);
    }

    public void follow(Vector3f pos) {
        target.set(pos);

        float x = target.x + FastMath.cos(horizontalAngle) * distance;
        float z = target.z + FastMath.sin(horizontalAngle) * distance;
        float y = target.y + FastMath.sin(verticalAngle) * distance;

        cam.setLocation(new Vector3f(x, y, z));
        cam.lookAt(target, Vector3f.UNIT_Y);
    }
}
