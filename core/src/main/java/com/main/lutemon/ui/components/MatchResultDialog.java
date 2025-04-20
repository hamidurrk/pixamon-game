package com.main.lutemon.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.screens.BattleScreen;
import com.main.lutemon.utils.StatisticsManager;

/**
 * Dialog to display the match result and provide options to restart or quit.
 */
public class MatchResultDialog extends Window {
    private final BattleScreen battleScreen;
    private final Lutemon playerLutemon;
    private final Lutemon opponentLutemon;
    private final boolean playerWon;

    /**
     * Creates a new match result dialog.
     *
     * @param battleScreen The battle screen
     * @param playerLutemon The player's Lutemon
     * @param opponentLutemon The opponent's Lutemon
     * @param playerWon Whether the player won
     * @param skin The skin to use for UI elements
     */
    public MatchResultDialog(BattleScreen battleScreen, Lutemon playerLutemon, Lutemon opponentLutemon,
                            boolean playerWon, Skin skin) {
        super("", skin);
        this.battleScreen = battleScreen;
        this.playerLutemon = playerLutemon;
        this.opponentLutemon = opponentLutemon;
        this.playerWon = playerWon;

        // Set up dialog properties
        setModal(true);
        setMovable(false);
        setResizable(false);

        // Set size to 70% of the screen size
        float width = battleScreen.getStage().getWidth() * 0.7f;
        float height = battleScreen.getStage().getHeight() * 0.7f;
        setSize(width, height);
        setPosition((battleScreen.getStage().getWidth() - width) / 2,
                   (battleScreen.getStage().getHeight() - height) / 2);

        // Create content
        createContent(skin);
    }

    /**
     * Creates the dialog content.
     *
     * @param skin The skin to use for UI elements
     */
    private void createContent(Skin skin) {
        Table contentTable = new Table();
        contentTable.pad(20);

        // Result message
        String resultMessage = playerWon
            ? "Congratulations! Your " + playerLutemon.getName() + " has won the battle!"
            : "Oh no! Your " + playerLutemon.getName() + " has been defeated!";

        Label resultLabel = new Label(resultMessage, skin);
        resultLabel.setWrap(true);
        resultLabel.setAlignment(Align.center);
        resultLabel.setFontScale(1.5f);

        // Stats message
        String statsMessage = playerWon
            ? "Your " + playerLutemon.getName() + " defeated " + opponentLutemon.getName() + "!"
            : opponentLutemon.getName() + " defeated your " + playerLutemon.getName() + "!";

        Label statsLabel = new Label(statsMessage, skin);
        statsLabel.setWrap(true);
        statsLabel.setAlignment(Align.center);
        statsLabel.setFontScale(1.3f);

        // Buttons with larger text
        TextButton restartButton = new TextButton("Restart Match", skin);
        TextButton quitButton = new TextButton("Quit to Home", skin);

        // Make button text larger
        restartButton.getLabel().setFontScale(1f);
        quitButton.getLabel().setFontScale(1f);

        // Button listeners
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Record battle statistics for both Lutemons
                recordBattleStatistics();

                // Make sure the player's Lutemon is in the BATTLE location
                Storage.getInstance().moveToLocation(playerLutemon.getId(), Storage.Location.BATTLE);
                battleScreen.restartBattle(playerLutemon, opponentLutemon);
                remove(); // Remove dialog
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Record battle statistics for both Lutemons
                recordBattleStatistics();

                // Move the player's Lutemon back to HOME location before navigating to home
                Storage.getInstance().moveToLocation(playerLutemon.getId(), Storage.Location.HOME);
                battleScreen.getGame().navigateToHome();
                remove(); // Remove dialog
            }
        });

        float contentWidth = getWidth() * 0.8f;
        float buttonWidth = getWidth() * 0.8f;
        float buttonHeight = getHeight() * 0.15f;

        contentTable.add(resultLabel).width(contentWidth).pad(20).row();
        contentTable.add(statsLabel).width(contentWidth).pad(20).row();
        contentTable.add(restartButton).width(buttonWidth).height(buttonHeight).pad(20).row();
        contentTable.add(quitButton).width(buttonWidth).height(buttonHeight).pad(20);

        add(contentTable).expand().fill();
    }

    /**
     * Records battle statistics for both Lutemons.
     * This method ensures that both Lutemons have their battle count incremented,
     * and the winner has their win count incremented.
     */
    private void recordBattleStatistics() {
        // Record battle for player Lutemon
        playerLutemon.getStats().incrementBattles();
        if (playerWon) {
            playerLutemon.getStats().incrementWins();
            playerLutemon.getStats().incrementExperience(); // Bonus experience for winning
            System.out.println("Player " + playerLutemon.getName() + " gained 1 experience point for winning");
        }

        // Record battle for opponent Lutemon (if it's in storage)
        if (Storage.getInstance().getLutemon(opponentLutemon.getId()) != null) {
            opponentLutemon.getStats().incrementBattles();
            if (!playerWon) {
                opponentLutemon.getStats().incrementWins();
                opponentLutemon.getStats().incrementExperience(); // Bonus experience for winning
                System.out.println("Opponent " + opponentLutemon.getName() + " gained 1 experience point for winning");
            }
        }

        // Save the game to persist statistics
        battleScreen.getGame().saveGame();
    }

    /**
     * Shows the dialog on the given stage.
     *
     * @param stage The stage to show the dialog on
     */
    public void show(Stage stage) {
        stage.addActor(this);
    }
}
