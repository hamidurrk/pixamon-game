package com.main.lutemon.model.lutemon;

public class WhiteLutemon extends Lutemon {
    public WhiteLutemon(int id, String name) {
        super(id, name, LutemonType.WHITE);
    }

    @Override
    protected void initializeStats() {
        getStats().setBaseStats(
            LutemonType.WHITE.getAttack(),
            LutemonType.WHITE.getDefense(),
            LutemonType.WHITE.getMaxHealth()
        );
    }
} 