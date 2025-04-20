package com.main.lutemon.utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.HashMap;

/**
 * Manages animations for Lutemons in the game.
 * Handles loading, updating, and retrieving animation frames.
 */
public class AnimationManager {
    private static AnimationManager instance;
    private final ObjectMap<String, Animation<TextureRegion>> animations;
    private final HashMap<String, Animation.PlayMode> defaultPlayModes;
    private float stateTime;

    /**
     * List of Lutemon types that need to be flipped horizontally (originally facing left)
     */
    private final String[] typesToFlip = {"white", "orange", "black"};

    private AnimationManager() {
        this.animations = new ObjectMap<>();
        this.defaultPlayModes = new HashMap<>();
        this.stateTime = 0f;

        // Set up default play modes for different animation types
        setupDefaultPlayModes();

        // Load animations from JSON
        loadAnimations();
    }

    /**
     * Sets up the default play modes for different animation types.
     */
    private void setupDefaultPlayModes() {
        // Looping animations
        defaultPlayModes.put("idle", Animation.PlayMode.LOOP);
        defaultPlayModes.put("run", Animation.PlayMode.LOOP);

        // Non-looping animations - using NORMAL ensures they play once and stop
        defaultPlayModes.put("attack", Animation.PlayMode.NORMAL);
        defaultPlayModes.put("hurt", Animation.PlayMode.NORMAL);
        defaultPlayModes.put("die", Animation.PlayMode.NORMAL);
    }

    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }

    /**
     * Checks if a Lutemon type needs to be flipped horizontally
     *
     * @param typeName The Lutemon type name
     * @return True if the type needs to be flipped, false otherwise
     */
    private boolean shouldFlipType(String typeName) {
        for (String type : typesToFlip) {
            if (typeName.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    private void loadAnimations() {
        // 1) read the JSON manifest
        FileHandle jsonFile = Gdx.files.internal("lutemons/animations.json");
        JsonValue root = new JsonReader().parse(jsonFile);

        // 2) for each lutemon type ("white", "green", etc.)
        for (JsonValue typeNode : root) {
            String type = typeNode.name();
            boolean shouldFlip = shouldFlipType(type);

            // 3) for each animation under that type ("idle", "walk", etc.)
            for (JsonValue animNode : typeNode) {
                String animName     = animNode.name();
                String fileName     = animNode.getString("file");
                int    cols         = animNode.getInt("cols");
                int    rows         = animNode.getInt("rows", 1);
                float  frameDur     = animNode.getFloat("frameDuration", 0.50f);

                // Adjust frame durations for specific animation types to make gameplay smoother
                if (animName.equals("attack")) {
                    frameDur = 0.1f; // Faster attack animations
                } else if (animName.equals("hurt")) {
                    frameDur = 0.08f; // Faster hurt animations
                } else if (animName.equals("run")) {
                    frameDur = 0.12f; // Faster run animations
                } else if (animName.equals("idle")) {
                    frameDur = 0.4f; // Slower idle animations for contrast
                } else if (animName.equals("die")) {
                    frameDur = 0.2f; // Slower die animations for more impact
                }

                try {
                    // load sheet and split into regions
                    Texture sheet = new Texture(Gdx.files.internal("lutemons/sprites/" + fileName));

                    int frameWidth  = sheet.getWidth()  / cols;
                    int frameHeight = sheet.getHeight() / rows;

                    TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);

                    // flatten in row-major order and flip if needed
                    TextureRegion[] frames = new TextureRegion[cols * rows];
                    int idx = 0;
                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < cols; c++) {
                            frames[idx] = tmp[r][c];

                            // Flip the texture horizontally if this type needs flipping
                            if (shouldFlip) {
                                frames[idx].flip(true, false);
                            }

                            idx++;
                        }
                    }

                    // create and store animation
                    Animation<TextureRegion> anim = new Animation<>(frameDur, frames);

                    // Set play mode based on animation type
                    Animation.PlayMode playMode = defaultPlayModes.getOrDefault(animName, Animation.PlayMode.LOOP);
                    anim.setPlayMode(playMode);

                    animations.put(type + "_" + animName, anim);

                } catch (Exception e) {
                    Gdx.app.error("AnimationManager",
                        "Failed to load “" + fileName + "”: " + e.getMessage());
                }
            }
        }
    }

    public void update(float delta) {
        stateTime += delta;
    }

    /**
     * Gets the current frame of an animation for a specific Lutemon type and animation type.
     *
     * @param lutemonType The type of Lutemon (e.g., "white", "green")
     * @param animationType The type of animation (e.g., "idle", "run", "attack")
     * @return The current frame of the animation, or null if not found
     */
    public TextureRegion getCurrentFrame(String lutemonType, String animationType) {
        String key = lutemonType.toLowerCase() + "_" + animationType;
        Animation<TextureRegion> animation = animations.get(key);

        // If the requested animation doesn't exist, try to fall back to idle
        if (animation == null && !animationType.equals("idle")) {
            key = lutemonType.toLowerCase() + "_idle";
            animation = animations.get(key);
        }

        return animation != null
            ? animation.getKeyFrame(stateTime)
            : null;
    }

    /**
     * Gets the current frame of an animation for a specific Lutemon type and animation type,
     * with a specific state time.
     *
     * @param lutemonType The type of Lutemon (e.g., "white", "green")
     * @param animationType The type of animation (e.g., "idle", "run", "attack")
     * @param stateTime The state time to use for the animation
     * @return The current frame of the animation, or null if not found
     */
    public TextureRegion getFrame(String lutemonType, String animationType, float stateTime) {
        String key = lutemonType.toLowerCase() + "_" + animationType;
        Animation<TextureRegion> animation = animations.get(key);

        // If the requested animation doesn't exist, try to fall back to idle
        if (animation == null && !animationType.equals("idle")) {
            key = lutemonType.toLowerCase() + "_idle";
            animation = animations.get(key);
        }

        return animation != null
            ? animation.getKeyFrame(stateTime)
            : null;
    }

    /**
     * Checks if an animation has finished playing using the global state time.
     *
     * @param lutemonType The type of Lutemon
     * @param animationType The type of animation
     * @return True if the animation has finished, false otherwise
     */
    public boolean isAnimationFinished(String lutemonType, String animationType) {
        String key = lutemonType.toLowerCase() + "_" + animationType;
        Animation<TextureRegion> animation = animations.get(key);
        return animation != null && animation.isAnimationFinished(stateTime);
    }

    /**
     * Checks if an animation has finished playing using a specific state time.
     *
     * @param lutemonType The type of Lutemon
     * @param animationType The type of animation
     * @param specificStateTime The specific state time to check against
     * @return True if the animation has finished, false otherwise
     */
    public boolean isAnimationFinished(String lutemonType, String animationType, float specificStateTime) {
        String key = lutemonType.toLowerCase() + "_" + animationType;
        Animation<TextureRegion> animation = animations.get(key);
        return animation != null && animation.isAnimationFinished(specificStateTime);
    }

    /**
     * Gets the duration of an animation in seconds.
     *
     * @param lutemonType The type of Lutemon
     * @param animationType The type of animation
     * @return The duration of the animation in seconds, or 0 if not found
     */
    public float getAnimationDuration(String lutemonType, String animationType) {
        String key = lutemonType.toLowerCase() + "_" + animationType;
        Animation<TextureRegion> animation = animations.get(key);
        if (animation == null) return 0;

        // Calculate total duration based on frame count and frame duration
        return animation.getKeyFrames().length * animation.getFrameDuration();
    }

    public void dispose() {
        for (Animation<TextureRegion> anim : animations.values()) {
            if (anim.getKeyFrame(0) != null) {
                anim.getKeyFrame(0).getTexture().dispose();
            }
        }
        animations.clear();
    }
}
