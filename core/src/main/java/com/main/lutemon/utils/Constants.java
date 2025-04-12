package com.main.lutemon.utils;

import com.badlogic.gdx.Gdx;

public class Constants {
    // Default screen dimensions (used as fallback)
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 480;

    // UI constants - will be scaled based on screen size
    private static final float BUTTON_WIDTH_PERCENT = 0.4f; // 40% of screen width
    private static final float BUTTON_HEIGHT_PERCENT = 0.2f; // 20% of screen height
    private static final float PADDING_PERCENT = 0.02f; // 2% of screen width
    private static final float MARGIN_PERCENT = 0.04f; // 4% of screen width

    // Font sizes
    private static final float TITLE_FONT_SIZE = 0.05f; // 5% of screen height
    private static final float BUTTON_FONT_SIZE = 0.04f; // 4% of screen height
    private static final float LABEL_FONT_SIZE = 0.035f; // 3.5% of screen height

    // Lutemon constants
    public static final int MAX_LEVEL = 100;
    public static final int BASE_STATS = 10;
    public static final float STAT_GROWTH = 1.5f;
    public static final int MAX_EXPERIENCE = 1000;

    // Battle constants
    public static final float BATTLE_ANIMATION_DURATION = 0.5f;
    public static final float ATTACK_MULTIPLIER = 1.2f;
    public static final float DEFENSE_MULTIPLIER = 0.8f;
    public static final float SPECIAL_MULTIPLIER = 1.5f;

    // Training constants
    public static final int TRAINING_EXPERIENCE = 10;
    public static final float TRAINING_DURATION = 1.0f;

    // Asset paths
    public static final String SKIN_PATH = "skins/uiskin.json";
    public static final String FONT_PATH = "fonts/default.fnt";
    public static final String BACKGROUND_PATH = "backgrounds/%s.png";
    public static final String LUTEMON_ATLAS_PATH = "lutemons/%s.atlas";
    public static final String UI_ELEMENT_PATH = "ui/%s.png";

    // Save/Load
    public static final String SAVE_FILE = "lutemon_save.json";

    /**
     * Get the current screen width
     * @return The width of the current screen
     */
    public static int getScreenWidth() {
        return Gdx.graphics != null ? Gdx.graphics.getWidth() : DEFAULT_WIDTH;
    }

    /**
     * Get the current screen height
     * @return The height of the current screen
     */
    public static int getScreenHeight() {
        return Gdx.graphics != null ? Gdx.graphics.getHeight() : DEFAULT_HEIGHT;
    }

    /**
     * Get the button width scaled to the current screen size
     * @return The scaled button width
     */
    public static int getButtonWidth() {
        return (int)(getScreenWidth() * BUTTON_WIDTH_PERCENT);
    }

    /**
     * Get the button height scaled to the current screen size
     * @return The scaled button height
     */
    public static int getButtonHeight() {
        return (int)(getScreenHeight() * BUTTON_HEIGHT_PERCENT);
    }

    /**
     * Get the padding scaled to the current screen size
     * @return The scaled padding value
     */
    public static float getPadding() {
        return getScreenWidth() * PADDING_PERCENT;
    }

    /**
     * Get the margin scaled to the current screen size
     * @return The scaled margin value
     */
    public static float getMargin() {
        return getScreenWidth() * MARGIN_PERCENT;
    }

    /**
     * Get the title font size scaled to the current screen size
     * @return The scaled title font size
     */
    public static int getTitleFontSize() {
        return (int)(getScreenHeight() * TITLE_FONT_SIZE);
    }

    /**
     * Get the button font size scaled to the current screen size
     * @return The scaled button font size
     */
    public static int getButtonFontSize() {
        return (int)(getScreenHeight() * BUTTON_FONT_SIZE);
    }

    /**
     * Get the label font size scaled to the current screen size
     * @return The scaled label font size
     */
    public static int getLabelFontSize() {
        return (int)(getScreenHeight() * LABEL_FONT_SIZE);
    }

    // Font paths
    public static final String PRESS_START_2P_FONT = "fonts/press-start-2p.ttf";
}
