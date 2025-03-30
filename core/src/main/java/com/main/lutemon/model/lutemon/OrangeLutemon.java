package com.main.lutemon.model.lutemon;

public class OrangeLutemon extends Lutemon {
    public OrangeLutemon(int id, String name) {
        super(id, name, LutemonType.ORANGE);
    }

    @Override
    protected void initializeStats() {
        getStats().setBaseStats(
            LutemonType.ORANGE.getAttack(),
            LutemonType.ORANGE.getDefense(),
            LutemonType.ORANGE.getMaxHealth()
        );
    }
} 