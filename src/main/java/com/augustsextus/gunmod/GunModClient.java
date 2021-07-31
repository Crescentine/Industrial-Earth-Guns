package com.augustsextus.gunmod;

/*import com.augustsextus.gunmod.client.BulletEntityRenderer;
import com.augustsextus.gunmod.entity.BulletEntity;*/
import com.augustsextus.gunmod.items.Gun;
import com.augustsextus.gunmod.mixin.TickMixin;
import com.augustsextus.gunmod.registry.EntityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class GunModClient implements ClientModInitializer {

    public static final Identifier PacketID = new Identifier(GunMod.MOD_ID, "spawn_packet");

    private static Boolean currentlyZoomed;
    private static Boolean originalSmoothCameraEnabled;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static double zoomLevel = 0;

    @Override
    public void onInitializeClient() {

        //Entity packet / render
        EntityRendererRegistry.INSTANCE.register(EntityRegistry.BulletEntityType, (dispatcher, context) ->
                new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer()));
        receiveEntityPacket();

        currentlyZoomed = false;
        originalSmoothCameraEnabled = false;

    }

    public void receiveEntityPacket() {
        ClientSidePacketRegistry.INSTANCE.register(PacketID, (ctx, byteBuf) -> {
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = EntitySpawnPacket2.PacketBufUtil.readVec3d(byteBuf);
            float pitch = EntitySpawnPacket2.PacketBufUtil.readAngle(byteBuf);
            float yaw = EntitySpawnPacket2.PacketBufUtil.readAngle(byteBuf);
            ctx.getTaskQueue().execute(() -> {
                if (MinecraftClient.getInstance().world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world!");
                Entity e = et.create(MinecraftClient.getInstance().world);
                if (e == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
                e.updateTrackedPosition(pos);
                e.setPos(pos.x, pos.y, pos.z);
                e.pitch = pitch;
                e.yaw = yaw;
                e.setEntityId(entityId);
                e.setUuid(uuid);
                MinecraftClient.getInstance().world.addEntity(entityId, e);
            });
        });
    }

    public static Boolean isZooming() {
        return Gun.getIsScoping();
    }

    public static void manageSmoothCamera() {
        if (zoomStarting()) {
            zoomStarted();
            enableSmoothCamera();
        }

        if (zoomStopping()) {
            zoomStopped();
            resetSmoothCamera();
        }
    }

    private static Boolean isSmoothCamera() {
        return mc.options.smoothCameraEnabled;
    }

    private static void enableSmoothCamera() {
        mc.options.smoothCameraEnabled = true;
    }

    private static void disableSmoothCamera() {
        mc.options.smoothCameraEnabled = false;
    }

    private static boolean zoomStarting() {
        return isZooming() && !currentlyZoomed;
    }

    private static boolean zoomStopping() {
        return !isZooming() && currentlyZoomed;
    }

    private static void zoomStarted() {
        originalSmoothCameraEnabled = isSmoothCamera();
        currentlyZoomed = true;
    }

    private static void zoomStopped() {
        currentlyZoomed = false;
    }

    private static void resetSmoothCamera() {
        if (originalSmoothCameraEnabled) {
            enableSmoothCamera();
        } else {
            disableSmoothCamera();
        }
    }
}


