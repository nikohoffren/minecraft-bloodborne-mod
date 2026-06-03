package dev.minecraftmods.bloodborne.stamina;

public interface StaminaAccess {

	float bloodborne$getStamina();

	float bloodborne$getMaxStamina();

	void bloodborne$setStamina(float value);

	boolean bloodborne$tryConsume(float amount);
}
