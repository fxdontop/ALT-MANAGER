package com.altmanager.data;

import java.util.UUID;

/**
 * Representa un perfil guardado por el usuario.
 * Solo almacena un identificador y un nombre visible: no maneja
 * credenciales, tokens de sesión ni ningún dato de autenticación.
 */
public class Profile {

	private final String id;
	private String name;

	public Profile(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	public Profile(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
