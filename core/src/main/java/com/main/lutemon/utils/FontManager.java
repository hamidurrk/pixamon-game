package com.main.lutemon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Texture;

/**
 * Manages font generation and loading for the game
 */
public class FontManager {
    private static FontManager instance;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private BitmapFont labelFont;
    private boolean fontsLoaded = false;

    private FontManager() {
        // Private constructor for singleton
    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    /**
     * Initializes fonts using the Press Start 2P font
     */
    public void initializeFonts() {
        if (fontsLoaded) return;

        try {
            FileHandle fontFile = Gdx.files.internal(Constants.PRESS_START_2P_FONT);
            if (fontFile.exists()) {
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
                FreeTypeFontParameter parameter = new FreeTypeFontParameter();

                // Generate title font
                parameter.size = Constants.getTitleFontSize();
                parameter.color = Color.WHITE;
                parameter.borderWidth = 3f; // Add black outline
                parameter.borderColor = Color.BLACK;
                parameter.minFilter = Texture.TextureFilter.Linear;
                parameter.magFilter = Texture.TextureFilter.Linear;
                titleFont = generator.generateFont(parameter);

                // Generate button font
                parameter.size = Constants.getButtonFontSize();
                buttonFont = generator.generateFont(parameter);

                // Generate label font
                parameter.size = Constants.getLabelFontSize();
                labelFont = generator.generateFont(parameter);

                generator.dispose();
            } else {
                Gdx.app.error("FontManager", "Font file not found: " + Constants.PRESS_START_2P_FONT);
                // Fallback to default bitmap font with scaling
                titleFont = new BitmapFont();
                titleFont.getData().setScale(2.0f);
                buttonFont = new BitmapFont();
                buttonFont.getData().setScale(1.5f);
                labelFont = new BitmapFont();
                labelFont.getData().setScale(1.0f);
            }

            fontsLoaded = true;
        } catch (Exception e) {
            Gdx.app.error("FontManager", "Error loading fonts", e);
            titleFont = new BitmapFont();
            buttonFont = new BitmapFont();
            labelFont = new BitmapFont();
        }
    }

    /**
     * Updates the skin with custom fonts
     * @param skin The skin to update
     */
    public void updateSkin(Skin skin) {
        if (!fontsLoaded) {
            initializeFonts();
        }

        try {
            // Update label styles
            LabelStyle titleStyle = new LabelStyle(titleFont, Color.WHITE);
            Gdx.app.log("FontManager", "Creating title style with font size: " + titleFont.getData().scaleX);
            skin.add("title", titleStyle, LabelStyle.class);

            LabelStyle defaultLabelStyle = skin.get("default", LabelStyle.class);
            defaultLabelStyle.font = labelFont;

            // Update button styles
            TextButtonStyle defaultButtonStyle = skin.get("default", TextButtonStyle.class);
            defaultButtonStyle.font = buttonFont;

        } catch (Exception e) {
            Gdx.app.error("FontManager", "Error updating skin", e);
        }
    }

    /**
     * Disposes of all fonts
     */
    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (labelFont != null) labelFont.dispose();
        fontsLoaded = false;
    }

    public BitmapFont getTitleFont() {
        if (!fontsLoaded) initializeFonts();
        return titleFont;
    }

    public BitmapFont getButtonFont() {
        if (!fontsLoaded) initializeFonts();
        return buttonFont;
    }

    public BitmapFont getLabelFont() {
        if (!fontsLoaded) initializeFonts();
        return labelFont;
    }
}
