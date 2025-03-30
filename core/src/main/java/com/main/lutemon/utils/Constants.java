package com.main.lutemon.utils;

public class Constants {
    // Screen dimensions
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 480;

    // UI constants
    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 50;
    public static final float PADDING = 10f;
    public static final float MARGIN = 20f;

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
} 