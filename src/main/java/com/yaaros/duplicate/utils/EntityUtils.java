package com.yaaros.duplicate.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.village.VillagerTradesEvent;

import java.util.List;

public class EntityUtils {

    public static void copyTargetedEntity(ServerPlayer player,int distance) {
        if (!player.isSpectator()) {
            EntityHitResult entityResult = RayTraceUtils.getEntityLookingAt(player, distance);
            if (entityResult != null) {
                Entity targetEntity = entityResult.getEntity();
                Level level = targetEntity.level();
                if (checkGlowingEnd(targetEntity) && level instanceof ServerLevel && targetEntity instanceof LivingEntity) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    EntityType<?> T = convertLivingEntityToEntityType((LivingEntity) targetEntity);
                    if (T != null) {
                        LivingEntity newEntity = (LivingEntity) T.create(serverLevel);
                        if (newEntity != null) {
                            newEntity.moveTo(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), targetEntity.getYRot(), targetEntity.getXRot());
                            LivingEntity targetLivingEntity = (LivingEntity) targetEntity;
                            for (EquipmentSlot slot : EquipmentSlot.values()) {
                                ItemStack stack = targetLivingEntity.getItemBySlot(slot);
                                if (!stack.isEmpty()) {
                                    newEntity.setItemSlot(slot, stack.copy());
                                }
                            }
                            copyBabyStatus(targetLivingEntity,newEntity);//isBaby
                            newEntity.setHealth(targetLivingEntity.getHealth());//HP
                            // 处理羊的颜色
                            if (targetLivingEntity instanceof Sheep) {
                                DyeColor woolColor = ((Sheep) targetLivingEntity).getColor();
                                ((Sheep) newEntity).setColor(woolColor);
                            }
                            if (newEntity.isAlive()) {
                                serverLevel.addFreshEntity(newEntity);
                                newEntity.addEffect(new MobEffectInstance(MobEffect.byId(24), 100, 1, true, false,false));
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean checkGlowingEnd(Entity e){
        return !e.hasGlowingTag();
    }
    public static final List<EntityType<?>> TYPES = List.of(EntityType.ALLAY, EntityType.AXOLOTL, EntityType.BAT, EntityType.BEE, EntityType.BLAZE, EntityType.CAMEL, EntityType.CAT, EntityType.CAVE_SPIDER,EntityType.CHICKEN, EntityType.COD, EntityType.COW, EntityType.CREEPER, EntityType.DOLPHIN, EntityType.DONKEY, EntityType.DROWNED,    EntityType.ELDER_GUARDIAN, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.FIREWORK_ROCKET, EntityType.FOX, EntityType.FROG, EntityType.FURNACE_MINECART, EntityType.GHAST, EntityType.GIANT,EntityType.GLOW_SQUID, EntityType.GOAT, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HORSE, EntityType.HUSK, EntityType.ILLUSIONER, EntityType.IRON_GOLEM, EntityType.LLAMA, EntityType.MOOSHROOM, EntityType.MULE, EntityType.OCELOT, EntityType.PANDA, EntityType.PARROT, EntityType.PHANTOM, EntityType.PIG, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.POLAR_BEAR,EntityType.PUFFERFISH, EntityType.RABBIT, EntityType.RAVAGER, EntityType.SALMON, EntityType.SHEEP, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.SNIFFER, EntityType.SNOW_GOLEM, EntityType.SPIDER, EntityType.SQUID, EntityType.STRAY, EntityType.STRIDER,EntityType.TRADER_LLAMA,  EntityType.TROPICAL_FISH, EntityType.TURTLE, EntityType.VEX, EntityType.VILLAGER, EntityType.VINDICATOR, EntityType.WANDERING_TRADER, EntityType.WARDEN, EntityType.WITCH, EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.WITHER_SKULL, EntityType.WOLF, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN);


    public static EntityType<?> convertLivingEntityToEntityType(LivingEntity livingEntity) {
        Class<? extends LivingEntity> entityClass = livingEntity.getClass();

        for (EntityType<?> entityType : TYPES) {
            if (entityType.create(livingEntity.level()) != null && entityType.create(livingEntity.level()).getClass().equals(entityClass)) {
                return entityType;
            }
        }

        return null; // 没有找到匹配的 EntityType
    }
    private static void copyBabyStatus(LivingEntity targetEntity, LivingEntity newEntity) {
        if (targetEntity instanceof Zombie targetZombie && newEntity instanceof Zombie) {
            if (targetZombie.isBaby()) {
                ((Zombie) newEntity).setBaby(true);
            }
        }
        else if (targetEntity instanceof AgeableMob targetAgeable && newEntity instanceof AgeableMob) {
            if (targetAgeable.isBaby()) {
                ((AgeableMob) newEntity).setBaby(true);
            }
        }
    }

}
