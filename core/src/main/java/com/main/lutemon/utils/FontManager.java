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
            // Use the default font as a fallback
            titleFont = new BitmapFont();
            buttonFont = new BitmapFont();
            labelFont = new BitmapFont();

            // Try to load the Press Start 2P font if available
            FileHandle fontFile = Gdx.files.internal(Constants.PRESS_START_2P_FONT);
            if (fontFile.exists()) {
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

                // Generate title font
                FreeTypeFontParameter titleParams = new FreeTypeFontParameter();
                titleParams.size = Constants.getTitleFontSize();
                titleParams.color = Color.WHITE;
                titleFont = generator.generateFont(titleParams);

                // Generate button font
                FreeTypeFontParameter buttonParams = new FreeTypeFontParameter();
                buttonParams.size = Constants.getButtonFontSize();
                buttonParams.color = Color.WHITE;
                buttonFont = generator.generateFont(buttonParams);

                // Generate label font
                FreeTypeFontParameter labelParams = new FreeTypeFontParameter();
                labelParams.size = Constants.getLabelFontSize();
                labelParams.color = Color.WHITE;
                labelFont = generator.generateFont(labelParams);

                generator.dispose();
            } else {
                Gdx.app.log("FontManager", "Press Start 2P font not found, using default font");

                // Scale the default font to be larger
                titleFont.getData().setScale(2.0f);
                buttonFont.getData().setScale(1.5f);
                labelFont.getData().setScale(1.2f);
            }

            fontsLoaded = true;
        } catch (Exception e) {
            Gdx.app.error("FontManager", "Error loading fonts: " + e.getMessage());

            // Ensure we have some fonts even if loading fails
            if (titleFont == null) titleFont = new BitmapFont();
            if (buttonFont == null) buttonFont = new BitmapFont();
            if (labelFont == null) labelFont = new BitmapFont();
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
            skin.add("title", titleStyle, LabelStyle.class);

            LabelStyle defaultLabelStyle = skin.get("default", LabelStyle.class);
            defaultLabelStyle.font = labelFont;

            // Update button styles
            TextButtonStyle defaultButtonStyle = skin.get("default", TextButtonStyle.class);
            defaultButtonStyle.font = buttonFont;

        } catch (Exception e) {
            Gdx.app.error("FontManager", "Error updating skin: " + e.getMessage());
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
