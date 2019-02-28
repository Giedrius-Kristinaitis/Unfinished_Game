package com.gasis.digger.logic;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gasis.digger.logic.entities.Vehicle;
import com.gasis.digger.logic.entities.upgrades.Drill;
import com.gasis.digger.logic.entities.upgrades.Engine;
import com.gasis.digger.logic.terrain.Map;
import com.gasis.digger.resources.Resources;
import com.gasis.digger.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Game world. Holds game state, draws the game world and updates it
 */
public class GameWorld {

    // resources used by the game
    private Resources resources;

    // game terrain
    private Map map;

    // seed used to gene the map
    private final int MAP_SEED = 2019;

    // vehicles roaming in the map
    // first index (0) is always the (host) player
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();

    // g value of the world, but it is low because the game takes place underwater
    public static final float GRAVITY = 0.001f;

    // how far the vehicle is allowed to go from the camera's position for it to update
    private final float xMargin = Constants.WIDTH * 0.15f;
    private final float yMargin = Constants.WIDTH * 0.15f * 0.25f;

    /**
     * Default class constructor
     * @param resources resources used by the game
     */
    public GameWorld(Resources resources) {
        this.resources = resources;

        map = new Map(resources.atlas("textures.atlas"));
        map.generateMap(MAP_SEED);

        initializePlayer();
    }

    /**
     * Initialize player's data
     */
    private void initializePlayer() {
        Vehicle player = new Vehicle(resources, Drill.STOCK, Engine.STOCK);
        player.setX(5);
        player.setY(252);

        vehicles.add(player);
    }

    /**
     * Called when the game should render itself
     * @param batch sprite batch to draw sprites with
     * @param delta time elapsed since last render
     */
    public void draw(SpriteBatch batch, float delta) {
        map.draw(batch, delta, (int) vehicles.get(0).getX(), (int) vehicles.get(0).getY());

        for (Vehicle vehicle: vehicles) {
            vehicle.draw(batch, delta);
        }
    }

    /**
     * Called when the game state should be updated
     * @param cam world's camera
     * @param delta time elapsed since last update
     */
    public void update(OrthographicCamera cam, float delta) {
        updateCameraPosition(cam);

        for (Vehicle vehicle: vehicles) {
            vehicle.update(map, delta);
        }
    }

    /**
     * Updates the position of the camera
     *
     * @param cam cam to update
     */
    private void updateCameraPosition(OrthographicCamera cam) {
        float x = vehicles.get(0).getX() + 0.5f;
        float y = vehicles.get(0).getY() + 0.5f;

        float camX = cam.position.x;
        float camY = cam.position.y;

        // update x position
        if (x < camX - xMargin) {
            camX = x + xMargin;
        } else if (x > camX + xMargin) {
            camX = x - xMargin;
        }

        // update y position
        if (y < camY - yMargin) {
            camY = y + yMargin;
        } else if (y >  camY + yMargin) {
            camY = y - yMargin;
        }

        // make sure the camera doesn't go out of the map's bounds
        if (camX < Constants.WIDTH / 2f) {
            camX = Constants.WIDTH / 2f;
        } else if (camX > Map.WIDTH - Constants.WIDTH / 2f) {
            camX = Map.WIDTH - Constants.WIDTH / 2f;
        }

        // only check for the bottom of the map since there might be stuff above the map
        if (camY < Constants.HEIGHT / 2f) {
            camY = Constants.HEIGHT / 2f;
        }

        cam.position.x = camX;
        cam.position.y = camY;
        cam.update();
    }

    /**
     * Called when a key is pressed
     * @param code code of the key
     */
    public void keyDown(int code) {
        code = convertKeyCode(code);

        switch (code) {
            case Keys.UP:
                vehicles.get(0).setAcceleratingUp(true);
                break;
            case Keys.DOWN:
                vehicles.get(0).setAcceleratingDown(true);
                break;
            case Keys.LEFT:
                vehicles.get(0).setAcceleratingLeft(true);
                break;
            case Keys.RIGHT:
                vehicles.get(0).setAcceleratingRight(true);
                break;
        }
    }

    /**
     * Called when a key is released
     * @param code code of the key
     */
    public void keyUp(int code) {
        code = convertKeyCode(code);

        switch (code) {
            case Keys.UP:
                vehicles.get(0).setAcceleratingUp(false);
                break;
            case Keys.DOWN:
                vehicles.get(0).setAcceleratingDown(false);
                break;
            case Keys.LEFT:
                vehicles.get(0).setAcceleratingLeft(false);
                break;
            case Keys.RIGHT:
                vehicles.get(0).setAcceleratingRight(false);
                break;
        }
    }

    /**
     * Converts WASD key codes to UP LEFT RIGHT DOWN
     *
     * @param code key code to convert
     * @return UP DOWN LEFR or RIGHT or the supplied code if it was not A W S or D
     */
    private int convertKeyCode(int code) {
        if (code == Keys.A) {
            return Keys.LEFT;
        } else if (code == Keys.W) {
            return Keys.UP;
        } else if (code == Keys.S) {
            return Keys.DOWN;
        } else if (code == Keys.D) {
            return Keys.RIGHT;
        }

        return code;
    }

    /**
     * Cleans up resources
     */
    public void unloadResources() {
        resources.unload("textures.atlas");
    }
}