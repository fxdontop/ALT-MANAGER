package com.altmanager.session;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * Permite cambiar el nombre de la sesión activa del cliente sin reiniciar Minecraft.
 *
 * <p>IMPORTANTE — alcance de esta utilidad: solo sirve para conectarte a servidores
 * en modo offline / no-premium (o LAN / mundos propios), porque el {@code accessToken}
 * que se genera aquí no es válido ante los servidores de autenticación de Mojang/Microsoft.
 * No inicia sesión con ninguna cuenta real ni maneja tokens de Microsoft/Xbox Live.
 * Un servidor en modo online (auténtico) seguirá rechazando esta sesión, tal como
 * rechazaría cualquier cliente "cracked".</p>
 */
public final class SessionUtil {

	private SessionUtil() {
	}

	/**
	 * Calcula el UUID que usan los servidores en modo offline a partir de un nombre,
	 * con el mismo algoritmo que usa el propio servidor vanilla ({@code OfflinePlayer:<nombre>}).
	 */
	public static UUID offlineUuid(String name) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	}

	/** Cambia la sesión activa del cliente al nombre indicado, sin reiniciar el juego. */
	public static void switchSession(String name) {
		UUID uuid = offlineUuid(name);
		User newUser = new User(name, uuid, "AltManager", Optional.empty(), Optional.empty());
		((MinecraftSessionAccessor) Minecraft.getInstance()).altmanager$setUser(newUser);
	}

	/** Nombre de la sesión actualmente activa en el cliente. */
	public static String getActiveUsername() {
		Minecraft client = Minecraft.getInstance();
		return client.getUser() != null ? client.getUser().getName() : "?";
	}
}
