package com.main.lutemon.model.lutemon;

public class GreenLutemon extends Lutemon {
    public GreenLutemon(int id, String name) {
        super(id, name, LutemonType.GREEN);
    }

    @Override
    protected void initializeStats() {
        getStats().setBaseStats(
            LutemonType.GREEN.getAttack(),
            LutemonType.GREEN.getDefense(),
            LutemonType.GREEN.getMaxHealth()
        );
    }
} 