package com.main.lutemon.model.lutemon;

public class BlackLutemon extends Lutemon {
    public BlackLutemon(int id, String name) {
        super(id, name, LutemonType.BLACK);
    }

    @Override
    protected void initializeStats() {
        getStats().setBaseStats(
            LutemonType.BLACK.getAttack(),
            LutemonType.BLACK.getDefense(),
            LutemonType.BLACK.getMaxHealth()
        );
    }
} 