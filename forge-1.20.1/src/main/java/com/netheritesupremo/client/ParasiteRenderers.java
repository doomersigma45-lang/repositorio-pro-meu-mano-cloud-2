package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.skeleton.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import com.netheritesupremo.parasite.ParasitePlayer;
import com.netheritesupremo.registry.ModEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
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

    /** Texturas das fases 1, 2 e 3. */
    private static ResourceLocation[] phases(String name) {
        return new ResourceLocation[] {tex(name), tex(name + "_2"), tex(name + "_3")};
    }

    /** Fase lida do efeito escondido que o servidor mantem no parasita (1 se nao houver). */
    static int phase(LivingEntity entity) {
        MobEffectInstance e = entity.getEffect(ModEffects.PARASITE_PHASE.get());
        return e == null ? 1 : Math.min(3, e.getAmplifier() + 2);
    }

    /** Fase 2: mais magro e torto. Fase 3: ainda mais esticado, torto e curvado. */
    static void deform(PoseStack pose, int phase) {
        if (phase == 2) {
            pose.scale(0.9F, 1.1F, 0.9F);
            pose.mulPose(Axis.ZP.rotationDegrees(5.0F));
        } else if (phase >= 3) {
            pose.scale(0.82F, 1.22F, 0.82F);
            pose.mulPose(Axis.ZP.rotationDegrees(11.0F));
            pose.mulPose(Axis.XP.rotationDegrees(7.0F));
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void spikes(LivingEntityRenderer<T, M> renderer,
            EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
        renderer.addLayer(new ParasiteSpikesLayer<T, M>(renderer, ctx, layer, SPIKES));
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        final ResourceLocation[] worm = phases("parasite_worm");
        final ResourceLocation[] cow = phases("parasite_cow");
        final ResourceLocation[] pig = phases("parasite_pig");
        final ResourceLocation[] chicken = phases("parasite_chicken");
        final ResourceLocation[] zombie = phases("parasite_zombie");
        final ResourceLocation[] skeleton = phases("parasite_skeleton");
        final ResourceLocation[] creeper = phases("parasite_creeper");
        final ResourceLocation[] spider = phases("parasite_spider");
        final ResourceLocation[] enderman = phases("parasite_enderman");

        event.<Endermite>registerEntityRenderer(ModEntities.PARASITE_WORM.get(), ctx -> new EndermiteRenderer(ctx) {
            @Override
                        public ResourceLocation getTextureLocation(Endermite entity) {
                return worm[phase(entity) - 1];
            }

            @Override
            protected void scale(Endermite entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
        });
        event.<Cow>registerEntityRenderer(ModEntities.PARASITE_COW.get(), ctx -> new CowRenderer(ctx) {
            @Override
                        public ResourceLocation getTextureLocation(Cow entity) {
                return cow[phase(entity) - 1];
            }

            @Override
            protected void scale(Cow entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
        });
        event.<Pig>registerEntityRenderer(ModEntities.PARASITE_PIG.get(), ctx -> new PigRenderer(ctx) {
            @Override
                        public ResourceLocation getTextureLocation(Pig entity) {
                return pig[phase(entity) - 1];
            }

            @Override
            protected void scale(Pig entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
        });
        event.<Chicken>registerEntityRenderer(ModEntities.PARASITE_CHICKEN.get(), ctx -> new ChickenRenderer(ctx) {
            @Override
                        public ResourceLocation getTextureLocation(Chicken entity) {
                return chicken[phase(entity) - 1];
            }

            @Override
            protected void scale(Chicken entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
        });
        event.<Zombie>registerEntityRenderer(ModEntities.PARASITE_ZOMBIE.get(), ctx -> {
            ZombieRenderer r = new ZombieRenderer(ctx) {
                @Override
                            public ResourceLocation getTextureLocation(Zombie entity) {
                return zombie[phase(entity) - 1];
            }

            @Override
            protected void scale(Zombie entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
            };
            spikes(r, ctx, ParasiteSpikesModel.HUMANOID);
            return r;
        });
        event.<AbstractSkeleton>registerEntityRenderer(ModEntities.PARASITE_SKELETON.get(), ctx -> {
            SkeletonRenderer r = new SkeletonRenderer(ctx) {
                @Override
                            public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
                return skeleton[phase(entity) - 1];
            }

            @Override
            protected void scale(AbstractSkeleton entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
            };
            spikes(r, ctx, ParasiteSpikesModel.HUMANOID);
            return r;
        });
        event.<Creeper>registerEntityRenderer(ModEntities.PARASITE_CREEPER.get(), ctx -> {
            CreeperRenderer r = new CreeperRenderer(ctx) {
                @Override
                            public ResourceLocation getTextureLocation(Creeper entity) {
                return creeper[phase(entity) - 1];
            }

            @Override
            protected void scale(Creeper entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
            };
            spikes(r, ctx, ParasiteSpikesModel.CREEPER);
            return r;
        });
        event.<Spider>registerEntityRenderer(ModEntities.PARASITE_SPIDER.get(), ctx -> new SpiderRenderer<Spider>(ctx) {
            @Override
                        public ResourceLocation getTextureLocation(Spider entity) {
                return spider[phase(entity) - 1];
            }

            @Override
            protected void scale(Spider entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
        });
        event.<EnderMan>registerEntityRenderer(ModEntities.PARASITE_ENDERMAN.get(), ctx -> {
            EndermanRenderer r = new EndermanRenderer(ctx) {
                @Override
                            public ResourceLocation getTextureLocation(EnderMan entity) {
                return enderman[phase(entity) - 1];
            }

            @Override
            protected void scale(EnderMan entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity));
            }
            };
            spikes(r, ctx, ParasiteSpikesModel.ENDER);
            return r;
        });
        event.registerEntityRenderer(ModEntities.PARASITE_TOTEM.get(),
                ctx -> new AirborneRenderer(ctx, AirborneRenderer.TOTEM_LAYER, tex("parasite_totem"), 0.9F));
        event.registerEntityRenderer(ModEntities.PARASITE_BLOOM.get(),
                ctx -> new AirborneRenderer(ctx, AirborneRenderer.BLOOM_LAYER, tex("parasite_bloom"), 0.8F));
        event.registerEntityRenderer(ModEntities.PARASITE_PLAYER.get(), ctx -> {
            PlayerRobotRenderer<ParasitePlayer> r = new PlayerRobotRenderer<ParasitePlayer>(ctx, tex("parasite_player"), 1.0F);
            // armadura visivel (a do equipamento por fase)
            r.addLayer(new HumanoidArmorLayer<ParasitePlayer, PlayerModel<ParasitePlayer>, HumanoidModel<ParasitePlayer>>(r,
                    new HumanoidModel<ParasitePlayer>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                    new HumanoidModel<ParasitePlayer>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), ctx.getModelManager()));
            return r;
        });
    }
}
