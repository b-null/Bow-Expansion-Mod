package vvbj.modding.bowexpansion.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class CustomBowItem extends BowItem {

    private final double damage;
    private final int tps;

    public CustomBowItem(Settings settings, BowSettings properties) {
        super(settings);
        this.damage = properties.damage;
        this.tps = properties.ticks_per_second;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("bow-expansion.tooltip.added_damage", damage).formatted(Formatting.DARK_GREEN));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // Copied from vanilla bow class with some refactoring
        if (user instanceof PlayerEntity playerEntity) {
            boolean infinite = playerEntity.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemStack = playerEntity.getProjectileType(stack);
            if (!itemStack.isEmpty() || infinite) {
                if (itemStack.isEmpty()) {
                    itemStack = new ItemStack(Items.ARROW);
                }

                int i = this.getMaxUseTime(stack) - remainingUseTicks;
                float pullProgress = getPullProgress(i, this.tps);
                if (!((double)pullProgress < 0.1)) {
                    boolean infinity_w_arrow = infinite && itemStack.getItem() instanceof ArrowItem; // Any kind of arrow
                    if (!world.isClient) {
                        ArrowItem arrowItem = (ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, playerEntity);
                        persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, pullProgress * 3.0F, 1.0F);
                        if (pullProgress == 1.0F) {
                            persistentProjectileEntity.setCritical(true);
                        }

                        int power_level = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                        if (power_level > 0) {
                            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double)power_level * 0.5 + 0.5 + this.damage / 2); // Add bow's damage
                        }else
                            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + this.damage / 2); // Add bow's damage

                        int punch_level = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                        if (punch_level > 0) {
                            persistentProjectileEntity.setPunch(punch_level);
                        }

                        if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                            persistentProjectileEntity.setOnFireFor(100);
                        }// Todo - Add built-in flame for a fiery bow

                        stack.damage(1, playerEntity, (p) -> {
                            p.sendToolBreakStatus(playerEntity.getActiveHand());
                        });
                        if (infinity_w_arrow || playerEntity.getAbilities().creativeMode && (itemStack.isOf(Items.SPECTRAL_ARROW) || itemStack.isOf(Items.TIPPED_ARROW))) {
                            persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                        }

                        world.spawnEntity(persistentProjectileEntity);
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);
                    if (!infinity_w_arrow && !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            playerEntity.getInventory().removeOne(itemStack);
                        }
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    public static float getPullProgress(int useTicks, int tps) {
        float f = (float)useTicks / tps;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getTps() {
        return tps;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return ((stack) -> stack.getItem() instanceof ArrowItem); // Allow any kind of arrow... even modded ones
    }

    public static class BowSettings{
        private double damage = 0; // Vanilla bow doesn't have extra damage
        private int ticks_per_second = 20; // Default 20 - Reduce to make bow faster... I guess

        public BowSettings(){}

        public BowSettings bowDamage(double damage){
            this.damage = damage;
            return this;
        }

        public BowSettings tps(int ticks){
            this.ticks_per_second = ticks;
            return this;
        }
    }
}
