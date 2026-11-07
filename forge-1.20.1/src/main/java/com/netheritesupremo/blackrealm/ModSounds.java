package com.netheritesupremo.blackrealm;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, NetheriteSupremo.MODID);

    /** Musica da Terra Negra (faixa tocada de tras para frente). */
    public static final RegistryObject<SoundEvent> BLACK_REALM_MUSIC = SOUNDS.register("music.black_realm",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NetheriteSupremo.MODID, "music.black_realm")));

    private ModSounds() {
    }
}
