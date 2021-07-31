package com.augustsextus.gunmod;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("deprecation")
public class EntitySpawnPacket2 {

        public static final Identifier ID = new Identifier(GunMod.MOD_ID, "spawn_entity");

        public static Packet<?> createPacket(Entity e, Identifier packetID) {
            PacketByteBuf buf = createBuffer();
            buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(e.getType()));
            buf.writeUuid(e.getUuid());
            buf.writeVarInt(e.getEntityId());
            buf.writeDouble(e.getX());
            buf.writeDouble(e.getY());
            buf.writeDouble(e.getZ());
            buf.writeByte(MathHelper.floor(e.pitch * 256.0F / 360.0F));
            buf.writeByte(MathHelper.floor(e.yaw * 256.0F / 360.0F));
            buf.writeFloat(e.pitch);
            buf.writeFloat(e.yaw);
            EntitySpawnPacket2.PacketBufUtil.writeVec3d(buf, e.getPos());
            EntitySpawnPacket2.PacketBufUtil.writeAngle(buf, e.pitch);
            EntitySpawnPacket2.PacketBufUtil.writeAngle(buf, e.yaw);
            return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
        }

        private static PacketByteBuf createBuffer() {
            return new PacketByteBuf(Unpooled.buffer());
        }
         public static final class PacketBufUtil {

                public static byte packAngle(float angle) {
                    return (byte) MathHelper.floor(angle * 256 / 360);
                }

                public static float unpackAngle(byte angleByte) {
                    return (angleByte * 360) / 256f;
                }

                public static void writeAngle(PacketByteBuf byteBuf, float angle) {
                    byteBuf.writeByte(packAngle(angle));
                }
                public static float readAngle(PacketByteBuf byteBuf) {
                    return unpackAngle(byteBuf.readByte());
                }

                public static void writeVec3d(PacketByteBuf byteBuf, Vec3d vec3d) {
                    byteBuf.writeDouble(vec3d.x);
                    byteBuf.writeDouble(vec3d.y);
                    byteBuf.writeDouble(vec3d.z);
                }
                public static Vec3d readVec3d(PacketByteBuf byteBuf) {
                    double x = byteBuf.readDouble();
                    double y = byteBuf.readDouble();
                    double z = byteBuf.readDouble();
                    return new Vec3d(x, y, z);
                }
            }
        }
