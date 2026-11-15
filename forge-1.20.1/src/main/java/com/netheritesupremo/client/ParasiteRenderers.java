package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.parasite.ParasitePlayer;
import com.netheritesupremo.registry.ModEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EndermiteRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.client.event.EntityRenderersEvent;

/** Renderers dos infectados: modelos de vanilla com textura de carne/osso e espinhos (jogador usa skin propria). */
public final class ParasiteRenderers {
    private static final ResourceLocation SPIKES = tex("parasite_spikes");

    private ParasiteRenderers() {
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/" + name + ".png");
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void spikes(LivingEntityRenderer<T, M> renderer,
            EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
        renderer.addLayer(new ParasiteSpikesLayer<T, M>(renderer, ctx, layer, SPIKES));
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        final ResourceLocation worm = tex("parasite_worm");
        final ResourceLocation cow = tex("parasite_cow");
        final ResourceLocation pig = tex("parasite_pig");
        final ResourceLocation chicken = tex("parasite_chicken");
        final ResourceLocation zombie = tex("parasite_zombie");
        final ResourceLocation skeleton = tex("parasite_skeleton");
        final ResourceLocation creeper = tex("parasite_creeper");
        final ResourceLocation spider = tex("parasite_spider");
        final ResourceLocation enderman = tex("parasite_enderman");

        event.<Endermite>registerEntityRenderer(ModEntities.PARASITE_WORM.get(), ctx -> new EndermiteRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Endermite entity) {
                return worm;
            }
        });
        event.<Cow>registerEntityRenderer(ModEntities.PARASITE_COW.get(), ctx -> new CowRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Cow entity) {
                return cow;
            }
        });
        event.<Pig>registerEntityRenderer(ModEntities.PARASITE_PIG.get(), ctx -> new PigRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Pig entity) {
                return pig;
            }
        });
        event.<Chicken>registerEntityRenderer(ModEntities.PARASITE_CHICKEN.get(), ctx -> new ChickenRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Chicken entity) {
                return chicken;
            }
        });
        event.<Zombie>registerEntityRenderer(ModEntities.PARASITE_ZOMBIE.get(), ctx -> {
            ZombieRenderer r = new ZombieRenderer(ctx) {
                @Override
                public ResourceLocation getTextureLocation(Zombie entity) {
                    return zombie;
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.HUMANOID);
            return r;
        });
        event.<AbstractSkeleton>registerEntityRenderer(ModEntities.PARASITE_SKELETON.get(), ctx -> {
            SkeletonRenderer r = new SkeletonRenderer(ctx) {
                @Override
                public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
                    return skeleton;
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.HUMANOID);
            return r;
        });
        event.<Creeper>registerEntityRenderer(ModEntities.PARASITE_CREEPER.get(), ctx -> {
            CreeperRenderer r = new CreeperRenderer(ctx) {
                @Override
                public ResourceLocation getTextureLocation(Creeper entity) {
                    return creeper;
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.CREEPER);
            return r;
        });
        event.<Spider>registerEntityRenderer(ModEntities.PARASITE_SPIDER.get(), ctx -> new SpiderRenderer<Spider>(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Spider entity) {
                return spider;
            }
        });
        event.<EnderMan>registerEntityRenderer(ModEntities.PARASITE_ENDERMAN.get(), ctx -> {
            EndermanRenderer r = new EndermanRenderer(ctx) {
                @Override
                public ResourceLocation getTextureLocation(EnderMan entity) {
                    return enderman;
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.ENDER);
            return r;
        });
        event.registerEntityRenderer(ModEntities.PARASITE_PLAYER.get(), ctx -> new PlayerRobotRenderer<ParasitePlayer>(ctx,
                tex("parasite_player"), 1.0F));
    }
}
