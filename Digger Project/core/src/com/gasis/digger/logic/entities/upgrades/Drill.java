package com.gasis.digger.logic.entities.upgrades;

/**
 * Contains drill upgrades
 */
public enum Drill {

    // all drills
    STOCK("drill_1", 1, 0); // stock is already available, price is not important

    // texture name in the texture atlas
    private String textureName;

    // sharpness which affects the drilling speed
    private float sharpness;

    // how much does it cost???
    private int price;

    /**
     * Default constructor
     *
     * @param textureName
     * @param sharpness
     * @param price
     */
    Drill(String textureName, float sharpness, int price) {
        this.textureName = textureName;
        this.sharpness = sharpness;
        this.price = price;
    }

    /**
     * Gets the price of the drill
     * @return price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Gets the sharpness of the drill
     * @return sharpness
     */
    public float getSharpness() {
        return sharpness;
    }

    /**
     * Gets the name of the drill's texture in the texture atlas
     * @return name of the texture
     */
    public String getTextureName() {
        return textureName;
    }
}
