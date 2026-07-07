package com.altmanager;

import com.altmanager.data.ProfileManager;
import net.fabricmc.api.ClientModInitializer;

/**
 * Punto de entrada del cliente. Carga los perfiles guardados al iniciar el juego.
 */
public class AltManagerClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ProfileManager.get().load();
		System.out.println("[AltManager] Mod inicializado. Perfiles cargados: "
				+ ProfileManager.get().getProfiles().size());
	}
}
