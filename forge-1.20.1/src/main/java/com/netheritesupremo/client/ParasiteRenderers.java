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
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
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

    /**
     * Deformacao das fases: fase 2 magro e torto, fase 3 esticado, curvado e assimetrico. Cada criatura tem seu proprio
     * jeito torto (sorteado pelo id) e, na fase 3, o corpo pulsa como se algo se mexesse por dentro.
     */
    static void deform(PoseStack pose, int phase, LivingEntity e, float pt) {
        if (phase < 2) {
            pose.scale(0.93F, 1.03F, 0.93F);
            return;
        }
        int id = e.getId();
        float a = (Math.floorMod(id * 37, 11) - 5) / 5.0F;
        float b = (Math.floorMod(id * 53, 9) - 4) / 4.0F;
        float side = a >= 0.0F ? 1.0F : -1.0F;
        float pulse = net.minecraft.util.Mth.sin((e.tickCount + pt) * 0.18F + id) * (phase >= 3 ? 0.04F : 0.015F);
        if (phase == 2) {
            pose.scale(0.8F + 0.05F * a + pulse, 1.12F + 0.05F * b - pulse * 0.5F, 0.8F - 0.04F * a + pulse);
            pose.mulPose(Axis.ZP.rotationDegrees(6.0F * side + 3.0F * a));
            pose.mulPose(Axis.XP.rotationDegrees(5.0F));
        } else {
            pose.scale(0.66F + 0.08F * a + pulse, 1.28F + 0.08F * b - pulse, 0.64F - 0.06F * a + pulse);
            pose.mulPose(Axis.ZP.rotationDegrees(12.0F * side + 5.0F * a));
            pose.mulPose(Axis.XP.rotationDegrees(12.0F + 4.0F * b));
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void spikes(LivingEntityRenderer<T, M> renderer,
            EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
        renderer.addLayer(new ParasiteSpikesLayer<T, M>(renderer, ctx, layer, SPIKES));
        // tumores de carne (alem dos ossos expostos), desde a fase 1
        if (layer == ParasiteSpikesModel.HUMANOID) {
            renderer.addLayer(new ParasiteSpikesLayer<T, M>(renderer, ctx, ParasiteSpikesModel.TUMORS, SPIKES, 1));
        } else if (layer == ParasiteSpikesModel.CREEPER) {
            renderer.addLayer(new ParasiteSpikesLayer<T, M>(renderer, ctx, ParasiteSpikesModel.TUMORS_CREEPER, SPIKES, 1));
        } else if (layer == ParasiteSpikesModel.ENDER) {
            renderer.addLayer(new ParasiteSpikesLayer<T, M>(renderer, ctx, ParasiteSpikesModel.TUMORS_ENDER, SPIKES, 1));
        }
    }

    /** Aldeao parasita: modelo do Aldeao Zumbi, sem a roupa de profissao, com garras e olhos brilhantes. */
    private static ZombieVillagerRenderer villagerRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture, float size) {
        ZombieVillagerRenderer r = new ZombieVillagerRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(ZombieVillager entity) {
                return texture;
            }

            @Override
            protected void scale(ZombieVillager entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                pose.scale(size, size, size);
                deform(pose, phase(entity), entity, partialTick);
            }

            { // sem a roupa de profissao do Aldeao Zumbi
                this.layers.removeIf(l -> l instanceof VillagerProfessionLayer<?, ?>);
            }
        };
        r.addLayer(new ParasiteClawsLayer<ZombieVillager, ZombieVillagerModel<ZombieVillager>>(r, ctx));
        r.addLayer(new ParasiteMawLayer<ZombieVillager, ZombieVillagerModel<ZombieVillager>>(r, ctx));
        spikes(r, ctx, ParasiteSpikesModel.HUMANOID);
        r.addLayer(new EyesLayer<ZombieVillager, ZombieVillagerModel<ZombieVillager>>(r) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(ParasiteTextures.VILLAGER_EYES);
            }
        });
        return r;
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
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
                deform(pose, phase(entity), entity, partialTick);
            }
            };
            spikes(r, ctx, ParasiteSpikesModel.ENDER);
            return r;
        });
        event.registerEntityRenderer(ModEntities.PARASITE_WARDEN.get(), ParasiteWardenRenderer::new);
        event.registerEntityRenderer(ModEntities.PARASITE_CHIMERA.get(), ctx -> new ParasiteBeastRenderer<com.netheritesupremo.parasite.ParasiteChimera>(ctx,
                new ParasiteBeastModels.Hound<com.netheritesupremo.parasite.ParasiteChimera>(ctx.bakeLayer(ParasiteBeastModels.HOUND)), 0.9F, ParasiteTextures.HYBRID_CHIMERA, 1.5F));
        event.registerEntityRenderer(ModEntities.PARASITE_AMALGAM.get(), ctx -> new ParasiteBeastRenderer<com.netheritesupremo.parasite.ParasiteAmalgam>(ctx,
                new ParasiteBeastModels.Mother<com.netheritesupremo.parasite.ParasiteAmalgam>(ctx.bakeLayer(ParasiteBeastModels.MOTHER)), 1.4F, ParasiteTextures.HYBRID_AMALGAM, 1.1F));
        event.<ZombieVillager>registerEntityRenderer(ModEntities.PARASITE_ABOMINATION.get(), ctx -> villagerRenderer(ctx, ParasiteTextures.VILLAGER, 1.45F));
        event.<net.minecraft.world.entity.animal.Squid>registerEntityRenderer(ModEntities.PARASITE_SQUID.get(),
                ctx -> new net.minecraft.client.renderer.entity.SquidRenderer<net.minecraft.world.entity.animal.Squid>(ctx,
                        new net.minecraft.client.model.SquidModel<net.minecraft.world.entity.animal.Squid>(ctx.bakeLayer(ModelLayers.SQUID))) {
                    @Override
                    public ResourceLocation getTextureLocation(net.minecraft.world.entity.animal.Squid entity) {
                        return ParasiteTextures.SQUID;
                    }

                    @Override
                    protected void scale(net.minecraft.world.entity.animal.Squid entity, PoseStack pose, float partialTick) {
                        super.scale(entity, pose, partialTick);
                        deform(pose, phase(entity), entity, partialTick);
                    }
                });
        event.<net.minecraft.world.entity.GlowSquid>registerEntityRenderer(ModEntities.PARASITE_GLOW_SQUID.get(),
                ctx -> new net.minecraft.client.renderer.entity.GlowSquidRenderer(ctx,
                        new net.minecraft.client.model.SquidModel<net.minecraft.world.entity.GlowSquid>(ctx.bakeLayer(ModelLayers.GLOW_SQUID))) {
                    @Override
                    public ResourceLocation getTextureLocation(net.minecraft.world.entity.GlowSquid entity) {
                        return ParasiteTextures.GLOW_SQUID;
                    }

                    @Override
                    protected void scale(net.minecraft.world.entity.GlowSquid entity, PoseStack pose, float partialTick) {
                        super.scale(entity, pose, partialTick);
                        deform(pose, phase(entity), entity, partialTick);
                    }
                });
        event.<net.minecraft.world.entity.monster.Witch>registerEntityRenderer(ModEntities.PARASITE_WITCH.get(),
                ctx -> new net.minecraft.client.renderer.entity.WitchRenderer(ctx) {
                    @Override
                    public ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Witch entity) {
                        return ParasiteTextures.WITCH;
                    }

                    @Override
                    protected void scale(net.minecraft.world.entity.monster.Witch entity, PoseStack pose, float partialTick) {
                        super.scale(entity, pose, partialTick);
                        pose.scale(1.2F, 1.2F, 1.2F); // mini boss
                        deform(pose, phase(entity), entity, partialTick);
                    }
                });
        event.<net.minecraft.world.entity.monster.Silverfish>registerEntityRenderer(ModEntities.PARASITE_SILVERFISH.get(),
                ctx -> new net.minecraft.client.renderer.entity.SilverfishRenderer(ctx) {
                    @Override
                    public ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Silverfish entity) {
                        return ParasiteTextures.SILVERFISH;
                    }

                    @Override
                    protected void scale(net.minecraft.world.entity.monster.Silverfish entity, PoseStack pose, float partialTick) {
                        super.scale(entity, pose, partialTick);
                        pose.scale(2.4F, 2.4F, 2.4F); // mini boss
                        deform(pose, phase(entity), entity, partialTick);
                    }
                });
        event.registerEntityRenderer(ModEntities.PARASITE_BURSTER.get(), ctx -> new ParasiteBeastRenderer<com.netheritesupremo.parasite.ParasiteBurster>(ctx,
                new ParasiteBeastModels.Mother<com.netheritesupremo.parasite.ParasiteBurster>(ctx.bakeLayer(ParasiteBeastModels.MOTHER)), 1.0F, ParasiteTextures.HYBRID_BURSTER, 0.8F) {
            @Override
            protected void scale(com.netheritesupremo.parasite.ParasiteBurster burster, PoseStack pose, float partialTick) {
                super.scale(burster, pose, partialTick);
                float f = burster.getSwelling(partialTick); // infla antes de estourar
                float g = 1.0F + net.minecraft.util.Mth.sin(f * 100.0F) * f * 0.01F;
                f = net.minecraft.util.Mth.clamp(f, 0.0F, 1.0F);
                f *= f;
                f *= f;
                pose.scale((1.0F + f * 0.5F) * g, (1.0F + f * 0.3F) / g, (1.0F + f * 0.5F) * g);
            }
        });
        event.registerEntityRenderer(ModEntities.PARASITE_CRAWLER.get(), ctx -> new ParasiteBeastRenderer<com.netheritesupremo.parasite.ParasiteCrawler>(ctx,
                new ParasiteBeastModels.Crawler<com.netheritesupremo.parasite.ParasiteCrawler>(ctx.bakeLayer(ParasiteBeastModels.CRAWLER)), 0.9F, ParasiteTextures.HYBRID_CRAWLER, 1.0F));
        event.registerEntityRenderer(ModEntities.PARASITE_HOUND.get(), ctx -> new ParasiteBeastRenderer<com.netheritesupremo.parasite.ParasiteHound>(ctx,
                new ParasiteBeastModels.Hound<com.netheritesupremo.parasite.ParasiteHound>(ctx.bakeLayer(ParasiteBeastModels.HOUND)), 0.5F, ParasiteTextures.HYBRID_HOUND, 1.0F) {
            @Override
            protected void scale(com.netheritesupremo.parasite.ParasiteHound hound, PoseStack pose, float partialTick) {
                super.scale(hound, pose, partialTick);
                float f = hound.getSwelling(partialTick); // infla antes de explodir, como o creeper
                float g = 1.0F + net.minecraft.util.Mth.sin(f * 100.0F) * f * 0.01F;
                f = net.minecraft.util.Mth.clamp(f, 0.0F, 1.0F);
                f *= f;
                f *= f;
                pose.scale((1.0F + f * 0.4F) * g, (1.0F + f * 0.1F) / g, (1.0F + f * 0.4F) * g);
            }
        });
        event.registerEntityRenderer(ModEntities.PARASITE_BROOD_MOTHER.get(), ctx -> new ParasiteBeastRenderer<com.netheritesupremo.parasite.ParasiteBroodMother>(ctx,
                new ParasiteBeastModels.Mother<com.netheritesupremo.parasite.ParasiteBroodMother>(ctx.bakeLayer(ParasiteBeastModels.MOTHER)), 1.6F, ParasiteTextures.HYBRID_MOTHER, 1.4F));
        event.<com.netheritesupremo.mob.Hunter>registerEntityRenderer(ModEntities.PARASITE_HUNTER.get(), ctx -> {
            PlayerRobotRenderer<com.netheritesupremo.mob.Hunter> r = new PlayerRobotRenderer<com.netheritesupremo.mob.Hunter>(ctx,
                    ParasiteTextures.HUNTER, 1.0F) {
                @Override
                protected void scale(com.netheritesupremo.mob.Hunter mob, PoseStack pose, float partialTick) {
                    super.scale(mob, pose, partialTick);
                    deform(pose, phase(mob), mob, partialTick);
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.HUMANOID);
            return r;
        });
        event.<ZombieVillager>registerEntityRenderer(ModEntities.PARASITE_VILLAGER.get(), ctx -> villagerRenderer(ctx, ParasiteTextures.VILLAGER, 1.0F));
        event.<ZombieVillager>registerEntityRenderer(ModEntities.PARASITE_WANDERING_TRADER.get(), ctx -> villagerRenderer(ctx, ParasiteTextures.TRADER, 1.15F));
        event.registerEntityRenderer(ModEntities.PARASITE_METEOR.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<com.netheritesupremo.parasite.ParasiteMeteor>(ctx, 4.0F, true));
        event.<Sheep>registerEntityRenderer(ModEntities.PARASITE_SHEEP.get(), ctx -> new SheepRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Sheep entity) {
                return ParasiteTextures.SHEEP;
            }

            @Override
            protected void scale(Sheep entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity), entity, partialTick);
            }
        });
        event.<Wolf>registerEntityRenderer(ModEntities.PARASITE_WOLF.get(), ctx -> new WolfRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Wolf entity) {
                return ParasiteTextures.WOLF;
            }

            @Override
            protected void scale(Wolf entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity), entity, partialTick);
            }
        });
        event.<Zombie>registerEntityRenderer(ModEntities.PARASITE_STRONGEST_ZOMBIE.get(), ctx -> {
            ZombieRenderer r = new ZombieRenderer(ctx) {
                @Override
                public ResourceLocation getTextureLocation(Zombie entity) {
                    return ParasiteTextures.STRONG_ZOMBIE;
                }

                @Override
                protected void scale(Zombie entity, PoseStack pose, float partialTick) {
                    pose.scale(1.5F, 1.5F, 1.5F);
                    deform(pose, phase(entity), entity, partialTick);
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.HUMANOID);
            return r;
        });
        event.<WitherBoss>registerEntityRenderer(ModEntities.PARASITE_WITHER.get(), ctx -> new WitherBossRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(WitherBoss entity) {
                return ParasiteTextures.WITHER;
            }

            @Override
            protected void scale(WitherBoss entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                deform(pose, phase(entity), entity, partialTick);
            }
        });
        event.<IronGolem>registerEntityRenderer(ModEntities.PARASITE_IRON_GOLEM.get(), ctx -> {
            IronGolemRenderer r = new IronGolemRenderer(ctx) {
                @Override
                public ResourceLocation getTextureLocation(IronGolem entity) {
                    return ParasiteTextures.IRON_GOLEM;
                }

                @Override
                protected void scale(IronGolem entity, PoseStack pose, float partialTick) {
                    super.scale(entity, pose, partialTick);
                    deform(pose, phase(entity), entity, partialTick);
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.GOLEM);
            return r;
        });
        event.<IronGolem>registerEntityRenderer(ModEntities.PARASITE_NETHERITE_GOLEM.get(), ctx -> {
            IronGolemRenderer r = new IronGolemRenderer(ctx) {
                @Override
                public ResourceLocation getTextureLocation(IronGolem entity) {
                    return ParasiteTextures.NETHERITE_GOLEM;
                }

                @Override
                protected void scale(IronGolem entity, PoseStack pose, float partialTick) {
                    pose.scale(1.25F, 1.25F, 1.25F);
                    deform(pose, phase(entity), entity, partialTick);
                }
            };
            spikes(r, ctx, ParasiteSpikesModel.GOLEM);
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
