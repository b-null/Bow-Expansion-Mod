package vvbj.modding.bowexpansion;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import vvbj.modding.bowexpansion.item.CustomBowItem;
import vvbj.modding.bowexpansion.item.ModItems;

public class BowExpansionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerBowModelPredicate(ModItems.COPPER_BOW);
        registerBowModelPredicate(ModItems.IRON_BOW);
        registerBowModelPredicate(ModItems.GOLDEN_BOW);
        registerBowModelPredicate(ModItems.EMERALD_BOW);
        registerBowModelPredicate(ModItems.OBSIDIAN_BOW);
        registerBowModelPredicate(ModItems.DIAMOND_BOW);
        registerBowModelPredicate(ModItems.NETHERITE_BOW);
    }

    private void registerBowModelPredicate(CustomBowItem bow){
        ModelPredicateProviderRegistry.register(bow, new Identifier("pull"), ((stack, world, entity, seed) -> {
            if(entity == null)
                return 0;
            if(entity.getActiveItem() != stack)
                return 0;
            return (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / bow.getTps();
        }));

        ModelPredicateProviderRegistry.register(bow, new Identifier("pulling"),
                ((stack, world, entity, seed) -> entity != null &&entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0));
    }
}
