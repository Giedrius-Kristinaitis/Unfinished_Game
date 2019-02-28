package com.gasis.digger.logic.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gasis.digger.logic.GameWorld;
import com.gasis.digger.logic.Point;
import com.gasis.digger.logic.entities.upgrades.Drill;
import com.gasis.digger.logic.entities.upgrades.Engine;
import com.gasis.digger.logic.terrain.Map;
import com.gasis.digger.resources.Resources;

import java.util.Random;

/**
 * Drilling vehicle
 */
public class Vehicle {

    // textures for the vehicle
    private Sprite drillSprite;
    private Sprite vehicleSprite;

    // vehicle coordinates in map block coordinate system
    private float x;
    private float y;

    // x and y axis speeds
    private float xSpeed;
    private float ySpeed;

    // is the drill spinning or not
    private boolean drilling = false;

    // how fast can the drill spin
    private float drillSpeed;

    // some numeric stuff used when drilling
    private float drillingSpeed; // how fast can the machine drill
    private int drilledBlockX; // x of the block that is being drilled
    private int drilledBlockY; // y of the block that is being drilled
    private byte drilledBlockType; // what kind of block is being drilled

    // the maximum speed the vehicle is allowed to move in x and y directions
    // using the engine
    private float maxSpeed;

    // how fast can the vehicle fall using only gravity
    private float maxFreeFallingSpeed = 0.1f;

    // x and y axis acceleration speed using the engine
    private float acceleration;

    // is the vehicle accelerating
    private boolean acceleratingRight = false;
    private boolean acceleratingLeft = false;
    private boolean acceleratingUp = false;
    private boolean acceleratingDown = false;

    // random used for visual effects
    private final Random random = new Random();

    // ***** PARTS OF THE VEHICLE ***** //
    private Drill drill;
    private Engine engine;
    // ***** END OF VEHICLE PARTS ***** //

    /**
     * Default class constructor
     *
     * @param resources assets
     * @param drill drill of the vehicle
     */
    public Vehicle(Resources resources, Drill drill, Engine engine) {
        changeParts(drill, engine);

        drillSprite = new Sprite(resources.atlas("textures.atlas").findRegion(drill.getTextureName()));
        drillSprite.setSize(1, 1);
        drillSprite.setOriginCenter();
        drillSprite.setScale(1.2f);

        vehicleSprite = new Sprite(resources.atlas("textures.atlas").findRegion("vehicle_1"));
        vehicleSprite.setSize(1, 1);
        vehicleSprite.setOriginCenter();
        vehicleSprite.setScale(1.1f);
    }

    /**
     * Calculates specs of the vehicle based on it's parts
     */
    private void calculateVehicleSpecs() {
        maxSpeed = engine.getHp() / 20000f;
        acceleration = engine.getHp() / 200000f;
        drillSpeed = engine.getHp() / 300f;
        drillingSpeed = engine.getHp() / 200000f * drill.getSharpness();
    }

    /**
     * Changes vehicle's parts (if a specified part is null, it is ignored and the old
     * value is kept)
     *
     * @param drill new drill
     * @param engine new engine
     */
    public void changeParts(Drill drill, Engine engine) {
        this.drill = drill == null ? this.drill : drill;
        this.engine = engine == null ? this.engine : engine;

        calculateVehicleSpecs();
    }

    /**
     * Called when the vehicle needs to render itself
     *
     * @param batch sprite batch to draw to
     * @param delta time elapsed since the last render (dunno if this is needed, but let's
     * keep it just in case it becomes useful later)
     */
    public void draw(SpriteBatch batch, float delta) {
        drillSprite.draw(batch);
        vehicleSprite.draw(batch);
    }

    /**
     * Called when the vehicle should update it's state
     *
     * @param map game map
     * @param delta time elapsed since the last update (dunno if this is needed, but let's
     * keep it just in case it becomes useful later)
     */
    @SuppressWarnings("unused")
    public void update(Map map, float delta) {
        // offsets are used to add shaking effect when drilling
        float xOffset = 0;
        float yOffset = 0;

        if (drilling) {
            xOffset = random.nextFloat() * 0.015f * (random.nextBoolean() ? -1 : 1);
            yOffset = random.nextFloat() * 0.015f * (random.nextBoolean() ? -1 : 1);
        }

        this.x += xOffset;
        this.y += yOffset;

        drillSprite.setPosition(this.x, this.y);
        vehicleSprite.setPosition(this.x, this.y);

        this.x -= xOffset;
        this.y -= yOffset;

        // rotate the drill
        rotateDrill();

        // check for collisions and update movement
        checkForMapCollisionsAndUpdateMovementSpeed(map);

        // make sure the vehicle doesn't go out of the map's bounds
        preventGoingOutOfBounds();

        // check if any block needs to be drilled and drill it
        tryToStartDrilling(map);

        // update drilling progress
        updateDrilling(map);

        // update position
        this.x += xSpeed;
        this.y += ySpeed;
    }

    /**
     * Makes sure the vehicle doesn't go out of the map's bounds. Map instance
     * is not required because it's width and height are static fields
     */
    private void preventGoingOutOfBounds() {
        // check for the left side of the map
        if (x < 0) {
            x = 0;
            xSpeed = 0;
        }

        // check for the right side of the map
        else if (x + 1 > Map.WIDTH) {
            x = Map.WIDTH - 1;
            xSpeed = 0;
        }

        // check for the top of the map
        if (y + 1 > Map.HEIGHT) {
            y = Map.HEIGHT - 1;
            ySpeed = 0;
        }

        // check for the bottom of the map
        else if (y < 0) {
            y = 0;
            ySpeed = 0;
        }
    }

    /**
     * Updates the drill's rotation angle
     */
    private void rotateDrill() {
        if (drilling) {
            float rotation = drillSprite.getRotation();

            if (rotation <= -360) {
                rotation = 0;
            }

            rotation -= drillSpeed;

            drillSprite.setRotation(rotation);
        }
    }

    /**
     * Checks if the vehicle touches any blocks in the map and prevents it from
     * moving through blocks, also, updates the vehicle's speed if it's accelerating
     *
     * @param map map the vehicle is in
     */
    private void checkForMapCollisionsAndUpdateMovementSpeed(Map map) {
        if (drilling) {
            // do not apply gravity or any other forces if drilling
            return;
        }

        float x = this.x + 0.9f;
        float y = this.y + 0.25f;

        // was the vehicle stopped by a block collision or not
        boolean wasStopped = false;

        // apply gravity if the vehicle doesn't touch the ground and is not using engine to move upwards
        Point block = map.blockExistsBelow(x, y);

        if (block == null) {
            x = this.x + 0.1f;
            block = map.blockExistsBelow(x, y);
        }

        if (block != null) {
            if (this.y < block.getY()) {
                this.y = block.getY();
                ySpeed = 0;
                wasStopped = true;
            }
        }

        if (!acceleratingUp && !wasStopped) {
            if (!acceleratingDown) {
                // apply gravity
                float speed = ySpeed - GameWorld.GRAVITY;

                if (speed > maxFreeFallingSpeed) {
                    speed = maxFreeFallingSpeed;
                }

                setYSpeed(speed);
            } else {
                // method should be used here since prevents the new speed from exceeding maximum allowed speed
                setYSpeed(ySpeed - acceleration);
            }
        }


        // update local values, since they might have changed
        wasStopped = false;
        x = this.x + 0.9f;
        y = this.y + 0.75f;

        // accelerate up
        block = map.blockExistsAbove(x, y);

        if (block == null) {
            x = this.x + 0.1f;
            block = map.blockExistsAbove(x, y);
        }

        if ((ySpeed > 0) && block != null) {
            if (this.y > block.getY()) {
                this.y = block.getY();
                ySpeed = 0;
                wasStopped = true;
            }
        }

        if (acceleratingUp && !wasStopped) {
            // method should be used here since prevents the new speed from exceeding maximum allowed speed
            setYSpeed(ySpeed + acceleration);
        }

        // update local values, since they might have changed
        wasStopped = false;
        x = this.x + 0.25f;
        y = this.y + 0.9f;

        // accelerate left
        block = map.blockExistsLeft(x, y);

        if (block == null) {
            y = this.y + 0.1f;
            block = map.blockExistsLeft(x, y);
        }

        if ((xSpeed < 0) && block != null) {
            if (this.x < block.getX()) {
                this.x = block.getX();
                xSpeed = 0;
                wasStopped = true;
            }
        }

        if (!wasStopped) {
            if (acceleratingLeft) {
                // method should be used here since prevents the new speed from exceeding maximum allowed speed
                setXSpeed(xSpeed - acceleration);
            } else {
                // decelerate if moving
                if (xSpeed < 0) {
                    xSpeed += acceleration;

                    if (xSpeed > 0) {
                        xSpeed = 0;
                    }
                }
            }
        }

        // update local values, since they might have changed
        wasStopped = false;
        x = this.x + 0.75f;
        y = this.y + 0.9f;

        // accelerate right
        block = map.blockExistsRight(x, y);

        if (block == null) {
            y = this.y + 0.1f;
            block = map.blockExistsRight(x, y);
        }

        if ((xSpeed > 0) && block != null) {
            if (this.x > block.getX()) {
                this.x = block.getX();
                xSpeed = 0;
                wasStopped = true;
            }
        }

        if (!wasStopped) {
            if (acceleratingRight) {
                // method should be used here since prevents the new speed from exceeding maximum allowed speed
                setXSpeed(xSpeed + acceleration);
            } else {
                // decelerate if moving
                if (xSpeed > 0) {
                    xSpeed -= acceleration;

                    if (xSpeed < 0) {
                        xSpeed = 0;
                    }
                }
            }
        }
    }

    /**
     * Tries to drill a block
     *
     * @param map game map
     * @param x x coordinate of the block
     * @param y y coordinate of the block
     */
    private void tryToDrill(Map map, int x, int y) {
        if (drilling) {
            return;
        }

        byte blockType = map.isBlockDrillable(x, y);

        if (blockType == -1) {
            // the block is not drillable, do nothing
            return;
        }

        xSpeed = 0;
        ySpeed = 0;

        drilling = true;
        drilledBlockType = blockType;
        drilledBlockX = x;
        drilledBlockY = y;
    }

    /**
     * Updates drilling progress
     */
    private void updateDrilling(Map map) {
        if (!drilling) {
            return;
        }

        if (drilledBlockX > x) {
            x += drillingSpeed;
        } else if (drilledBlockX < x) {
            x -= drillingSpeed;
        }

        if (drilledBlockY < y) {
            y -= drillingSpeed;
        } else if (drilledBlockY > y) {
            y += drillingSpeed;
        }

        // stop drilling if the block was drilled through
        if (Math.abs(drilledBlockX - x) <= drillingSpeed * 1.05f
            && Math.abs(drilledBlockY - y) <= drillingSpeed * 1.05f) {
            drilling = false;
            map.removeBlock(drilledBlockX, drilledBlockY);
            addMinedItemToCargo(drilledBlockType);
        }
    }

    /**
     * Adds mined item to the cargo
     * @param drilledBlockType type of the block that was just drilled
     */
    private void addMinedItemToCargo(byte drilledBlockType) {
        // TODO: add to the cargo once it's implemented
    }

    /**
     * Checks if the vehicle is trying to drill any blocks and drills them
     *
     * @param map game map
     */
    private void tryToStartDrilling(Map map) {
        if (Math.abs(xSpeed) > acceleration || Math.abs(ySpeed) > acceleration) {
            return;
        }

        float x = this.x + 0.5f;
        float y = this.y + 0.5f;

        // check if the bottom block needs to be drilled
        if (this.y <= (int) y && acceleratingDown && map.blockExistsBelow(x, y) != null) {
            tryToDrill(map, (int) x, (int) y - 1);
        }

        // check if the left block needs to be drilled
        else if (this.x <= (int) x && acceleratingLeft && map.blockExistsBelow(x, y) != null && map.blockExistsLeft(x, y) != null) {
            tryToDrill(map, (int) x - 1, (int) y);
        }

        // check if the right block needs to be drilled
        else if (this.x >= (int) x && acceleratingRight && map.blockExistsBelow(x, y) != null && map.blockExistsRight(x, y) != null) {
            tryToDrill(map, (int) x + 1, (int) y);
        }

        // check if the top block needs to be drilled (only if the engine allows
        // drilling upwards
        else if (this.y >= (int) y && engine.isDrillingUpwardsAllowed() && acceleratingUp) {
            tryToDrill(map, (int) x, (int) y + 1);
        }
    }

    // x and y getters
    public float getX() { return x; }
    public float getY() { return y; }

    // x and y setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }

    // x and y speed getters
    public float getXSpeed() { return xSpeed; }
    public float getYSpeed() { return ySpeed; }

    // x and y speed setters
    public void setXSpeed(float xSpeed) {
        if (Math.abs(xSpeed) > maxSpeed) {
            this.xSpeed = xSpeed >= 0 ? maxSpeed : -maxSpeed;
        } else {
            this.xSpeed = xSpeed;
        }
    }

    public void setYSpeed(float ySpeed) {
        if (Math.abs(ySpeed) > maxSpeed) {
            this.ySpeed = ySpeed >= 0 ? maxSpeed : -maxSpeed;
        } else {
            this.ySpeed = ySpeed;
        }
    }

    // acceleration getters
    public boolean isAcceleratingLeft() { return acceleratingLeft; }
    public boolean isAcceleratingRight() { return acceleratingRight; }
    public boolean isAcceleratingUp() { return acceleratingUp; }
    public boolean isAcceleratingDown() { return acceleratingDown; }

    // acceleration setters
    public void setAcceleratingRight(boolean acceleratingRight) {
        this.acceleratingRight = acceleratingRight;
    }

    public void setAcceleratingLeft(boolean acceleratingLeft) {
        this.acceleratingLeft = acceleratingLeft;
    }

    public void setAcceleratingUp(boolean acceleratingUp) {
        this.acceleratingUp = acceleratingUp;
    }

    public void setAcceleratingDown(boolean acceleratingDown) {
        this.acceleratingDown = acceleratingDown;
    }
}
