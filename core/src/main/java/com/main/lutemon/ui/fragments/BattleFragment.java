package com.main.lutemon.ui.fragments;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.main.lutemon.model.battle.Battle;
import com.main.lutemon.model.battle.BattleState;
import com.main.lutemon.model.lutemon.*;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.screens.BattleScreen;
import com.main.lutemon.ui.components.HealthBar;
import com.main.lutemon.utils.Constants;

import java.util.List;

public class BattleFragment {
    private final BattleScreen screen;
    private final Table table;
    private final ScrollPane scrollPane;
    private final Table lutemonTable;
    private final Skin skin;
    private Lutemon selectedLutemon;
    private HealthBar playerHealthBar;
    private HealthBar enemyHealthBar;
    private Label battleStatusLabel;
    private TextButton attackButton;
    private TextButton defendButton;
    private TextButton specialButton;
    private Table buttonTable;

    public BattleFragment(BattleScreen screen, Skin skin) {
        this.screen = screen;
        this.skin = skin;
        this.selectedLutemon = null;

        // Create main table
        table = new Table();
        table.setFillParent(true);

        // Create scrollable table for Lutemons
        lutemonTable = new Table();
        scrollPane = new ScrollPane(lutemonTable, skin);
        scrollPane.setFadeScrollBars(false);

        // Add title
        Label titleLabel = new Label("Battle Arena", skin);
        table.add(titleLabel).pad(20);
        table.row();

        // Add scroll pane
        table.add(scrollPane).expand().fill();
        table.row();

        // Add buttons
        Table buttonTable = new Table();
        buttonTable.bottom().pad(20);

        TextButton homeButton = new TextButton("Return Home", skin);
        TextButton startBattleButton = new TextButton("Start Battle", skin);
        startBattleButton.setDisabled(true); // Initially disabled

        homeButton.addListener(event -> {
            screen.getGame().navigateToHome();
            return true;
        });

        startBattleButton.addListener(event -> {
            if (selectedLutemon != null) {
                // Create an opponent with similar stats
                Lutemon opponent = createOpponent(selectedLutemon);
                screen.startBattle(selectedLutemon, opponent);
            }
            return true;
        });

        buttonTable.add(homeButton).pad(10).width(150);
        buttonTable.add(startBattleButton).pad(10).width(150);

        table.add(buttonTable);

        // Initial update
        updateLutemonList();

        createUI();
    }

    private void createUI() {
        table.setFillParent(true);
        table.defaults().pad(10);

        // Health bars
        playerHealthBar = new HealthBar(100, 400, 200, null);
        enemyHealthBar = new HealthBar(500, 400, 200, null);
        table.add(playerHealthBar).row();
        table.add(enemyHealthBar).row();

        // Battle status
        battleStatusLabel = new Label("", skin);
        table.add(battleStatusLabel).row();

        // Battle buttons
        this.buttonTable = new Table();
        attackButton = new TextButton("Attack", skin);
        defendButton = new TextButton("Defend", skin);
        specialButton = new TextButton("Special", skin);

        attackButton.addListener(event -> {
            screen.performAction("ATTACK");
            return true;
        });

        defendButton.addListener(event -> {
            screen.performAction("DEFEND");
            return true;
        });

        specialButton.addListener(event -> {
            screen.performAction("SPECIAL");
            return true;
        });

        this.buttonTable.add(attackButton).pad(5);
        this.buttonTable.add(defendButton).pad(5);
        this.buttonTable.add(specialButton).pad(5);

        table.add(this.buttonTable).row();

        // Back button
        TextButton backButton = new TextButton("Back to Home", skin);
        backButton.addListener(event -> {
            screen.getGame().navigateToHome();
            return true;
        });
        table.add(backButton).pad(20);
    }

    private Lutemon createOpponent(Lutemon playerLutemon) {
        // Create a new Lutemon of the same type with similar stats
        Lutemon opponent = null;
        switch (playerLutemon.getType()) {
            case WHITE:
                opponent = new WhiteLutemon(0, "Opponent");
                break;
            case GREEN:
                opponent = new GreenLutemon(0, "Opponent");
                break;
            case PINK:
                opponent = new PinkLutemon(0, "Opponent");
                break;
            case ORANGE:
                opponent = new OrangeLutemon(0, "Opponent");
                break;
            case BLACK:
                opponent = new BlackLutemon(0, "Opponent");
                break;
        }

        // Adjust opponent's stats to be similar to player's
        if (opponent != null) {
            opponent.getStats().setExperience(playerLutemon.getStats().getExperience());
        }

        return opponent;
    }

    public void updateLutemonList() {
        lutemonTable.clear();
        List<Lutemon> lutemons = Storage.getInstance().getLutemonsAtLocation(Storage.Location.BATTLE);

        for (Lutemon lutemon : lutemons) {
            Table lutemonRow = new Table();
            lutemonRow.setBackground(skin.getDrawable("button"));

            // Lutemon name and type
            Label nameLabel = new Label(lutemon.getName(), skin);
            Label typeLabel = new Label(lutemon.getType().name(), skin);
            lutemonRow.add(nameLabel).pad(5);
            lutemonRow.add(typeLabel).pad(5);

            // Stats
            Label healthLabel = new Label("HP: " + lutemon.getStats().getCurrentHealth() + "/" +
                                        lutemon.getStats().getMaxHealth(), skin);
            Label expLabel = new Label("EXP: " + lutemon.getStats().getExperience(), skin);
            Label battlesLabel = new Label("Battles: " + lutemon.getStats().getBattles(), skin);
            Label winsLabel = new Label("Wins: " + lutemon.getStats().getWins(), skin);
            lutemonRow.add(healthLabel).pad(5);
            lutemonRow.add(expLabel).pad(5);
            lutemonRow.add(battlesLabel).pad(5);
            lutemonRow.add(winsLabel).pad(5);

            // Action buttons
            TextButton selectButton = new TextButton("Select", skin);
            TextButton homeButton = new TextButton("Return Home", skin);

            selectButton.addListener(event -> {
                selectedLutemon = lutemon;
                // Enable start battle button
                Table buttonTable = (Table) table.getChildren().get(2);
                TextButton startBattleButton = (TextButton) buttonTable.getChildren().get(1);
                startBattleButton.setDisabled(false);
                return true;
            });

            homeButton.addListener(event -> {
                Storage.getInstance().moveToLocation(lutemon.getId(), Storage.Location.HOME);
                updateLutemonList();
                return true;
            });

            lutemonRow.add(selectButton).pad(5).width(80);
            lutemonRow.add(homeButton).pad(5).width(100);

            lutemonTable.add(lutemonRow).fillX().pad(5);
            lutemonTable.row();
        }
    }

    public void updateBattleState(BattleState state) {
        Battle currentBattle = screen.getCurrentBattle();
        if (currentBattle != null) {
            // Update health bars
            if (playerHealthBar != null && currentBattle.getPlayerLutemon() != null) {
                playerHealthBar.update(
                    currentBattle.getPlayerLutemon().getStats().getCurrentHealth(),
                    currentBattle.getPlayerLutemon().getStats().getMaxHealth()
                );
            }
            
            if (enemyHealthBar != null && currentBattle.getEnemyLutemon() != null) {
                enemyHealthBar.update(
                    currentBattle.getEnemyLutemon().getStats().getCurrentHealth(),
                    currentBattle.getEnemyLutemon().getStats().getMaxHealth()
                );
            }
        }
        
        // Update battle status text
        switch (state) {
            case IN_PROGRESS:
                battleStatusLabel.setText("Battle in progress!");
                enableBattleButtons(true);
                break;
            case FINISHED:
                String resultText = "Battle finished!";
                if (currentBattle != null) {
                    boolean playerWon = currentBattle.getPlayerLutemon().isAlive();
                    resultText += playerWon ? " You won!" : " You lost!";
                }
                battleStatusLabel.setText(resultText);
                enableBattleButtons(false);
                break;
            case STARTING:
                battleStatusLabel.setText("Battle starting...");
                enableBattleButtons(false);
                break;
            default:
                battleStatusLabel.setText("Ready to battle...");
                enableBattleButtons(false);
                break;
        }
    }

    private void enableBattleButtons(boolean enabled) {
        attackButton.setDisabled(!enabled);
        defendButton.setDisabled(!enabled);
        specialButton.setDisabled(!enabled);
    }

    public Table getTable() {
        return table;
    }
}
