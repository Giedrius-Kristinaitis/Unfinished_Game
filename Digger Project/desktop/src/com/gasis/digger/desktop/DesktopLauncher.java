package com.gasis.digger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gasis.digger.Main;

/**
 * Launches the desktop version of the game
 */
public class DesktopLauncher {

	/**
	 * Entry point of the program
	 *
	 * @param args arguments for the program
	 */
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.x = 512;
		config.y = 10;
		config.width = 720;
		config.height = 936;
		config.title = "Digger";

		new LwjglApplication(new Main(), config);
	}
}
