package com.augustsextus.gunmod.entity;

import com.augustsextus.gunmod.EntitySpawnPacket;
import com.augustsextus.gunmod.GunModClient;
import com.augustsextus.gunmod.deathmessages.DamageSourceV2;
//import com.augustsextus.gunmod.items.Gun;
import com.augustsextus.gunmod.items.Gun;
import com.augustsextus.gunmod.mixin.TickMixin;
import com.augustsextus.gunmod.registry.EntityRegistry;
import com.augustsextus.gunmod.registry.ItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Shadow;

public class BulletEntity extends ThrownItemEntity {

    public BulletEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public BulletEntity(World world, LivingEntity owner) {
        super(EntityRegistry.BulletEntityType, owner, world);
    }

    public BulletEntity(World world, double x, double y, double z) {
        super(EntityRegistry.BulletEntityType, x, y, z, world);
    }

    /*@Override
    public void setNoGravity(boolean noGravity) {
        super.setNoGravity(noGravity = true);
    }*/

    public void spawnSmoke() {
        ParticleEffect particleEffect = this.getParticleParameters();
        this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), this.getX(), this.getY(), this.getZ());
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.BULLET;
    }

    @Environment(EnvType.CLIENT)
    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.CAMPFIRE_COSY_SMOKE : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    @Override
    public Packet createSpawnPacket() {
        return EntitySpawnPacket.create(this, GunModClient.PacketID);
    }

    @Override
    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        super.setVelocity(x, y, z, Gun.getBulletRangeSpeed(), 0);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)
        ((LivingEntity) entity).damage(DamageSourceV2.rifle_1("rifle", this.getOwner()), Gun.getDamage());

        if (entity instanceof LivingEntity) { // checks if entity is an instance of LivingEntity (meaning it is not a boat or minecart)
        }
    }

    protected void onCollision(HitResult hitResult) { // called on collision with a block
        super.onCollision(hitResult);
        if (!this.world.isClient) { // checks if the world is client
            this.world.sendEntityStatus(this, (byte)3); // particle?
            this.remove(); // kills the projectile
        }

    }
}


