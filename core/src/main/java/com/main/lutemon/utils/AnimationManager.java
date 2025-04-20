package com.main.lutemon.utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationManager {
    private static AnimationManager instance;
    private final ObjectMap<String, Animation<TextureRegion>> animations;
    private float stateTime;

    private AnimationManager() {
        this.animations = new ObjectMap<>();
        this.stateTime = 0f;
        loadAnimations();
    }

    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }

    private void loadAnimations() {
        // 1) read the JSON manifest
        FileHandle jsonFile = Gdx.files.internal("lutemons/animations.json");
        JsonValue root = new JsonReader().parse(jsonFile);

        // 2) for each lutemon type ("white", "green", etc.)
        for (JsonValue typeNode : root) {
            String type = typeNode.name();

            // 3) for each animation under that type ("idle", "walk", etc.)
            for (JsonValue animNode : typeNode) {
                String animName     = animNode.name();
                String fileName     = animNode.getString("file");
                int    cols         = animNode.getInt("cols");
                int    rows         = animNode.getInt("rows", 1);
                float  frameDur     = animNode.getFloat("frameDuration", 0.15f);

                try {
                    // load sheet and split into regions
                    Texture sheet = new Texture(Gdx.files.internal("lutemons/idle/" + fileName));

                    int frameWidth  = sheet.getWidth()  / cols;
                    int frameHeight = sheet.getHeight() / rows;

                    TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);

                    // flatten in row-major order
                    TextureRegion[] frames = new TextureRegion[cols * rows];
                    int idx = 0;
                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < cols; c++) {
                            frames[idx++] = tmp[r][c];
                        }
                    }

                    // create and store animation
                    Animation<TextureRegion> anim = new Animation<>(frameDur, frames);
                    anim.setPlayMode(Animation.PlayMode.LOOP);
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

    public TextureRegion getCurrentFrame(String lutemonType, String animationType) {
        String key = lutemonType.toLowerCase() + "_" + animationType;
        Animation<TextureRegion> animation = animations.get(key);
        return animation != null
            ? animation.getKeyFrame(stateTime)
            : null;
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
