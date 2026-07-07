package com.altmanager.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Administra la lista de perfiles guardados y su persistencia en disco.
 * El archivo se guarda en: .minecraft/config/altmanager/profiles.json
 */
public final class ProfileManager {

	private static final ProfileManager INSTANCE = new ProfileManager();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type LIST_TYPE = new TypeToken<ArrayList<Profile>>() {}.getType();

	private final List<Profile> profiles = new ArrayList<>();
	private String selectedProfileId = null;
	private boolean loaded = false;

	private ProfileManager() {
	}

	public static ProfileManager get() {
		return INSTANCE;
	}

	private Path getConfigDir() {
		Path dir = FabricLoader.getInstance().getConfigDir().resolve("altmanager");
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			throw new RuntimeException("No se pudo crear la carpeta de configuración de Alt Manager", e);
		}
		return dir;
	}

	private Path getProfilesFile() {
		return getConfigDir().resolve("profiles.json");
	}

	private Path getStateFile() {
		return getConfigDir().resolve("state.json");
	}

	/** Carga los perfiles desde disco. Se puede llamar varias veces; solo carga una vez. */
	public synchronized void load() {
		if (loaded) {
			return;
		}
		loaded = true;
		profiles.clear();

		Path profilesFile = getProfilesFile();
		if (Files.exists(profilesFile)) {
			try (Reader reader = Files.newBufferedReader(profilesFile, StandardCharsets.UTF_8)) {
				List<Profile> loadedProfiles = GSON.fromJson(reader, LIST_TYPE);
				if (loadedProfiles != null) {
					profiles.addAll(loadedProfiles);
				}
			} catch (IOException e) {
				System.err.println("[AltManager] No se pudo leer profiles.json: " + e.getMessage());
			}
		}

		Path stateFile = getStateFile();
		if (Files.exists(stateFile)) {
			try (Reader reader = Files.newBufferedReader(stateFile, StandardCharsets.UTF_8)) {
				State state = GSON.fromJson(reader, State.class);
				if (state != null) {
					this.selectedProfileId = state.selectedProfileId;
				}
			} catch (IOException e) {
				System.err.println("[AltManager] No se pudo leer state.json: " + e.getMessage());
			}
		}
	}

	/** Guarda la lista de perfiles y el estado actual en disco. */
	public synchronized void save() {
		try (Writer writer = Files.newBufferedWriter(getProfilesFile(), StandardCharsets.UTF_8)) {
			GSON.toJson(profiles, LIST_TYPE, writer);
		} catch (IOException e) {
			System.err.println("[AltManager] No se pudo guardar profiles.json: " + e.getMessage());
		}

		try (Writer writer = Files.newBufferedWriter(getStateFile(), StandardCharsets.UTF_8)) {
			State state = new State();
			state.selectedProfileId = this.selectedProfileId;
			GSON.toJson(state, State.class, writer);
		} catch (IOException e) {
			System.err.println("[AltManager] No se pudo guardar state.json: " + e.getMessage());
		}
	}

	public synchronized List<Profile> getProfiles() {
		load();
		return new ArrayList<>(profiles);
	}

	/** Añade un perfil nuevo con el nombre dado y lo guarda inmediatamente. */
	public synchronized Profile addProfile(String name) {
		load();
		String trimmed = name == null ? "" : name.trim();
		if (trimmed.isEmpty()) {
			trimmed = "Perfil " + (profiles.size() + 1);
		}
		Profile profile = new Profile(trimmed);
		profiles.add(profile);
		save();
		return profile;
	}

	/** Elimina un perfil por su id y lo persiste. */
	public synchronized void removeProfile(String id) {
		load();
		profiles.removeIf(p -> p.getId().equals(id));
		if (id.equals(selectedProfileId)) {
			selectedProfileId = null;
		}
		save();
	}

	public synchronized void renameProfile(String id, String newName) {
		load();
		for (Profile p : profiles) {
			if (p.getId().equals(id)) {
				p.setName(newName);
				break;
			}
		}
		save();
	}

	public synchronized void selectProfile(String id) {
		load();
		this.selectedProfileId = id;
		save();
	}

	public synchronized Optional<Profile> getSelectedProfile() {
		load();
		if (selectedProfileId == null) {
			return Optional.empty();
		}
		return profiles.stream().filter(p -> p.getId().equals(selectedProfileId)).findFirst();
	}

	public synchronized String getSelectedProfileId() {
		load();
		return selectedProfileId;
	}

	/** Estructura auxiliar para serializar el estado (perfil seleccionado). */
	private static class State {
		String selectedProfileId;
	}
}
