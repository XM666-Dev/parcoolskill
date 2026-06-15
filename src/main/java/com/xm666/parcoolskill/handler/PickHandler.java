package com.xm666.parcoolskill.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;

public class PickHandler {
    public static double getEntityPickRange(Entity shooter, double distance) {
        var mc = Minecraft.getInstance();
        var timer = mc.getTimer();
        var partialTick = timer.getGameTimeDeltaPartialTick(true);
        var hitResult = shooter.pick(distance, partialTick, false);
        if (hitResult.getType() != HitResult.Type.MISS) {
            var eyePosition = shooter.getEyePosition(partialTick);
            var pickPosition = hitResult.getLocation();
            return eyePosition.distanceTo(pickPosition);
        }
        return distance;
    }

    public static Entity[] getHitEntities(Entity shooter, double distance, double inflationAmount, long limit) {
        var mc = Minecraft.getInstance();
        var timer = mc.getTimer();
        var partialTick = timer.getGameTimeDeltaPartialTick(true);
        var eyePosition = shooter.getEyePosition();
        var viewVector = shooter.getViewVector(partialTick);
        var pickVector = viewVector.scale(distance);
        var pickPosition = eyePosition.add(pickVector);
        var boundingBox = shooter.getBoundingBox().expandTowards(pickVector).inflate(1.0);
        return getHitEntities(
                shooter,
                eyePosition,
                pickPosition,
                boundingBox,
                (entity) -> !entity.isSpectator() && entity.isPickable(),
                inflationAmount,
                limit
        );
    }

    private static Entity[] getHitEntities(Entity shooter, Vec3 startPosition, Vec3 endPosition, AABB boundingBox, Predicate<Entity> filter, double inflationAmount, long limit) {
        record HitResult(Entity entity, double distanceSquared) {
        }

        var level = shooter.level();
        var hitResults = new ArrayList<HitResult>();
        for (var entity : level.getEntities(shooter, boundingBox, filter)) {
            var aabb = entity.getBoundingBox().inflate(entity.getPickRadius() + inflationAmount);
            var optionalPoint = aabb.clip(startPosition, endPosition);
            double distanceSquare;
            if (aabb.contains(startPosition)) {
                distanceSquare = 0.0;
            } else if (optionalPoint.isPresent()) {
                var point = optionalPoint.get();
                distanceSquare = startPosition.distanceToSqr(point);
            } else {
                continue;
            }
            hitResults.add(new HitResult(entity, distanceSquare));
        }

        return hitResults.stream()
                .sorted(Comparator.comparingDouble(HitResult::distanceSquared))
                .limit(limit)
                .map(HitResult::entity)
                .toArray(Entity[]::new);
    }
}
