package com.main.lutemon.model.lutemon;

public class PinkLutemon extends Lutemon {
    public PinkLutemon(int id, String name) {
        super(id, name, LutemonType.PINK);
    }

    @Override
    protected void initializeStats() {
        getStats().setBaseStats(
            LutemonType.PINK.getAttack(),
            LutemonType.PINK.getDefense(),
            LutemonType.PINK.getMaxHealth()
        );
    }
} 