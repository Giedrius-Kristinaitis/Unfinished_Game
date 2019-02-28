package com.gasis.digger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gasis.digger.resources.Resources;
import com.gasis.digger.ui.abstractions.BasicScreen;
import com.gasis.digger.ui.abstractions.ScreenSwitcher;
import com.gasis.digger.ui.abstractions.ScreenWithInput;
import com.gasis.digger.ui.implementations.GameScreen;
import com.gasis.digger.ui.implementations.LoadingScreen;
import com.gasis.digger.utils.Constants;

import java.util.Map;
import java.util.TreeMap;

/**
 * Main class of the game
 */
public class Main extends Game implements ScreenSwitcher {

	// current visible ui
	private BasicScreen currentScreen;

	// game's resources
	private Resources resources;

	// viewport used by all screens
	private Viewport port = new FillViewport(Constants.WIDTH, Constants.HEIGHT);

	/**
	 * Performs initialization. Called automatically by libGDX
	 */
	@Override
	public void create () {
		resources = new Resources();

		// initialize stuff for the loading screen
		GameScreen gameScreen = new GameScreen();

		String loadingImage = "textures.png";

		Map<String, Class> assets = new TreeMap<String, Class>();
		assets.put("textures.atlas", TextureAtlas.class);

		// show the loading screen
		showScreen(new LoadingScreen(gameScreen, loadingImage,
					true, assets, null));
	}

	/**
	 * Game loop render method. Called automatically by libGDX
	 */
	@Override
	public void render () {
		super.render();
	}

	/**
	 * Disposes of all heavy resources. Called automatically by libGDX
	 */
	@Override
	public void dispose () {
		if (currentScreen != null) {
			currentScreen.dispose();
		}

		resources.dispose();
	}

	/**
	 * Sets the current screen to the specified one
	 * @param screen screen to be shown
	 */
	@Override
	public void showScreen(BasicScreen screen) {
		if (currentScreen != null) {
			currentScreen.dispose();
		}

		this.currentScreen = screen;
		this.currentScreen.setViewport(port);
		this.currentScreen.initialize();
		this.currentScreen.setResources(resources);
		this.currentScreen.setScreenSwitcher(this);

		this.setScreen(currentScreen);

		// if the screen has input handling, register it
		if (currentScreen instanceof ScreenWithInput) {
			Gdx.input.setInputProcessor(((ScreenWithInput) currentScreen).getInputProcessor());
		}
	}
}
