package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.main.lutemon.LutemonGame;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.ui.dialogs.StatisticsChartDialog;
import com.main.lutemon.utils.Constants;
import com.main.lutemon.utils.StatisticsManager;

import java.util.Map;

/**
 * Screen for displaying game statistics.
 */
public class StatisticsScreen implements Screen {
    private final LutemonGame game;
    private Stage stage;
    private final OrthographicCamera camera;
    private TextureRegion backgroundTexture;

    public StatisticsScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.getScreenWidth(), Constants.getScreenHeight());
        initialize();
    }

    private void initialize() {
        try {
            stage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));
            Gdx.input.setInputProcessor(stage);
            backgroundTexture = game.getAssetLoader().getBackground("home");
            createUI();
        } catch (Exception e) {
            Gdx.app.error("StatisticsScreen", "Error initializing: " + e.getMessage());
            throw e;
        }
    }

    private void createUI() {
        // Main container table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        float padding = Constants.getPadding();
        float buttonWidth = Constants.getButtonWidth();
        float buttonHeight = Constants.getScreenHeight() * Constants.BUTTON_HEIGHT_PERCENT;

        // Top bar with back button and chart button
        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();

        // Back button in upper left corner
        TextButton backButton = new TextButton("Back", game.getAssetLoader().getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.navigateToHome();
            }
        });

        // Chart button in upper right corner
        TextButton chartButton = new TextButton("View Charts", game.getAssetLoader().getSkin());
        chartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showChartSelectionDialog();
            }
        });

        // Add buttons to top table
        topTable.add(backButton).size(buttonWidth * 0.25f, buttonHeight).pad(padding).left();
        topTable.add().expandX();
        topTable.add(chartButton).size(buttonWidth * 0.6f, buttonHeight).pad(padding).right();

        // Title
        Label titleLabel = new Label("Game Statistics", game.getAssetLoader().getSkin(), "title");
        mainTable.add(titleLabel).pad(padding * 2).expandX().center().row();

        // Create a scroll pane for the statistics
        Table statsTable = createStatsTable();
        ScrollPane scrollPane = new ScrollPane(statsTable, game.getAssetLoader().getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false); // Allow horizontal scrolling if needed

        mainTable.add(scrollPane).expand().fill().pad(padding).row();

        // Add tables to stage
        stage.addActor(topTable);
        stage.addActor(mainTable);
    }

    private Table createStatsTable() {
        Table statsTable = new Table();
        statsTable.top();
        float padding = Constants.getPadding();
        Skin skin = game.getAssetLoader().getSkin();

        // Get statistics
        StatisticsManager statsManager = StatisticsManager.getInstance();
        int totalLutemons = statsManager.getTotalLutemonsCreated();
        int totalBattles = statsManager.getTotalBattles();
        int totalTraining = statsManager.getTotalTrainingSessions();
        Map<Integer, StatisticsManager.LutemonPerformance> performanceMap = statsManager.getLutemonPerformanceStats();

        // General Statistics Section
        Label generalStatsLabel = new Label("General Statistics", skin, "title");
        statsTable.add(generalStatsLabel).pad(padding).colspan(4).row();

        // Create a table for general stats
        Table generalTable = new Table();
        generalTable.add(new Label("Total Lutemons Created:", skin)).pad(padding).left();
        generalTable.add(new Label(String.valueOf(totalLutemons), skin)).pad(padding).left().row();

        generalTable.add(new Label("Total Battles:", skin)).pad(padding).left();
        generalTable.add(new Label(String.valueOf(totalBattles), skin)).pad(padding).left().row();

        generalTable.add(new Label("Total Training Sessions:", skin)).pad(padding).left();
        generalTable.add(new Label(String.valueOf(totalTraining), skin)).pad(padding).left().row();

        statsTable.add(generalTable).pad(padding).colspan(4).row();

        // Lutemon Performance Section
        Label lutemonStatsLabel = new Label("Lutemon Performance", skin, "title");
        statsTable.add(lutemonStatsLabel).pad(padding).colspan(4).row();

        // Headers for Lutemon stats
        Table headerRow = new Table();
        headerRow.add(new Label("Name", skin)).width(600).pad(padding).left();
        headerRow.add(new Label("Battles", skin)).width(300).pad(padding).center();
        headerRow.add(new Label("Wins", skin)).width(200).pad(padding).center();
        headerRow.add(new Label("Training", skin)).width(200).pad(padding).center();
        statsTable.add(headerRow).expandX().fillX().row();

        // Add a row for each Lutemon
        for (Lutemon lutemon : Storage.getInstance().getAllLutemons()) {
            Table lutemonRow = new Table();

            StatisticsManager.LutemonPerformance performance = performanceMap.get(lutemon.getId());
            if (performance == null) {
                continue;
            }

            lutemonRow.add(new Label(lutemon.getName(), skin)).width(600).pad(padding).left();
            lutemonRow.add(new Label(String.valueOf(performance.getBattles()), skin)).width(300).pad(padding).center();
            lutemonRow.add(new Label(String.valueOf(performance.getWins()), skin)).width(200).pad(padding).center();
            lutemonRow.add(new Label(String.valueOf(performance.getTrainingDays()), skin)).width(200).pad(padding).center();

            statsTable.add(lutemonRow).expandX().fillX().row();
        }

        return statsTable;
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Draw background
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        game.getBatch().draw(backgroundTexture, 0, 0, Constants.getScreenWidth(), Constants.getScreenHeight());
        game.getBatch().end();

        // Draw UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update camera and viewport
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);

        // Recreate UI to adjust to new screen size
        stage.clear();
        createUI();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Refresh the UI to show updated statistics
        stage.clear();
        createUI();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Shows a dialog to select which chart to display.
     */
    private void showChartSelectionDialog() {
        // Create a dialog for chart selection
        Dialog dialog = new Dialog("Select Chart Type", game.getAssetLoader().getSkin());

        // Set size and position
        float dialogWidth = Gdx.graphics.getWidth() * 0.8f;
        float dialogHeight = Gdx.graphics.getHeight() * 0.8f;
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setPosition(
            (Gdx.graphics.getWidth() - dialogWidth) / 2,
            (Gdx.graphics.getHeight() - dialogHeight) / 2
        );

        // Create content table
        Table contentTable = new Table();
        contentTable.pad(20);

        // Add title
        Label titleLabel = new Label("Select Chart Type", game.getAssetLoader().getSkin());
        titleLabel.setFontScale(1.5f); // Make it larger instead of using title style
        contentTable.add(titleLabel).pad(20).row();

        // Add chart type buttons
        float buttonWidth = dialogWidth * 0.8f;
        float buttonHeight = dialogHeight * 0.1f;
        float buttonPad = 10;

        // Battles chart button
        TextButton battlesButton = new TextButton("Battles by Lutemon", game.getAssetLoader().getSkin());
        battlesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                showChart("battles");
            }
        });
        contentTable.add(battlesButton).width(buttonWidth).height(buttonHeight).pad(buttonPad).row();

        // Wins chart button
        TextButton winsButton = new TextButton("Wins by Lutemon", game.getAssetLoader().getSkin());
        winsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                showChart("wins");
            }
        });
        contentTable.add(winsButton).width(buttonWidth).height(buttonHeight).pad(buttonPad).row();

        // Training chart button
        TextButton trainingButton = new TextButton("Training Sessions by Lutemon", game.getAssetLoader().getSkin());
        trainingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                showChart("training");
            }
        });
        contentTable.add(trainingButton).width(buttonWidth).height(buttonHeight).pad(buttonPad).row();

        // Win rate chart button
        TextButton winRateButton = new TextButton("Win Rate by Lutemon", game.getAssetLoader().getSkin());
        winRateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                showChart("winrate");
            }
        });
        contentTable.add(winRateButton).width(buttonWidth).height(buttonHeight).pad(buttonPad).row();

        // Cancel button
        TextButton cancelButton = new TextButton("Cancel", game.getAssetLoader().getSkin());
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        contentTable.add(cancelButton).width(buttonWidth * 0.5f).height(buttonHeight).pad(buttonPad).row();

        dialog.getContentTable().add(contentTable).expand().fill();
        dialog.show(stage);
    }

    /**
     * Shows a chart of the specified type.
     *
     * @param chartType The type of chart to show ("battles", "wins", "training", or "winrate")
     */
    private void showChart(String chartType) {
        StatisticsChartDialog chartDialog = new StatisticsChartDialog(game.getAssetLoader().getSkin(), chartType);
        chartDialog.show(stage);
    }
}
