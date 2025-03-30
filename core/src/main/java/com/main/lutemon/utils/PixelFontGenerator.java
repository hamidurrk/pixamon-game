package com.main.lutemon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class PixelFontGenerator {
    private static final String TAG = "PixelFontGenerator";

    public static BitmapFont generatePixelFont(int size) {
        try {
            // Create a pixel-perfect font using FreeType
            Gdx.app.log(TAG, "Loading font file...");
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel.ttf"));
            
            Gdx.app.log(TAG, "Setting up font parameters...");
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = size;
            parameter.mono = true; // Use monospace for pixel-perfect rendering
            parameter.minFilter = Texture.TextureFilter.Nearest;
            parameter.magFilter = Texture.TextureFilter.Nearest;
            
            Gdx.app.log(TAG, "Generating font...");
            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();
            
            Gdx.app.log(TAG, "Font generation successful");
            return font;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error generating font: " + e.getMessage());
            if (e.getCause() != null) {
                Gdx.app.error(TAG, "Caused by: " + e.getCause().getMessage());
            }
            // Return a default font as fallback
            return new BitmapFont();
        }
    }
} 