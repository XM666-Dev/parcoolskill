package com.xm666.parcoolskill.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PickHandler {
    public static double getHitRange(Entity shooter, double distance) {
        var mc = Minecraft.getInstance();
        var timer = mc.getDeltaTracker();
        var partialTick = timer.getGameTimeDeltaPartialTick(true);
        var hitResult = shooter.pick(distance, partialTick, false);
        if (hitResult.getType() != HitResult.Type.MISS) {
            var eyePosition = shooter.getEyePosition(partialTick);
            var hitPosition = hitResult.getLocation();
            return eyePosition.distanceTo(hitPosition);
        }
        return distance;
    }

    public static Stream<Entity> getHitEntities(Entity shooter, double distance, int count) {
        var mc = Minecraft.getInstance();
        var timer = mc.getDeltaTracker();
        var partialTick = timer.getGameTimeDeltaPartialTick(true);
        var eyePosition = shooter.getEyePosition();
        var viewVector = shooter.getViewVector(partialTick);
        var hitVector = viewVector.scale(distance);
        var hitPosition = eyePosition.add(hitVector);
        var boundingBox = shooter.getBoundingBox().expandTowards(hitVector).inflate(1.0);
        return getHitEntities(
                shooter,
                eyePosition,
                hitPosition,
                boundingBox,
                (entity) -> !entity.isSpectator() && entity.isPickable(),
                count
        );
    }

    private static Stream<Entity> getHitEntities(Entity shooter, Vec3 startPosition, Vec3 endPosition, AABB boundingBox, Predicate<Entity> filter, int count) {
        record HitResult(Entity entity, double distanceSquared) {
        }

        var level = shooter.level();
        var hitResults = new ArrayList<HitResult>();
        for (var entity : level.getEntities(shooter, boundingBox, filter)) {
            var entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            var optionalPoint = entityBoundingBox.clip(startPosition, endPosition);
            double distanceSquare;
            if (entityBoundingBox.contains(startPosition)) {
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
                .limit(count)
                .map(HitResult::entity);
    }
}
