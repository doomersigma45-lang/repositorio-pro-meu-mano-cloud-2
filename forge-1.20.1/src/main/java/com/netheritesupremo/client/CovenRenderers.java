package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.netheritesupremo.coven.Broom;
import com.netheritesupremo.coven.Coven;
import com.netheritesupremo.coven.CovenDog;
import com.netheritesupremo.coven.CovenBird;
import com.netheritesupremo.coven.CovenMob;
import com.netheritesupremo.parasite.ParasiteGnat;
import com.netheritesupremo.parasite.ParasiteHead;
import com.netheritesupremo.parasite.ParasiteRooter;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;

/** Renderers das criaturas do covil e dos novos parasitas (modelos do jogo recoloridos + modelos proprios). */
public final class CovenRenderers {
    private CovenRenderers() {
    }

    private static ResourceLocation textureOf(CovenMob.Kind kind) {
        return switch (kind) {
            case IMP -> CovenTextures.IMP;
            case WEREWOLF -> CovenTextures.WEREWOLF;
            case SHADOW_PERSON -> CovenTextures.SHADOW_PERSON;
            case GHOST -> CovenTextures.GHOST;
            case DEMON -> CovenTextures.DEMON;
            case CAMBION -> CovenTextures.CAMBION;
            case SNAKE -> CovenTextures.SNAKE;
        };
    }

    private static void humanoid(EntityRenderersEvent.RegisterRenderers event,
            net.minecraftforge.registries.RegistryObject<net.minecraft.world.entity.EntityType<CovenMob>> type) {
        event.registerEntityRenderer(type.get(), ctx -> new HumanoidMobRenderer<CovenMob, HumanoidModel<CovenMob>>(ctx,
                new HumanoidModel<CovenMob>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F) {
            @Override
            public ResourceLocation getTextureLocation(CovenMob entity) {
                return textureOf(entity.kind());
            }

            @Override
            protected void scale(CovenMob entity, PoseStack pose, float partialTick) {
                float s = entity.kind().scale;
                pose.scale(s, s, s);
            }

            @Nullable
            @Override
            protected RenderType getRenderType(CovenMob entity, boolean visible, boolean translucent, boolean glowing) {
                if (entity.kind() == CovenMob.Kind.GHOST) {
                    return RenderType.entityTranslucent(this.getTextureLocation(entity));
                }
                return super.getRenderType(entity, visible, translucent, glowing);
            }
        });
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        humanoid(event, Coven.IMP);
        humanoid(event, Coven.WEREWOLF);
        humanoid(event, Coven.SHADOW_PERSON);
        humanoid(event, Coven.GHOST);
        humanoid(event, Coven.DEMON);
        humanoid(event, Coven.CAMBION);

        event.registerEntityRenderer(Coven.SNAKE.get(), ctx -> new MobRenderer<CovenMob, SilverfishModel<CovenMob>>(ctx,
                new SilverfishModel<CovenMob>(ctx.bakeLayer(ModelLayers.SILVERFISH)), 0.3F) {
            @Override
            public ResourceLocation getTextureLocation(CovenMob entity) {
                return CovenTextures.SNAKE;
            }

            @Override
            protected void scale(CovenMob entity, PoseStack pose, float partialTick) {
                pose.scale(1.5F, 1.0F, 1.5F);
            }
        });

        event.<Wolf>registerEntityRenderer(Coven.BLACK_DOG.get(), ctx -> new WolfRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Wolf entity) {
                return CovenTextures.BLACK_DOG;
            }
        });
        event.<Wolf>registerEntityRenderer(Coven.HELLHOUND.get(), ctx -> new WolfRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Wolf entity) {
                return CovenTextures.HELLHOUND;
            }

            @Override
            protected void scale(Wolf entity, PoseStack pose, float partialTick) {
                pose.scale(1.15F, 1.15F, 1.15F);
            }
        });
        event.<Parrot>registerEntityRenderer(Coven.RAVEN.get(), ctx -> new ParrotRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Parrot entity) {
                return CovenTextures.RAVEN;
            }
        });
        event.<Parrot>registerEntityRenderer(Coven.OWL.get(), ctx -> new ParrotRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Parrot entity) {
                return CovenTextures.OWL;
            }

            @Override
            protected void scale(Parrot entity, PoseStack pose, float partialTick) {
                pose.scale(1.3F, 1.3F, 1.3F);
            }
        });
        event.<Frog>registerEntityRenderer(Coven.TOAD.get(), ctx -> new FrogRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Frog entity) {
                return CovenTextures.TOAD;
            }

            @Override
            protected void scale(Frog entity, PoseStack pose, float partialTick) {
                pose.scale(1.2F, 1.2F, 1.2F);
            }
        });

        event.<Blaze>registerEntityRenderer(Coven.PARASITE_BLAZE.get(), ctx -> new BlazeRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Blaze entity) {
                return CovenTextures.PARASITE_BLAZE;
            }
        });
        event.<Phantom>registerEntityRenderer(Coven.PARASITE_BOMBER.get(), ctx -> new PhantomRenderer(ctx) {
            @Override
            public ResourceLocation getTextureLocation(Phantom entity) {
                return CovenTextures.PARASITE_BOMBER;
            }

            @Override
            protected void scale(Phantom entity, PoseStack pose, float partialTick) {
                super.scale(entity, pose, partialTick);
                pose.scale(1.3F, 1.3F, 1.3F);
            }
        });
        event.registerEntityRenderer(Coven.PARASITE_HEAD.get(), ctx -> new MobRenderer<ParasiteHead, CovenModels.Head<ParasiteHead>>(ctx,
                new CovenModels.Head<ParasiteHead>(ctx.bakeLayer(CovenModels.HEAD)), 0.4F) {
            @Override
            public ResourceLocation getTextureLocation(ParasiteHead entity) {
                return CovenTextures.PARASITE_HEAD;
            }
        });
        event.registerEntityRenderer(Coven.PARASITE_GNAT.get(), ctx -> new MobRenderer<ParasiteGnat, CovenModels.Gnat<ParasiteGnat>>(ctx,
                new CovenModels.Gnat<ParasiteGnat>(ctx.bakeLayer(CovenModels.GNAT)), 0.15F) {
            @Override
            public ResourceLocation getTextureLocation(ParasiteGnat entity) {
                return CovenTextures.PARASITE_GNAT;
            }
        });
        event.registerEntityRenderer(Coven.PARASITE_ROOTER.get(), ctx -> new MobRenderer<ParasiteRooter, CovenModels.Rooter<ParasiteRooter>>(ctx,
                new CovenModels.Rooter<ParasiteRooter>(ctx.bakeLayer(CovenModels.ROOTER)), 0.8F) {
            @Override
            public ResourceLocation getTextureLocation(ParasiteRooter entity) {
                return CovenTextures.PARASITE_ROOTER;
            }
        });
        event.registerEntityRenderer(Coven.PARASITE_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Coven.BROOM.get(), BroomRenderer::new);
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CovenModels.HEAD, CovenModels.Head::createLayer);
        event.registerLayerDefinition(CovenModels.GNAT, CovenModels.Gnat::createLayer);
        event.registerLayerDefinition(CovenModels.ROOTER, CovenModels.Rooter::createLayer);
    }

    /** Vassoura: desenha o sprite do item deitado, na direcao em que ela aponta. */
    public static class BroomRenderer extends EntityRenderer<Broom> {
        public BroomRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(Broom entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int light) {
            pose.pushPose();
            pose.translate(0.0D, 0.12D, 0.0D);
            pose.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
            pose.mulPose(Axis.XP.rotationDegrees(90.0F));
            pose.mulPose(Axis.ZP.rotationDegrees(45.0F));
            pose.scale(2.4F, 2.4F, 2.4F);
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Coven.BROOM_ITEM.get()),
                    ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffer, entity.level(), entity.getId());
            pose.popPose();
            super.render(entity, entityYaw, partialTick, pose, buffer, light);
        }

        @Override
        public ResourceLocation getTextureLocation(Broom entity) {
            return InventoryMenu.BLOCK_ATLAS;
        }
    }
}
