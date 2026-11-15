package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.parasite.ParasitePlayer;
import com.netheritesupremo.registry.ModEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EndermiteRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.client.event.EntityRenderersEvent;

/** Renderers dos parasitas: modelos de vanilla com uma camada verde por cima (jogador usa skin propria). */
public final class ParasiteRenderers {
    private ParasiteRenderers() {
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void tint(LivingEntityRenderer<T, M> renderer, String vanillaTexture) {
        renderer.addLayer(new ParasiteLayer<T, M>(renderer, new ResourceLocation("minecraft", vanillaTexture)));
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.<Endermite>registerEntityRenderer(ModEntities.PARASITE_WORM.get(), ctx -> {
            EndermiteRenderer r = new EndermiteRenderer(ctx);
            tint(r, "textures/entity/endermite.png");
            return r;
        });
        event.<Cow>registerEntityRenderer(ModEntities.PARASITE_COW.get(), ctx -> {
            CowRenderer r = new CowRenderer(ctx);
            tint(r, "textures/entity/cow/cow.png");
            return r;
        });
        event.<Pig>registerEntityRenderer(ModEntities.PARASITE_PIG.get(), ctx -> {
            PigRenderer r = new PigRenderer(ctx);
            tint(r, "textures/entity/pig/pig.png");
            return r;
        });
        event.<Chicken>registerEntityRenderer(ModEntities.PARASITE_CHICKEN.get(), ctx -> {
            ChickenRenderer r = new ChickenRenderer(ctx);
            tint(r, "textures/entity/chicken.png");
            return r;
        });
        event.<Zombie>registerEntityRenderer(ModEntities.PARASITE_ZOMBIE.get(), ctx -> {
            ZombieRenderer r = new ZombieRenderer(ctx);
            tint(r, "textures/entity/zombie/zombie.png");
            return r;
        });
        event.<Skeleton>registerEntityRenderer(ModEntities.PARASITE_SKELETON.get(), ctx -> {
            SkeletonRenderer r = new SkeletonRenderer(ctx);
            tint(r, "textures/entity/skeleton/skeleton.png");
            return r;
        });
        event.<Creeper>registerEntityRenderer(ModEntities.PARASITE_CREEPER.get(), ctx -> {
            CreeperRenderer r = new CreeperRenderer(ctx);
            tint(r, "textures/entity/creeper/creeper.png");
            return r;
        });
        event.<Spider>registerEntityRenderer(ModEntities.PARASITE_SPIDER.get(), ctx -> {
            SpiderRenderer<Spider> r = new SpiderRenderer<>(ctx);
            tint(r, "textures/entity/spider/spider.png");
            return r;
        });
        event.registerEntityRenderer(ModEntities.PARASITE_PLAYER.get(), ctx -> new PlayerRobotRenderer<ParasitePlayer>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/parasite_player.png"), 1.0F));
    }
}
