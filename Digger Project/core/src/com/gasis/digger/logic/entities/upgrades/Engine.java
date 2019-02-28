package com.gasis.digger.logic.entities.upgrades;

/**
 * Contains engine upgrades
 */
public enum Engine {

    // all engines
    STOCK("", 0, 1000,true); // stock is already available so the price is not important

    // name of the texture in the texture atlas
    private String textureName;

    // price of the engine
    private int price;

    // horse-power
    private int hp;

    // does the engine allow drilling upwards???
    private boolean drillingUpwardsAllowed;

    /**
     * Default constructor
     *
     * @param textureName
     * @param price
     * @param hp
     * @param drillingUpwardsAllowed
     */
    Engine(String textureName, int price, int hp, boolean drillingUpwardsAllowed) {
        this.textureName = textureName;
        this.price = price;
        this.hp = hp;
        this.drillingUpwardsAllowed = drillingUpwardsAllowed;
    }

    /**
     * Checks if the engine can drill upwards
     * @return is drilling upwards allowed
     */
    public boolean isDrillingUpwardsAllowed() {
        return drillingUpwardsAllowed;
    }

    /**
     * Gets the price of the engine
     * @return price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Gets the horse-power of the engine
     * @return hp
     */
    public int getHp() {
        return hp;
    }

    /**
     * Gets the name of the texture in the texture atlas
     * @return texture name
     */
    public String getTextureName() {
        return textureName;
    }
}
