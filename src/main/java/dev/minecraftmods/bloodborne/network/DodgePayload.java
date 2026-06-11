package dev.minecraftmods.bloodborne.network;

import dev.minecraftmods.bloodborne.BloodborneMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DodgePayload(float yaw, float forward, float strafe) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DodgePayload> TYPE =
			new CustomPacketPayload.Type<>(BloodborneMod.id("dodge"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DodgePayload> CODEC =
			StreamCodec.composite(
					ByteBufCodecs.FLOAT, DodgePayload::yaw,
					ByteBufCodecs.FLOAT, DodgePayload::forward,
					ByteBufCodecs.FLOAT, DodgePayload::strafe,
					DodgePayload::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
