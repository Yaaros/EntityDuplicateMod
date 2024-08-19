package com.yaaros.duplicate.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

import java.util.List;
import java.util.function.Predicate;

public class RayTraceUtils {

    public static EntityHitResult getEntityLookingAt(ServerPlayer player, double distance) {
        Vec3 startVec = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endVec = startVec.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
        AABB aabb = new AABB(startVec, endVec);

        List<Entity> entities = player.level().getEntities(player, aabb, entity -> !entity.isSpectator() && entity.isPickable());
        BlockHitResult blockHitResult = player.level().clip(new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double closestDistance = blockHitResult.getType() != HitResult.Type.MISS ? blockHitResult.getLocation().distanceTo(startVec) : distance;

        EntityHitResult closestEntityResult = null;

        for (Entity entity : entities) {
            AABB entityAABB = entity.getBoundingBox().inflate(entity.getPickRadius());
            Vec3 entityHitVec = entityAABB.clip(startVec, endVec).orElse(null);

            if (entityHitVec != null) {
                double distanceToEntity = startVec.distanceTo(entityHitVec);

                if (distanceToEntity < closestDistance) {
                    closestDistance = distanceToEntity;
                    closestEntityResult = new EntityHitResult(entity, entityHitVec);
                }
            }
        }
        return closestEntityResult;
    }

}
