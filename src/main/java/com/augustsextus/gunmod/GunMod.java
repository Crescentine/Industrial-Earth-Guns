package com.augustsextus.gunmod;

//import com.augustsextus.gunmod.items.Gun;
import com.augustsextus.gunmod.entity.BulletEntity;
import com.augustsextus.gunmod.items.Bullet;
import com.augustsextus.gunmod.items.Gun;
import com.augustsextus.gunmod.mixin.BulletEntityInvoker;
import com.augustsextus.gunmod.registry.EntityRegistry;
import com.augustsextus.gunmod.registry.ItemRegistry;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Set;

public class GunMod implements ModInitializer, BulletEntityInvoker {

    public static final String MOD_ID = "gun";

    private static final Set<Item> GUNS = ImmutableSet.of(ItemRegistry.RIFLE_1, ItemRegistry.RIFLE_2, ItemRegistry.RIFLE_3, ItemRegistry.HANDGUN_1, ItemRegistry.HANDGUN_2, ItemRegistry.HANDGUN_3);
    private static final Set<Item> RIFLES = ImmutableSet.of(ItemRegistry.RIFLE_1, ItemRegistry.RIFLE_2, ItemRegistry.RIFLE_3);

    LivingEntity user;
    World world;

    private static final Identifier SHOOT_PACKET = new Identifier("shoot_packet");
    private static final Identifier SMOKE_SPAWN_PACKET = new Identifier("shoot_spawn_packet");

    public static Identifier getShootPacket() {
        return SHOOT_PACKET;
    }
    public static Identifier getSmokeSpawnPacket() {
        return SHOOT_PACKET;
    }

    @Override
    public void onInitialize() {
        ItemRegistry.registerItems();
        EntityRegistry.registerEntities();

        ServerPlayNetworking.registerGlobalReceiver(SHOOT_PACKET, (server, user, handler, buf, responseSender) -> server.execute(() -> {
            Gun.shoot(user.world, user);
        }));

        ServerPlayNetworking.registerGlobalReceiver(SMOKE_SPAWN_PACKET, (server, user, handler, buf, responseSender) -> server.execute(() -> {
            BulletEntity bulletEntity = new BulletEntity(this.world, user);
            bulletEntity.spawnSmoke();
        }));

    }

    public static Set<Item> getGuns() {
        return GUNS;
    }

    @Override
    public void invokeSpawnSmoke() {

    }
}
