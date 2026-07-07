package com.altmanager.mixin;

import com.altmanager.session.MinecraftSessionAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Quita el "final" del campo {@code user} de {@link Minecraft} para poder
 * reemplazar la sesión activa en tiempo de ejecución (ver {@link com.altmanager.session.SessionUtil}).
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftSessionAccessor {

	@Mutable
	@Shadow
	@Final
	private User user;

	@Override
	public void altmanager$setUser(User user) {
		this.user = user;
	}
}
