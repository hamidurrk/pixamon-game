package com.main.lutemon.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.lutemon.LutemonType;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.utils.Constants;
import com.main.lutemon.utils.StatisticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dialog to display statistics in chart format.
 */
public class StatisticsChartDialog extends Dialog {
    private final ShapeRenderer shapeRenderer;
    private final Map<Integer, StatisticsManager.LutemonPerformance> performanceMap;
    private final List<Lutemon> lutemons;
    private final String chartType;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;

    // Chart dimensions and positioning
    private float chartWidth;
    private float chartHeight;
    private float chartX;
    private float chartY;
    private float barWidth;
    private float barSpacing;
    private float maxBarHeight;
    private int maxValue;

    /**
     * Creates a new statistics chart dialog.
     *
     * @param skin The skin to use for the dialog
     * @param chartType The type of chart to display ("battles", "wins", "training", or "winrate")
     */
    public StatisticsChartDialog(Skin skin, String chartType) {
        super("", skin);
        this.chartType = chartType;

        // Set size to 90% of the screen
        float width = Gdx.graphics.getWidth() * 0.9f;
        float height = Gdx.graphics.getHeight() * 0.9f;
        setSize(width, height);
        setPosition((Gdx.graphics.getWidth() - width) / 2, (Gdx.graphics.getHeight() - height) / 2);

        // Initialize shape renderer for drawing charts
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        // Get font for labels
        font = skin.getFont("default-font");
        glyphLayout = new GlyphLayout();

        // Get lutemon performance data
        performanceMap = StatisticsManager.getInstance().getLutemonPerformanceStats();
        lutemons = new ArrayList<>(Storage.getInstance().getAllLutemons());

        // Calculate chart dimensions
        float padding = Constants.getPadding();
        chartWidth = width * 0.6f;
        chartHeight = height * 0.4f;
        chartX = padding * 8;
        chartY = height * 0.35f;

        // Calculate bar width and spacing based on number of lutemons
        int numLutemons = lutemons.size();
        barWidth = Math.min(40, chartWidth / (Math.max(numLutemons, 1) * 1.5f));
        barSpacing = barWidth * 0.5f;
        maxBarHeight = chartHeight * 0.8f;

        // Find the maximum value for scaling
        maxValue = findMaxValue();

        // Create UI
        createUI(skin);
    }

    private void createUI(Skin skin) {
        Table contentTable = new Table();
        contentTable.setFillParent(true);
        contentTable.top().pad(20);

        // Title based on chart type
        String title = getChartTitle();
        Label titleLabel = new Label(title, skin);
        titleLabel.setFontScale(1.1f);
        titleLabel.setAlignment(Align.left);
        contentTable.add(titleLabel).expandX().fillX().pad(20).left().row();

        // Add a spacer for the chart area
        contentTable.add().height(chartHeight + 100).row();

        // Legend - positioned in the top-right corner
        Table legendTable = new Table();
        legendTable.top().right().pad(50, 10, 10, 10);
        legendTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.7f)));

        // Add legend title
        Label legendTitle = new Label("Legend:", skin);
        legendTitle.setFontScale(0.9f); // Smaller font size
        legendTable.add(legendTitle).colspan(2).pad(5).left().row();

        // Add legend entries for all lutemons
        int numLegendEntries = lutemons.size();
        for (int i = 0; i < numLegendEntries; i++) {
            Lutemon lutemon = lutemons.get(i);

            // Color box
            Table colorBox = new Table();
            colorBox.setBackground(skin.newDrawable("white", getLutemonTypeColor(lutemon.getType())));
            legendTable.add(colorBox).size(15).pad(3);

            // Lutemon name
            Label nameLabel = new Label(lutemon.getName(), skin);
            nameLabel.setFontScale(0.8f);
            legendTable.add(nameLabel).pad(3).left().row();
        }

        // Position the legend in the top-right corner
        Table legendContainer = new Table();
        legendContainer.setFillParent(true);
        legendContainer.top().right().pad(60);
        legendContainer.add(legendTable);
        addActor(legendContainer);

        // Close button at the bottom
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        contentTable.add(closeButton).pad(20).width(300).height(60);

        add(contentTable).expandX().fill();
    }

    private String getChartTitle() {
        switch (chartType) {
            case "battles":
                return "Battles by Lutemon";
            case "wins":
                return "Wins by Lutemon";
            case "training":
                return "Training Sessions by Lutemon";
            case "winrate":
                return "Win Rate by Lutemon (%)";
            default:
                return "Lutemon Statistics";
        }
    }

    private int findMaxValue() {
        int max = 0;
        int count = lutemons.size();

        for (int i = 0; i < count; i++) {
            Lutemon lutemon = lutemons.get(i);
            StatisticsManager.LutemonPerformance performance = performanceMap.get(lutemon.getId());
            if (performance == null) continue;

            int value = getValueForLutemon(lutemon, performance);
            if (value > max) {
                max = value;
            }
        }
        return Math.max(max, 1); // Ensure we don't divide by zero
    }

    private int getValueForLutemon(Lutemon lutemon, StatisticsManager.LutemonPerformance performance) {
        switch (chartType) {
            case "battles":
                return performance.getBattles();
            case "wins":
                return performance.getWins();
            case "training":
                return performance.getTrainingDays();
            case "winrate":
                // For win rate, percentage (0-100)
                return performance.getBattles() > 0 ?
                    (int) ((float) performance.getWins() / performance.getBattles() * 100) : 0;
            default:
                return 0;
        }
    }

    private Color getLutemonTypeColor(LutemonType type) {
        switch (type) {
            case WHITE:
                return new Color(0.9f, 0.9f, 0.9f, 1);
            case GREEN:
                return new Color(0.2f, 0.8f, 0.2f, 1);
            case PINK:
                return new Color(0.9f, 0.5f, 0.8f, 1);
            case ORANGE:
                return new Color(1.0f, 0.6f, 0.2f, 1);
            case BLACK:
                return new Color(0.0f, 0.0f, 0.0f, 1);
            default:
                return Color.WHITE;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Draw the dialog background and UI
        super.draw(batch, parentAlpha);

        // End the batch to use ShapeRenderer
        batch.end();

        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Set the projection matrix to match the stage's camera
        shapeRenderer.setProjectionMatrix(getStage().getCamera().combined);

        // Draw chart background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.7f));
        shapeRenderer.rect(chartX - 10, chartY - 10, chartWidth + 20, chartHeight + 20);
        shapeRenderer.end();

        // Draw chart axes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        // X-axis
        shapeRenderer.line(chartX, chartY, chartX + chartWidth, chartY);

        // Y-axis
        shapeRenderer.line(chartX, chartY, chartX, chartY + chartHeight);


        shapeRenderer.end();

        // Start batch for drawing text
        batch.begin();

        // Draw Y-axis title
        String yAxisTitle = getYAxisTitle();
        float oldScale = font.getScaleX();
        font.getData().setScale(1.5f);
        glyphLayout.setText(font, yAxisTitle);
        font.draw(batch, yAxisTitle,
                  chartX - 10,
                  chartY + chartHeight + 40);
        font.getData().setScale(oldScale);

        // Draw X-axis title
        String xAxisTitle = "Lutemons";
        font.getData().setScale(1.5f);
        glyphLayout.setText(font, xAxisTitle);
        font.draw(batch, xAxisTitle,
                  chartX + 10,
                  chartY - 20);
        font.getData().setScale(oldScale);

        // End batch to draw bars
        batch.end();

        // Draw bars
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int count = lutemons.size();
        for (int i = 0; i < count; i++) {
            Lutemon lutemon = lutemons.get(i);
            StatisticsManager.LutemonPerformance performance = performanceMap.get(lutemon.getId());
            if (performance == null) continue;

            int value = getValueForLutemon(lutemon, performance);
            float barHeight = (float) value / maxValue * maxBarHeight;

            // Calculate bar position - start from the left
            float totalBarWidth = barWidth + barSpacing;
            float x = chartX + (i * totalBarWidth);
            float y = chartY;

            // Set color based on lutemon type
            shapeRenderer.setColor(getLutemonTypeColor(lutemon.getType()));

            // Draw the bar
            shapeRenderer.rect(x, y, barWidth, barHeight);
        }

        shapeRenderer.end();

        // Restart batch for drawing bar labels
        batch.begin();

        // Draw bar labels (lutemon names and values)
        count = lutemons.size();
        for (int i = 0; i < count; i++) {
            Lutemon lutemon = lutemons.get(i);
            StatisticsManager.LutemonPerformance performance = performanceMap.get(lutemon.getId());
            if (performance == null) continue;

            int value = getValueForLutemon(lutemon, performance);
            float barHeight = (float) value / maxValue * maxBarHeight;

            // Calculate bar position - start from the left
            float totalBarWidth = barWidth + barSpacing;
            float x = chartX + (i * totalBarWidth);
            float y = chartY;

            // Draw value on top of bar
            String valueText = String.valueOf(value);
            if (chartType.equals("winrate")) {
                valueText = value + "%";
            }

            glyphLayout.setText(font, valueText);
            float textWidth = glyphLayout.width;
            float textX = x + (barWidth / 2) - (textWidth / 2);
            float textY = y + barHeight + glyphLayout.height + 5;

            font.draw(batch, valueText, textX, textY);
        }

        // Disable blending
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private String getYAxisTitle() {
        switch (chartType) {
            case "battles":
                return "Number of Battles";
            case "wins":
                return "Number of Wins";
            case "training":
                return "Number of Training Sessions";
            case "winrate":
                return "Win Rate (%)";
            default:
                return "Value";
        }
    }

    /**
     * Shows the dialog on the given stage.
     *
     * @param stage The stage to show the dialog on
     * @return This dialog for chaining
     */
    @Override
    public Dialog show(Stage stage) {
        stage.addActor(this);
        return this;
    }

    @Override
    public void hide() {
        super.hide();
        remove();
    }

    @Override
    public boolean remove() {
        boolean result = super.remove();
        if (result) {
            // Dispose of resources
            shapeRenderer.dispose();
        }
        return result;
    }
}
