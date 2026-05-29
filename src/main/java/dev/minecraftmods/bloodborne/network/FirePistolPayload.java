package dev.minecraftmods.bloodborne.network;

import dev.minecraftmods.bloodborne.BloodborneMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FirePistolPayload() implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<FirePistolPayload> TYPE =
			new CustomPacketPayload.Type<>(BloodborneMod.id("fire_pistol"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FirePistolPayload> CODEC =
			StreamCodec.unit(new FirePistolPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
