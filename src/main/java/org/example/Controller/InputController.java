package org.example.Controller;

import com.jme3.input.controls.ActionListener;

public class InputController implements ActionListener {

    private final PlayerController player;

    public InputController(PlayerController player) {
        this.player = player;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "Forward" -> player.forward = isPressed;
            case "Backward" -> player.backward = isPressed;
            case "Left" -> player.left = isPressed;
            case "Right" -> player.right = isPressed;
            case "Jump" -> player.jump = isPressed;
            case "Reset" -> player.reset = isPressed;
        }
    }
}
