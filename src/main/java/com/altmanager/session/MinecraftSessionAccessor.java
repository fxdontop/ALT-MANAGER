package com.altmanager.session;

import net.minecraft.client.User;

/**
 * Implementada por {@code MinecraftMixin} para poder reemplazar la sesión
 * (usuario/nombre) activa del cliente en caliente, sin reiniciar el juego.
 */
public interface MinecraftSessionAccessor {
	void altmanager$setUser(User user);
}
