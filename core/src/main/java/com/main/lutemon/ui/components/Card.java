package com.main.lutemon.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public abstract class Card extends Table {
    protected final float width;
    protected final float height;
    protected final Skin skin;

    public Card(float width, Skin skin) {
        this.width = width;
        this.height = width * 1.5f; // 3:2 aspect ratio
        this.skin = skin;

        setSize(width, height);
        setBackground(createCardBackground());
        defaults().expand().fill();
        pad(width * 0.05f);

        createLayout();
    }

    private void createLayout() {
        // Title Section (20% of height)
        Table titleSection = createTitleSection();
        add(titleSection).height(height * 0.2f).fillX().row();

        // Image Section (40% of height)
        Table imageSection = createImageSection();
        add(imageSection).height(height * 0.4f).fillX().row();

        // Stats Section (40% of height)
        Table statsSection = createStatsSection();
        add(statsSection).height(height * 0.4f).fillX();

        validate();
    }

    protected abstract Table createTitleSection();
    protected abstract Table createStatsSection();

    protected Table createImageSection() {
        Table imageTable = new Table();

        // Create placeholder image with darker gray
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.3f, 0.3f, 0.3f, 1);
        pixmap.fill();
        TextureRegionDrawable placeholder = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        Image image = new Image(placeholder);
        imageTable.add(image).expand().fill();

        return imageTable;
    }

    private TextureRegionDrawable createCardBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.9f));
        pixmap.fill();
        TextureRegionDrawable background = new TextureRegionDrawable(
            new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        return background;
    }
}
