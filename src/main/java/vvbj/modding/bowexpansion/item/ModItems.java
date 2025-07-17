package vvbj.modding.bowexpansion.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import vvbj.modding.bowexpansion.BowExpansion;


public class ModItems {

    public static final CustomBowItem COPPER_BOW = new CustomBowItem(new Item.Settings().maxDamage(384), new CustomBowItem.BowSettings()
            .bowDamage(1.5));
    public static final CustomBowItem IRON_BOW = new CustomBowItem(new Item.Settings().maxDamage(432), new CustomBowItem.BowSettings()
            .bowDamage(2.5));
    public static final CustomBowItem GOLDEN_BOW = new CustomBowItem(new Item.Settings().maxDamage(86), new CustomBowItem.BowSettings()
            .bowDamage(1.5)
            .tps(8));
    public static final CustomBowItem EMERALD_BOW = new CustomBowItem(new Item.Settings().maxDamage(432), new CustomBowItem.BowSettings()
            .bowDamage(3.5));
    public static final CustomBowItem OBSIDIAN_BOW = new CustomBowItem(new Item.Settings().maxDamage(512), new CustomBowItem.BowSettings()
            .bowDamage(4));
    public static final CustomBowItem DIAMOND_BOW = new CustomBowItem(new Item.Settings().maxDamage(896), new CustomBowItem.BowSettings()
            .bowDamage(5));
    public static final CustomBowItem NETHERITE_BOW = new CustomBowItem(new Item.Settings().maxDamage(1072), new CustomBowItem.BowSettings()
            .bowDamage(6));

    public static void register(){
        Registry.register(Registries.ITEM, Identifier.of(BowExpansion.MOD_ID, "copper_bow"), COPPER_BOW);
        Registry.register(Registries.ITEM, Identifier.of(BowExpansion.MOD_ID, "iron_bow"), IRON_BOW);
        Registry.register(Registries.ITEM, Identifier.of(BowExpansion.MOD_ID, "golden_bow"), GOLDEN_BOW);
        Registry.register(Registries.ITEM, Identifier.of(BowExpansion.MOD_ID, "emerald_bow"), EMERALD_BOW);
        Registry.register(Registries.ITEM, Identifier.of(BowExpansion.MOD_ID, "obsidian_bow"), OBSIDIAN_BOW);
        Registry.register(Registries.ITEM, Identifier.of(BowExpansion.MOD_ID, "diamond_bow"), DIAMOND_BOW);
        Registry.register(Registries.ITEM, Identifier.of(BowExpansion.MOD_ID, "netherite_bow"), NETHERITE_BOW);

        // Add to ItemGroup (weapons)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(COPPER_BOW);
            entries.add(GOLDEN_BOW);
            entries.add(IRON_BOW);
            entries.add(EMERALD_BOW);
            entries.add(OBSIDIAN_BOW);
            entries.add(DIAMOND_BOW);
            entries.add(NETHERITE_BOW);
        });
    }
}
