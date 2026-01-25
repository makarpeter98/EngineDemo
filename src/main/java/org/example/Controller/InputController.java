package org.example.Controller;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class InputController implements ActionListener {

    private final CarController car;

    public InputController(CarController car) {
        this.car = car;
    }

    public void register(InputManager input) {
        input.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        input.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        input.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        input.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));

        input.addListener(this, "Forward", "Backward", "Left", "Right");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "Forward" -> car.forward = isPressed;
            case "Backward" -> car.backward = isPressed;
            case "Left" -> car.left = isPressed;
            case "Right" -> car.right = isPressed;
        }
    }
}
