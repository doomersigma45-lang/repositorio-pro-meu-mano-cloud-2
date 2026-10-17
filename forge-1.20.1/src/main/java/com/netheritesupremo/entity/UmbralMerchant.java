package com.netheritesupremo.entity;

import com.netheritesupremo.trade.UmbralTrades;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

/**
 * Mercador Umbral: usa o modelo/textura do mercador viajante (estende WanderingTrader),
 * mas nunca some e vende as melhores trocas possiveis (ver {@link UmbralTrades}).
 */
public class UmbralMerchant extends WanderingTrader {
    public UmbralMerchant(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
        this.forcedLoading = false;
    }

    @Override
    protected void registerGoals() {
        // Sem a pocao de invisibilidade do mercador viajante
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        UmbralTrades.addMerchantTrades(offers);
    }

    @Override
    public void tick() {
        super.tick();
        // Fica perto de onde nasceu (a restricao nao e salva, entao refaz ao carregar)
        if (!this.level().isClientSide && this.tickCount == 40 && !this.hasRestriction()) {
            this.restrictTo(this.blockPosition(), 24);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return false;
    }
}
