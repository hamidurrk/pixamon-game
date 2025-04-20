package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.main.lutemon.LutemonGame;
import com.main.lutemon.model.battle.Battle;
import com.main.lutemon.model.battle.BattleState;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.ui.components.AnimatedAvatar;
import com.main.lutemon.ui.components.BattleArena;
import com.main.lutemon.ui.components.MatchResultDialog;
import com.main.lutemon.ui.fragments.BattleFragment;
import com.main.lutemon.utils.Constants;

import java.util.List;

/**
 * Screen for the battle gameplay.
 * Handles the battle between two Lutemons with animated 2D pixel art.
 */
public class BattleScreen implements Screen {
    private final LutemonGame game;
    private Stage stage;
    private Stage selectionStage;
    private final OrthographicCamera camera;
    private TextureRegion backgroundTexture;
    private BattleFragment battleFragment;
    private Battle currentBattle;
    private BattleArena battleArena;
    private Table controlsTable;
    private Table selectionTable;
    private boolean inSelectionMode;
    private Lutemon selectedPlayerLutemon;
    private InputMultiplexer inputMultiplexer;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean matchEnded;
    private boolean resultDialogScheduled;
    private float resultDialogTimer;
    private MatchResultDialog resultDialog;
    private static final float RESULT_DIALOG_DELAY = 4.0f;

    /**
     * Creates a new battle screen.
     *
     * @param game The game instance
     */
    public BattleScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.getScreenWidth(), Constants.getScreenHeight());
        this.inSelectionMode = true;
        this.leftPressed = false;
        this.rightPressed = false;
        this.matchEnded = false;
        this.resultDialogScheduled = false;
        this.resultDialogTimer = 0;

        initialize();

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
    }

    /**
     * Initializes the battle screen.
     */
    private void initialize() {
        // main stage
        stage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));

        //  selection stage
        selectionStage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));

        Gdx.input.setInputProcessor(selectionStage);

        backgroundTexture = game.getAssetLoader().getBackground("battle");

        createUI();
        createSelectionUI();
        battleFragment = new BattleFragment(this, game.getAssetLoader().getSkin());
    }

    /**
     * Creates the UI for the battle screen.
     */
    private void createUI() {
        // Main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title
        Label titleLabel = new Label("Battle Arena", game.getAssetLoader().getSkin(), "title");
        mainTable.add(titleLabel).pad(Constants.getPadding()).row();

        // Controls table (positioned above battle arena)
        controlsTable = new Table();
        controlsTable.setFillParent(true);
        controlsTable.bottom();

        // Create directional buttons (left side)
        Table directionTable = new Table();
        directionTable.bottom().left();

        // Create larger, more visible directional buttons
        TextButton leftButton = new TextButton("<", game.getAssetLoader().getSkin());
        TextButton rightButton = new TextButton(">", game.getAssetLoader().getSkin());

        // Style the buttons to be more visible
        leftButton.getLabel().setFontScale(4.5f);
        rightButton.getLabel().setFontScale(4.5f);

        // Added visual feedback for button presses
        leftButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                leftButton.setColor(0.7f, 0.7f, 1.0f, 1.0f); // Visual feedback
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
                leftButton.setColor(Color.WHITE);
            }
        });

        rightButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                rightButton.setColor(0.7f, 0.7f, 1.0f, 1.0f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
                rightButton.setColor(Color.WHITE);
            }
        });

        // Make buttons even larger with increased spacing
        directionTable.add(leftButton).size(220, 220).pad(25);
        directionTable.add(rightButton).size(220, 220).pad(25);

        // Create action buttons (right side)
        Table actionTable = new Table();
        actionTable.bottom().right();

        // Create larger, more visible action buttons
        TextButton attackButton = new TextButton("A", game.getAssetLoader().getSkin());
        TextButton defendButton = new TextButton("B", game.getAssetLoader().getSkin());

        // Style the buttons to be more visible
        attackButton.getLabel().setFontScale(4.0f);
        defendButton.getLabel().setFontScale(4.0f);

        // Add visual feedback and better click handling
        attackButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                attackButton.setColor(1.0f, 0.7f, 0.7f, 1.0f); // Visual feedback - reddish
                if (battleArena != null && !matchEnded) {
                    battleArena.playerAttack();
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                attackButton.setColor(Color.WHITE);
            }
        });

        defendButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                defendButton.setColor(0.7f, 1.0f, 0.7f, 1.0f);
                if (battleArena != null && !matchEnded) {
                    battleArena.playerDefend();
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                defendButton.setColor(Color.WHITE);
            }
        });

        actionTable.add(attackButton).size(230, 220).pad(25);
        actionTable.add(defendButton).size(230, 220).pad(25);

        controlsTable.add(directionTable).expand().left().bottom().pad(50);
        controlsTable.add(actionTable).expand().right().bottom().pad(50);

        // Add tables to stage
        stage.addActor(mainTable);
        stage.addActor(controlsTable);
        controlsTable.setVisible(false);
    }

    /**
     * Creates the UI for Lutemon selection.
     */
    private void createSelectionUI() {
        selectionTable = new Table();
        selectionTable.setFillParent(true);

        Label titleLabel = new Label("Select Your Lutemon", game.getAssetLoader().getSkin(), "title");
        selectionTable.add(titleLabel).colspan(2).pad(20).row();

        // Add Lutemons from storage
        updateSelectionUI();

        selectionStage.addActor(selectionTable);
    }

    /**
     * Updates the selection UI with current Lutemons.
     */
    private void updateSelectionUI() {
        selectionTable.clear();

        // Set appropriate title based on whether player Lutemon is already selected
        String titleText = (selectedPlayerLutemon == null) ? "Select Your Lutemon" : "Select Enemy Lutemon";
        Label titleLabel = new Label(titleText, game.getAssetLoader().getSkin(), "title");
        selectionTable.add(titleLabel).colspan(3).pad(20).row(); // Increased colspan to 3

        // Get Lutemons from storage
        List<Lutemon> lutemons = Storage.getInstance().getAllLutemons();

        if (lutemons.isEmpty()) {
            Label noLutemonsLabel = new Label("No Lutemons available. Create some first!",
                                           game.getAssetLoader().getSkin());
            selectionTable.add(noLutemonsLabel).colspan(3).pad(20).row(); // Increased colspan to 3

            TextButton backButton = new TextButton("Back to Home", game.getAssetLoader().getSkin());
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.navigateToHome();
                }
            });

            selectionTable.add(backButton).colspan(3).pad(20); // Increased colspan to 3
            return;
        }

        Table lutemonTable = new Table();
        lutemonTable.top();

        // Create a scroll pane with proper settings
        ScrollPane scrollPane = new ScrollPane(lutemonTable, game.getAssetLoader().getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Only allow vertical scrolling
        scrollPane.setForceScroll(false, true); // Force vertical scrolling
        scrollPane.setOverscroll(false, false); // Disable overscroll

        // Add Lutemons to table
        for (Lutemon lutemon : lutemons) {
            if (selectedPlayerLutemon != null && lutemon.getId() == selectedPlayerLutemon.getId()) {
                continue; // Skip already selected Lutemon
            }

            // Create a container for each Lutemon with proper spacing - increased width
            Table lutemonContainer = new Table();
            lutemonContainer.setBackground(game.getAssetLoader().getSkin().getDrawable("default-pane"));
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
            Label nameLabel = new Label(lutemon.getName(), game.getAssetLoader().getSkin());
            nameLabel.setFontScale(1.2f);

            // Lutemon type with color
            Label typeLabel = new Label(lutemon.getType().toString(), game.getAssetLoader().getSkin());

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
                "DEF: " + lutemon.getStats().getDefense(),
                game.getAssetLoader().getSkin()
            );

            // Create select button - larger size
            TextButton selectButton = new TextButton("Select", game.getAssetLoader().getSkin());
            selectButton.getLabel().setFontScale(1f);
            final Lutemon finalLutemon = lutemon;
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectLutemon(finalLutemon);
                }
            });

            // Create a horizontal layout for stats and button
            Table statsAndButtonTable = new Table();

            // Add info to the info table with proper spacing
            infoTable.add(nameLabel).left().padBottom(10).row();
            infoTable.add(typeLabel).left().padBottom(15).row();

            // Add info table to the row
            lutemonRow.add(infoTable).expandX().fillX().left();

            // Add the row to the container
            lutemonContainer.add(lutemonRow).expandX().fillX().row();

            // Add stats to the left of the stats and button table
            statsAndButtonTable.add(statsLabel).left().expandX();

            // Add select button to the right of the stats
            statsAndButtonTable.add(selectButton).width(300).height(80).padLeft(20).right();

            // Add the stats and button table to the container
            lutemonContainer.add(statsAndButtonTable).expandX().fillX().padTop(15);

            // Add the container to the table with spacing - increased width
            lutemonTable.add(lutemonContainer).width(1200).pad(15).row();
        }

        // Add the scroll pane to the selection table
        selectionTable.add(scrollPane).colspan(3).expand().fill().pad(20).row(); // Increased colspan to 3

        // Back button - increased size
        TextButton backButton = new TextButton("Back to Home", game.getAssetLoader().getSkin());
        backButton.getLabel().setFontScale(1f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.navigateToHome();
            }
        });

        selectionTable.add(backButton).colspan(3).width(800).height(80).pad(25); // Increased size
    }

    /**
     * Selects a Lutemon for battle.
     *
     * @param lutemon The selected Lutemon
     */
    private void selectLutemon(Lutemon lutemon) {
        if (selectedPlayerLutemon == null) {
            // First selection (player's Lutemon)
            selectedPlayerLutemon = lutemon;

            // Update selection list with new title
            updateSelectionUI();
        } else {
            // Second selection (opponent's Lutemon)
            startBattle(selectedPlayerLutemon, lutemon);
        }
    }

    /**
     * Starts a battle between two Lutemons.
     *
     * @param playerLutemon The player's Lutemon
     * @param opponentLutemon The opponent's Lutemon
     */
    public void startBattle(Lutemon playerLutemon, Lutemon opponentLutemon) {
        // Reset all battle state variables
        matchEnded = false;
        resultDialogScheduled = false;
        resultDialogTimer = 0;
        leftPressed = false;
        rightPressed = false;

        // Reset Lutemons and move them to BATTLE location
        playerLutemon.heal();
        opponentLutemon.heal();

        // Move Lutemons to BATTLE location
        Storage.getInstance().moveToLocation(playerLutemon.getId(), Storage.Location.BATTLE);

        // Clear any existing battle elements
        if (battleArena != null) {
            battleArena.remove();
            battleArena = null;
        }

        // Clear any existing result dialog
        if (resultDialog != null) {
            resultDialog.remove();
            resultDialog = null;
        }

        // Create battle
        currentBattle = new Battle(playerLutemon, opponentLutemon);
        currentBattle.setState(BattleState.IN_PROGRESS);

        // Create battle arena
        battleArena = new BattleArena(
            Constants.getScreenWidth(),
            Constants.getScreenHeight() - 450,
            currentBattle,
            game.getAssetLoader().getSkin(),
            game.getAssetLoader()
        );

        // Position arena - leave more space at bottom for controls
        battleArena.setPosition(0, 350);

        // Add arena to stage
        stage.addActor(battleArena);

        // Show controls
        controlsTable.setVisible(true);

        // Switch to battle mode
        inSelectionMode = false;
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Start the battle
        Gdx.app.postRunnable(() -> {
            currentBattle.start();
        });
    }

    /**
     * Performs a battle action.
     *
     * @param action The action to perform
     */
    public void performAction(String action) {
        if (currentBattle != null && currentBattle.getState() == BattleState.IN_PROGRESS) {
            currentBattle.performAction(action);
            updateUI();
        }
    }

    /**
     * Updates the UI based on the current battle state.
     */
    private void updateUI() {
        if (currentBattle != null && battleFragment != null) {
            // Update battle UI elements
            battleFragment.updateBattleState(currentBattle.getState());

            // Check if battle has ended
            if (currentBattle.getState() == BattleState.FINISHED && !matchEnded) {
                matchEnded = true;
                resultDialogScheduled = true;
                resultDialogTimer = 0;
            }

            // Handle delayed result dialog
            if (resultDialogScheduled) {
                resultDialogTimer += Gdx.graphics.getDeltaTime();
                if (resultDialogTimer >= RESULT_DIALOG_DELAY) {
                    resultDialogScheduled = false;
                    showMatchResult();
                }
            }
        }
    }

    /**
     * Shows the match result dialog.
     */
    private void showMatchResult() {
        boolean playerWon = currentBattle.getPlayerLutemon().isAlive();

        // Create and show the result dialog
        resultDialog = new MatchResultDialog(
            this,
            currentBattle.getPlayerLutemon(),
            currentBattle.getEnemyLutemon(),
            playerWon,
            game.getAssetLoader().getSkin()
        );

        resultDialog.show(stage);
    }

    /**
     * Restarts the battle with the same Lutemons.
     *
     * @param playerLutemon The player's Lutemon
     * @param opponentLutemon The opponent's Lutemon
     */
    public void restartBattle(Lutemon playerLutemon, Lutemon opponentLutemon) {
        // Reset all battle state variables
        matchEnded = false;
        resultDialogScheduled = false;
        resultDialogTimer = 0;
        leftPressed = false;
        rightPressed = false;

        // Reset Lutemons and move them to BATTLE location
        playerLutemon.heal();
        opponentLutemon.heal();

        // Move Lutemons to BATTLE location
        Storage.getInstance().moveToLocation(playerLutemon.getId(), Storage.Location.BATTLE);
        // Note: We don't move the opponent to BATTLE location as it might not be in Storage

        // Clear existing battle elements
        if (battleArena != null) {
            battleArena.remove();
            battleArena = null;
        }

        // Clear any existing result dialog
        if (resultDialog != null) {
            resultDialog.remove();
            resultDialog = null;
        }

        // Create battle
        currentBattle = new Battle(playerLutemon, opponentLutemon);
        currentBattle.setState(BattleState.IN_PROGRESS);

        // Create battle arena
        battleArena = new BattleArena(
            Constants.getScreenWidth(),
            Constants.getScreenHeight() - 450,
            currentBattle,
            game.getAssetLoader().getSkin(),
            game.getAssetLoader()
        );

        // Position arena - leave more space at bottom for controls
        battleArena.setPosition(0, 350);

        // Add arena to stage
        stage.addActor(battleArena);

        // Show controls
        controlsTable.setVisible(true);

        // Switch to battle mode
        inSelectionMode = false;
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Start the battle
        Gdx.app.postRunnable(() -> {
            currentBattle.start();
        });
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        if (inSelectionMode) {
            // Draw selection screen
            game.getBatch().setProjectionMatrix(camera.combined);
            game.getBatch().begin();
            game.getBatch().draw(backgroundTexture, 0, 0, Constants.getScreenWidth(), Constants.getScreenHeight());
            game.getBatch().end();

            selectionStage.act(delta);
            selectionStage.draw();
        } else {
            // Update battle
            if (currentBattle != null) {
                // Only update battle logic if not ended or during the delay before showing result
                if (!matchEnded || resultDialogScheduled) {
                    currentBattle.update(delta);
                }

                // Handle movement based on button presses if match hasn't ended
                if (battleArena != null && !matchEnded) {
                    // Handle movement - make sure we're not in a special state
                    if (!battleArena.getPlayerLutemon().isAttacking() &&
                        !battleArena.getPlayerLutemon().isHurt() &&
                        !battleArena.getPlayerLutemon().isDead()) {

                        if (leftPressed) {
                            battleArena.movePlayerLeft();
                        } else if (rightPressed) {
                            battleArena.movePlayerRight();
                        } else {
                            // Set idle animation when not moving
                            battleArena.stopPlayerMovement();
                        }
                    }

                    // Update enemy AI
                    battleArena.updateEnemyAI(delta);
                }

                updateUI();
            }

            // Draw battle screen
            game.getBatch().setProjectionMatrix(camera.combined);
            game.getBatch().begin();
            game.getBatch().draw(backgroundTexture, 0, 0, Constants.getScreenWidth(), Constants.getScreenHeight());
            game.getBatch().end();

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

        // Recreate UI to adjust to new screen size
        stage.clear();
        createUI();

        // Recreate selection UI
        selectionStage.clear();
        createSelectionUI();

        // Re-add battle arena if it exists
        if (battleArena != null) {
            stage.addActor(battleArena);
        }

        // Update battle UI if there's an ongoing battle
        if (currentBattle != null) {
            updateUI();
        }
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
        // Reset all state variables
        selectedPlayerLutemon = null;
        inSelectionMode = true;
        matchEnded = false;
        resultDialogScheduled = false;
        resultDialogTimer = 0;
        leftPressed = false;
        rightPressed = false;

        // Clear any existing battle arena
        if (battleArena != null) {
            battleArena.remove();
            battleArena = null;
        }

        // Clear any existing result dialog
        if (resultDialog != null) {
            resultDialog.remove();
            resultDialog = null;
        }

        // Set input processor to selection stage
        Gdx.input.setInputProcessor(selectionStage);

        // Update selection UI
        updateSelectionUI();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    /**
     * Gets the game instance.
     *
     * @return The game instance
     */
    public LutemonGame getGame() {
        return game;
    }

    /**
     * Gets the current battle.
     *
     * @return The current battle
     */
    public Battle getCurrentBattle() {
        return currentBattle;
    }

    /**
     * Gets the stage.
     *
     * @return The stage
     */
    public Stage getStage() {
        return stage;
    }

}
