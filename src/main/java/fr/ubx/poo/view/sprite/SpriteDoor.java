/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;

public class SpriteDoor extends SpriteDecor {

    private Door door;

    public SpriteDoor(Pane layer, Position position, Door door) {
        super(layer, null, position);
        this.door = door;
        updateImage();
    }

    @Override
    public void updateImage() {
        setImage(ImageFactory.getInstance().getDoor(this.door.isOpen()));
    }
}
