package com.netheritesupremo.skeleton;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, NetheriteSupremo.MODID);

    public static final RegistryObject<MobEffect> WAR_MARK = EFFECTS.register("war_mark", WarMarkEffect::new);

    public static final RegistryObject<MobEffect> INFESTATION = EFFECTS.register("infestation",
            com.netheritesupremo.parasite.InfestationEffect::new);
    public static final RegistryObject<MobEffect> PARASITIZED = EFFECTS.register("parasitized",
            com.netheritesupremo.parasite.ParasitizedEffect::new);

    private ModEffects() {
    }
}
