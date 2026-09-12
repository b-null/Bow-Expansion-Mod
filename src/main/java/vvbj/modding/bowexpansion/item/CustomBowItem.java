package vvbj.modding.bowexpansion.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

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
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("bow-expansion.tooltip.added_damage", damage).formatted(Formatting.DARK_GREEN));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        ItemStack projectile = player.getProjectileType(stack);
        if (projectile.isEmpty()) {
            return;
        }

        int useTicks = getMaxUseTime(stack, user) - remainingUseTicks;
        float pullProgress = getPullProgress(useTicks, tps);
        if (pullProgress < 0.1F) {
            return;
        }

        List<ItemStack> projectiles = load(stack, projectile, player);
        if (world instanceof ServerWorld serverWorld && !projectiles.isEmpty()) {
            shootAll(serverWorld, player, player.getActiveHand(), stack, projectiles,
                    pullProgress * 3.0F, 1.0F, pullProgress == 1.0F, null);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS, 1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed,
                         float divergence, float yaw, LivingEntity target) {
        super.shoot(shooter, projectile, index, speed, divergence, yaw, target);
        if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
            persistentProjectile.setDamage(persistentProjectile.getDamage() + damage / 2.0);
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
        return stack -> stack.getItem() instanceof ArrowItem;
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
