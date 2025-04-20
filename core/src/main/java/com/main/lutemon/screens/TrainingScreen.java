package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.main.lutemon.ui.components.AnimatedAvatar;
import com.main.lutemon.ui.components.TrainingAnimation;
import com.main.lutemon.utils.Constants;

import java.util.List;

/**
 * Screen for training Lutemons.
 */
public class TrainingScreen implements Screen {
    private final LutemonGame game;
    private Stage stage;
    private Stage selectionStage;
    private final OrthographicCamera camera;
    private TextureRegion backgroundTexture;
    private Lutemon selectedLutemon;
    private boolean inSelectionMode;
    private boolean isTraining;
    private float trainingTime;
    private float trainingDuration = 8.0f; // 8 seconds of training
    private ProgressBar trainingProgressBar;
    private TrainingAnimation trainingAnimation;
    private Label trainingStatusLabel;
    private Table trainingTable;

    /**
     * Creates a new training screen.
     *
     * @param game The game instance
     */
    public TrainingScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.getScreenWidth(), Constants.getScreenHeight());
        this.inSelectionMode = true;
        this.isTraining = false;
        this.trainingTime = 0;

        initialize();
    }

    /**
     * Initializes the training screen.
     */
    private void initialize() {
        // Create main stage
        stage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));

        // Create selection stage
        selectionStage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));

        // Set input processor to selection stage initially
        Gdx.input.setInputProcessor(selectionStage);

        // Load background
        backgroundTexture = game.getAssetLoader().getBackground("training");

        // Create UI
        createUI();

        // Create selection UI
        createSelectionUI();
    }

    /**
     * Creates the UI for the training screen.
     */
    private void createUI() {
        Skin skin = game.getAssetLoader().getSkin();

        // Main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title
        Label titleLabel = new Label("Training", skin, "title");
        mainTable.add(titleLabel).pad(Constants.getPadding()).row();

        // Training table
        trainingTable = new Table();

        // Training animation container
        Table animationContainer = new Table();
        animationContainer.setBackground(skin.getDrawable("default-pane"));

        // Create training animation
        trainingAnimation = new TrainingAnimation();
        animationContainer.add(trainingAnimation).size(500, 500).pad(20); // Increased container size

        trainingTable.add(animationContainer).pad(20).row();

        // Training status label
        trainingStatusLabel = new Label("Training in progress...", skin);
        trainingStatusLabel.setAlignment(Align.center);
        trainingTable.add(trainingStatusLabel).pad(10).row();

        // Training progress bar
        trainingProgressBar = new ProgressBar(0, 1, 0.01f, false, skin);
        trainingProgressBar.setAnimateDuration(0.25f);
        trainingProgressBar.setSize(300, 30);
        trainingTable.add(trainingProgressBar).width(300).height(30).pad(10).row();

        // Cancel button
        TextButton cancelButton = new TextButton("Cancel Training", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelTraining();
            }
        });

        trainingTable.add(cancelButton).pad(20).width(200).height(60);

        // Add training table to main table
        mainTable.add(trainingTable).expand().fill();

        // Back button
        TextButton backButton = new TextButton("Back to Home", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.navigateToHome();
            }
        });

        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top().right();
        topTable.add(backButton).pad(10);

        // Add tables to stage
        stage.addActor(mainTable);
        stage.addActor(topTable);

        // Initially hide training UI
        trainingTable.setVisible(false);
    }

    /**
     * Creates the UI for Lutemon selection.
     */
    private void createSelectionUI() {
        Skin skin = game.getAssetLoader().getSkin();

        Table selectionTable = new Table();
        selectionTable.setFillParent(true);

        Label titleLabel = new Label("Select Lutemon to Train", skin, "title");
        selectionTable.add(titleLabel).colspan(3).pad(20).row(); // Increased colspan to 3

        // Get Lutemons from storage
        List<Lutemon> lutemons = Storage.getInstance().getAllLutemons();

        if (lutemons.isEmpty()) {
            Label noLutemonsLabel = new Label("No Lutemons available. Create some first!", skin);
            selectionTable.add(noLutemonsLabel).colspan(3).pad(20).row(); // Increased colspan to 3

            TextButton backButton = new TextButton("Back to Home", skin);
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.navigateToHome();
                }
            });

            selectionTable.add(backButton).colspan(3).pad(20); // Increased colspan to 3
        } else {
            // Create scrollable container
            Table lutemonTable = new Table();
            lutemonTable.top();

            // Create a scroll pane with proper settings
            ScrollPane scrollPane = new ScrollPane(lutemonTable, skin);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false); // Only allow vertical scrolling
            scrollPane.setForceScroll(false, true); // Force vertical scrolling
            scrollPane.setOverscroll(false, false); // Disable overscroll

            // Add Lutemons to table
            for (Lutemon lutemon : lutemons) {
                // Create a container for each Lutemon with proper spacing - increased width
                Table lutemonContainer = new Table();
                lutemonContainer.setBackground(skin.getDrawable("default-pane"));
                lutemonContainer.pad(20);

                // Create a row for avatar and info
                Table lutemonRow = new Table();

                // Add animated avatar on the left - increased size by 3x
                AnimatedAvatar avatar = new AnimatedAvatar(lutemon.getType().toString(), 360); // 120 * 3 = 360
                lutemonRow.add(avatar).size(360).padRight(25);

                // Create info table for name, type, and stats
                Table infoTable = new Table();
                infoTable.align(Align.left);

                // Lutemon name with larger font
                Label nameLabel = new Label(lutemon.getName(), skin);
                nameLabel.setFontScale(1.2f);

                // Lutemon type with color
                Label typeLabel = new Label(lutemon.getType().toString(), skin);

                // Set color based on type
                switch (lutemon.getType()) {
                    case WHITE:
                        typeLabel.setColor(0.9f, 0.9f, 0.9f, 1);
                        break;
                    case GREEN:
                        typeLabel.setColor(0.2f, 0.8f, 0.2f, 1);
                        break;
                    case PINK:
                        typeLabel.setColor(0.9f, 0.5f, 0.8f, 1);
                        break;
                    case ORANGE:
                        typeLabel.setColor(1.0f, 0.6f, 0.2f, 1);
                        break;
                    case BLACK:
                        typeLabel.setColor(0.0f, 0.0f, 0.0f, 1); // True black color
                        break;
                }

                // Lutemon stats
                Label statsLabel = new Label(
                    "HP: " + lutemon.getStats().getCurrentHealth() + "/" + lutemon.getStats().getMaxHealth() + "\n" +
                    "ATK: " + lutemon.getStats().getAttack() + "\n" +
                    "DEF: " + lutemon.getStats().getDefense() + "\n" +
                    "EXP: " + lutemon.getExperience(),
                    skin
                );

                // Add info to the info table with proper spacing
                infoTable.add(nameLabel).left().padBottom(5).row();
                infoTable.add(typeLabel).left().padBottom(10).row();
                infoTable.add(statsLabel).left().padBottom(10).row();

                // Add info table to the row
                lutemonRow.add(infoTable).expandX().fillX().left();

                // Add the row to the container
                lutemonContainer.add(lutemonRow).expandX().fillX().row();

                // Add select button at the bottom
                TextButton selectButton = new TextButton("Train", skin);
                selectButton.getLabel().setFontScale(1.2f);
                final Lutemon finalLutemon = lutemon;
                selectButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectLutemon(finalLutemon);
                    }
                });

                // Create a horizontal layout for stats and button
                Table statsAndButtonTable = new Table();

                // Remove stats from info table and add to stats and button table
                infoTable.getChildren().removeIndex(infoTable.getChildren().size - 1); // Remove stats label

                // Add stats to the left of the stats and button table
                statsAndButtonTable.add(statsLabel).left().expandX();

                // Add select button to the right of the stats
                statsAndButtonTable.add(selectButton).width(250).height(80).padLeft(20).right();

                // Add the stats and button table to the container
                lutemonContainer.add(statsAndButtonTable).expandX().fillX().padTop(15);

                // Add the container to the table with spacing - increased width
                lutemonTable.add(lutemonContainer).width(800).pad(15).row();
            }

            // Add the scroll pane to the selection table
            selectionTable.add(scrollPane).colspan(3).expand().fill().pad(20).row(); // Increased colspan to 3

            // Back button - increased size
            TextButton backButton = new TextButton("Back to Home", skin);
            backButton.getLabel().setFontScale(1.5f);
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.navigateToHome();
                }
            });

            selectionTable.add(backButton).colspan(3).width(300).height(80).pad(25); // Increased size
        }

        selectionStage.addActor(selectionTable);
    }

    /**
     * Selects a Lutemon for training.
     *
     * @param lutemon The selected Lutemon
     */
    private void selectLutemon(Lutemon lutemon) {
        selectedLutemon = lutemon;
        inSelectionMode = false;

        // Set the Lutemon for the training animation
        trainingAnimation.setLutemon(selectedLutemon);

        // Show training UI
        trainingTable.setVisible(true);

        // Start training
        startTraining();

        // Switch input processor to main stage
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Starts the training process.
     */
    private void startTraining() {
        isTraining = true;
        trainingTime = 0;
        trainingProgressBar.setValue(0);
        trainingStatusLabel.setText("Training " + selectedLutemon.getName() + "...");
    }

    /**
     * Cancels the current training.
     */
    private void cancelTraining() {
        if (isTraining) {
            isTraining = false;
            trainingTime = 0;
            trainingProgressBar.setValue(0);

            // Return to selection mode
            inSelectionMode = true;
            Gdx.input.setInputProcessor(selectionStage);

            // Hide training UI
            trainingTable.setVisible(false);

            // Recreate selection UI to refresh stats
            selectionStage.clear();
            createSelectionUI();
        }
    }

    /**
     * Completes the training and awards experience.
     */
    private void completeTraining() {
        isTraining = false;

        // Get stats before training
        int oldAttack = selectedLutemon.getStats().getAttack();
        int oldDefense = selectedLutemon.getStats().getDefense();
        int oldMaxHealth = selectedLutemon.getStats().getMaxHealth();

        // Award experience
        selectedLutemon.addExperience(1);

        // Get stats after training
        int newAttack = selectedLutemon.getStats().getAttack();
        int newDefense = selectedLutemon.getStats().getDefense();
        int newMaxHealth = selectedLutemon.getStats().getMaxHealth();

        // Create status message showing stat improvements
        StringBuilder message = new StringBuilder(selectedLutemon.getName() + " gained 1 EXP!\n");

        if (newAttack > oldAttack) {
            message.append("Attack: ").append(oldAttack).append(" → ").append(newAttack).append("\n");
        }

        if (newDefense > oldDefense) {
            message.append("Defense: ").append(oldDefense).append(" → ").append(newDefense).append("\n");
        }

        if (newMaxHealth > oldMaxHealth) {
            message.append("Max Health: ").append(oldMaxHealth).append(" → ").append(newMaxHealth);
        }

        // Update status label
        trainingStatusLabel.setText(message.toString());

        // Add a delay before returning to selection
        Gdx.app.postRunnable(() -> {
            // Schedule return to selection after 2 seconds
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // Return to selection mode
                    inSelectionMode = true;
                    Gdx.input.setInputProcessor(selectionStage);

                    // Hide training UI
                    trainingTable.setVisible(false);

                    // Recreate selection UI to refresh stats
                    selectionStage.clear();
                    createSelectionUI();
                }
            }, 2);
        });
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

        // Update training if in progress
        if (isTraining) {
            trainingTime += delta;
            float progress = Math.min(trainingTime / trainingDuration, 1.0f);
            trainingProgressBar.setValue(progress);

            // Update animation
            trainingAnimation.update(delta);

            // Check if training is complete
            if (trainingTime >= trainingDuration) {
                completeTraining();
            }
        }

        // Draw appropriate stage
        if (inSelectionMode) {
            selectionStage.act(delta);
            selectionStage.draw();
        } else {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Update camera and viewport
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
        selectionStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        selectionStage.dispose();
        if (backgroundTexture != null && backgroundTexture.getTexture() != null) {
            backgroundTexture.getTexture().dispose();
        }
    }

    @Override
    public void show() {
        // Reset selection state
        inSelectionMode = true;
        isTraining = false;
        selectedLutemon = null;

        // Set input processor to selection stage
        Gdx.input.setInputProcessor(selectionStage);

        // Hide training UI
        if (trainingTable != null) {
            trainingTable.setVisible(false);
        }

        // Recreate selection UI to refresh stats
        if (selectionStage != null) {
            selectionStage.clear();
            createSelectionUI();
        }
    }

    @Override
    public void hide() {
        // Cancel any ongoing training when screen is hidden
        if (isTraining) {
            isTraining = false;
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public LutemonGame getGame() {
        return game;
    }

    /**
     * Simple timer class for scheduling tasks.
     */
    private static class Timer {
        private static interface Task {
            void run();
        }

        public static void schedule(Task task, float delaySeconds) {
            final float[] timeLeft = {delaySeconds};

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    timeLeft[0] -= Gdx.graphics.getDeltaTime();

                    if (timeLeft[0] <= 0) {
                        task.run();
                    } else {
                        Gdx.app.postRunnable(this);
                    }
                }
            });
        }
    }
}
