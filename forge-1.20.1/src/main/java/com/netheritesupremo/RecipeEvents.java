package com.netheritesupremo;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tira do jogo a receita original da barra de netherite (4 fragmentos + 4 barras de ouro). Os arquivos de dados
 * de um mod nao conseguem apagar uma receita do jogo base, entao ela e removida aqui depois de cada recarga.
 * A nossa receita (obsidiana + estrela do Nether) fica em netheritesupremo:netherite_ingot.
 */
public class RecipeEvents {
    private static final Logger LOGGER = LoggerFactory.getLogger("NetheriteSupremo");

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        RecipeManager manager = event.getServerResources().getRecipeManager();
        event.addListener((ResourceManagerReloadListener) (ResourceManager resourceManager) -> strip(manager, "recarga"));
    }

    /** Garante a remocao no inicio do servidor, depois que todas as receitas ja carregaram. */
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        strip(event.getServer().getRecipeManager(), "inicio");
    }

    private static void strip(RecipeManager manager, String when) {
        List<Recipe<?>> kept = new ArrayList<>();
        int removed = 0;
        int total = 0;
        for (Recipe<?> recipe : manager.getRecipes()) {
            total++;
            try {
                if (recipe.getResultItem(RegistryAccess.EMPTY).is(Items.NETHERITE_INGOT)) {
                    LOGGER.info("DIAG netherite_ingot: {} {} {}", recipe.getId(), recipe.getClass().getSimpleName(),
                            recipe.getIngredients().size());
                }
            } catch (RuntimeException ignored) {
                // algumas receitas (ex.: ornamentos) nao aceitam RegistryAccess vazio
            }
            if (isVanillaGoldNetheriteIngot(recipe)) {
                removed++;
            } else {
                kept.add(recipe);
            }
        }
        if (removed > 0) {
            manager.replaceRecipes(kept);
        }
        LOGGER.info("receita original da netherite removida ({}): {} de {} receitas", when, removed, total);
    }

    private static boolean isVanillaGoldNetheriteIngot(Recipe<?> recipe) {
        if (!(recipe instanceof ShapelessRecipe) || !"minecraft".equals(recipe.getId().getNamespace())
                || !recipe.getResultItem(RegistryAccess.EMPTY).is(Items.NETHERITE_INGOT)) {
            return false;
        }
        ItemStack gold = new ItemStack(Items.GOLD_INGOT);
        return recipe.getIngredients().stream().anyMatch(ing -> ing.test(gold));
    }
}
