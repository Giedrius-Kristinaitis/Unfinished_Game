package com.gasis.digger.logic.terrain;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.gasis.digger.logic.Point;
import com.gasis.digger.utils.Constants;

import java.util.Random;

/**
 * Holds game map data
 */
public class Map {

    // ********* BLOCK TYPES ******** //
    /*
        you could say an enum would be better here, but I'm not using them because
        an array of object references (enums are basically turned into singleton objects)
        would take up much more memory than an array of bytes and the array in this
        case is gonna be HUGE
    */
    public static final byte BLOCK_EMPTY = 0;
    public static final byte BLOCK_DIRT = 1;
    // ***** END OF BLOCK TYPES ***** //

    // width of the map in blocks
    public static final int WIDTH = 25;

    // height of the map in blocks
    public static final int HEIGHT = 256;

    // how many blocks is the top layer of water
    public static final int WATER_THICKNESS = 5;

    // map data (blocks)
    private byte[][] data;

    // texture atlas to look for textures in
    private TextureAtlas textures;

    /**
     * Default class constructor
     */
    public Map(TextureAtlas textures) {
        this.textures = textures;

        data = new byte[WIDTH][HEIGHT];
    }

    /**
     * Generates a random map
     *
     * @param seed of the map
     */
    public void generateMap(int seed) {
        Random random = new Random(seed);

        // map characteristics
        int maxEmptyBlocksPerLine = 10;

        // fill the map
        for (int y = HEIGHT - 1; y >= 0; y--) {
            // fill water layer
            if (y >= HEIGHT - WATER_THICKNESS) {
                for (int x = 0; x < WIDTH; x++) {
                    data[x][y] = BLOCK_EMPTY;
                }

                continue;
            }

            // fill in everything else
            int emptyBlocks = random.nextInt(maxEmptyBlocksPerLine);

            // fill the line with dirt
            for (int x = 0; x < WIDTH; x++) {
                data[x][y] = BLOCK_DIRT;
            }

            // add empty spaces
            for (int n = 0; n < emptyBlocks; n++) {
                int emptyBlock = random.nextInt(WIDTH);

                if (data[emptyBlock][y] == BLOCK_EMPTY) {
                    n--;
                    continue;
                }

                data[emptyBlock][y] = BLOCK_EMPTY;
            }
        }
    }

    /**
     * Called when the map should render itself. Player's coordinates are used for
     * rendering efficiency.
     *
     * @param batch sprite batch to draw to
     * @param delta time elapsed since the last render
     * @param playerX player's x coordinate (in map block coordinates)
     * @param playerY player's y coordinate (in map block coordinates)
     */
    @SuppressWarnings("unused")
    public void draw(SpriteBatch batch, float delta, int playerX, int playerY) {
        for (int x = playerX - (int) Constants.WIDTH; x < playerX + (int) Constants.WIDTH; x++) {
            for (int y = playerY - (int) Constants.HEIGHT; y < playerY + (int) Constants.HEIGHT; y++) {
                if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
                    continue;
                }

                // draw map blocks
                switch (data[x][y]) {
                    case BLOCK_DIRT:
                        batch.draw(textures.findRegion("dirt"),
                                x, y, 1, 1);
                        break;
                    case BLOCK_EMPTY:
                        float smallerDimension = 1 / 7f;
                        float mediumDimension = smallerDimension * 2.77f;
                        float largerDimension = 1;

                        // if there is a block in the left draw a side of dirt
                        if (x - 1 >= 0 && data[x - 1][y] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_left"),
                                    x, y, smallerDimension, largerDimension);
                        }

                        // if there is a block to the right draw a side of dirt
                        if (x + 1 < WIDTH && data[x + 1][y] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_right"),
                                    x + 1 - smallerDimension, y, smallerDimension, largerDimension);
                        }

                        // if there is a block to the top draw a side of dirt
                        if (y + 1 < HEIGHT && data[x][y + 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_top"),
                                    x, y + 1 - smallerDimension, largerDimension, smallerDimension);
                        }

                        // if there is a block to the bottom draw a side of dirt
                        if (y - 1 >= 0 && data[x][y - 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_bottom"),
                                    x, y, largerDimension, smallerDimension);
                        }

                        // if there is a block to the left and top draw a rounded corner
                        if (x - 1 >= 0 && y + 1 < HEIGHT && data[x - 1][y] != BLOCK_EMPTY && data[x][y + 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_rounded_top_left"),
                                    x, y + 1 - mediumDimension, mediumDimension, mediumDimension);
                        }

                        // if there are no blocks to the left and top check if a corner needs to be drawn
                        else if (x - 1 >= 0 && y + 1 < HEIGHT && data[x - 1][y] == BLOCK_EMPTY && data[x][y + 1] == BLOCK_EMPTY && data[x - 1][y + 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_top_left"),
                                    x, y + 1 - smallerDimension, smallerDimension, smallerDimension);
                        }

                        // if there is a block to the right and top draw a rounded corner
                        if (x + 1 < WIDTH && y + 1 < HEIGHT && data[x + 1][y] != BLOCK_EMPTY && data[x][y + 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_rounded_top_right"),
                                    x + 1 - mediumDimension, y + 1 - mediumDimension, mediumDimension, mediumDimension);
                        }

                        // if there are no blocks to the right and top check if a corner needs to be drawn
                        else if (x + 1 < WIDTH && y + 1 < HEIGHT && data[x + 1][y] == BLOCK_EMPTY && data[x][y + 1] == BLOCK_EMPTY && data[x + 1][y + 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_top_right"),
                                    x + 1 - smallerDimension, y + 1 - smallerDimension, smallerDimension, smallerDimension);
                        }

                        // if there is a block to the right and bottom draw a rounded corner
                        if (x + 1 < WIDTH && y - 1 >= 0 && data[x + 1][y] != BLOCK_EMPTY && data[x][y - 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_rounded_bottom_right"),
                                    x + 1 - mediumDimension, y, mediumDimension, mediumDimension);
                        }

                        // if there are no blocks to the right and bottom check if a corner needs to be drawn
                        else if (x + 1 < WIDTH && y - 1 >= 0 && data[x + 1][y] == BLOCK_EMPTY && data[x][y - 1] == BLOCK_EMPTY && data[x + 1][y - 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_bottom_right"),
                                    x + 1 - smallerDimension, y, smallerDimension, smallerDimension);
                        }

                        // if there is a block to the left and bottom draw a rounded corner
                        if (x - 1 >= 0 && y - 1 >= 0 && data[x - 1][y] != BLOCK_EMPTY && data[x][y - 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_rounded_bottom_left"),
                                    x, y, mediumDimension, mediumDimension);
                        }

                        // if there are no blocks to the left and bottom check if a corner needs to be drawn
                        else if (x - 1 >= 0 && y - 1 >= 0 && data[x - 1][y] == BLOCK_EMPTY && data[x][y - 1] == BLOCK_EMPTY && data[x - 1][y - 1] != BLOCK_EMPTY) {
                            batch.draw(textures.findRegion("dirt_bottom_left"),
                                    x, y, smallerDimension, smallerDimension);
                        }

                        break;
                }
            }
        }
    }

    /**
     * Checks if a block below specified coordinates exists
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return supplied coordinates if block exists, null if it doesn't
     */
    public Point blockExistsBelow(float x, float y) {
        if (x >= 0 && x < WIDTH && y - 1 >= 0 && y - 1 < HEIGHT && data[(int) x][(int) y - 1] != BLOCK_EMPTY) {
            return new Point((int) x, (int) y);
        }

        return null;
    }

    /**
     * Checks if a block above specified coordinates exists
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return supplied coordinates if block exists, null if it doesn't
     */
    public Point blockExistsAbove(float x, float y) {
        if (x >= 0 && x < WIDTH && y + 1 < HEIGHT && y + 1 >= 0 && data[(int) x][(int) y + 1] != BLOCK_EMPTY) {
            return new Point((int) x, (int) y);
        }

        return null;
    }

    /**
     * Checks if a block to the left of the specified coordinates exists
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return supplied coordinates if block exists, null if it doesn't
     */
    public Point blockExistsLeft(float x, float y) {
        if (y >= 0 && y < HEIGHT && x - 1 >= 0 && x - 1 < WIDTH && data[(int) x - 1][(int) y] != BLOCK_EMPTY) {
            return new Point((int) x, (int) y);
        }

        return null;
    }

    /**
     * Checks if a block to the right of the specified coordinates exists
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return supplied coordinates if block exists, null if it doesn't
     */
    public Point blockExistsRight(float x, float y) {
        if (y >= 0 && y < HEIGHT && x + 1 >= 0 && x + 1 < WIDTH && data[(int) x + 1][(int) y] != BLOCK_EMPTY) {
            return new Point((int) x, (int) y);
        }

        return null;
    }

    /**
     * Checks if the block at the given coordinates is drillable or not
     *
     * @param x x coordinate of the block
     * @param y y coordinate of the block
     * @return block type if it is drillable, -1 if it is not
     */
    public byte isBlockDrillable(int x, int y) {
        byte block = data[x][y];

        if (block == BLOCK_EMPTY) {
            return -1;
        }

        return data[x][y];
    }

    /**
     * Removes a block from the map by making it empty
     *
     * @param x
     * @param y
     */
    public void removeBlock(int x, int y) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            data[x][y] = BLOCK_EMPTY;
        }
    }
}
