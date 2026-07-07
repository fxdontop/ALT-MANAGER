package com.altmanager.gui;

import com.altmanager.data.Profile;
import com.altmanager.data.ProfileManager;
import com.altmanager.session.SessionUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Pantalla de gestión de perfiles ("Alt Manager").
 * Permite añadir, editar, usar (cambiar de sesión sin reiniciar) y eliminar perfiles.
 * Los datos persisten en disco a través de {@link ProfileManager}.
 */
public class AltManagerScreen extends Screen {

	private static final int ROW_HEIGHT = 24;
	private static final int LIST_TOP = 60;

	private final Screen parent;
	private EditBox nameField;
	private Button confirmButton;

	/** Si no es null, estamos editando este perfil en vez de añadir uno nuevo. */
	private String editingProfileId = null;

	public AltManagerScreen(Screen parent) {
		super(Component.literal("Alt Manager"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		ProfileManager.get().load();

		int centerX = this.width / 2;

		this.nameField = new EditBox(this.font, centerX - 150, 30, 200, 20,
				Component.literal("Nombre del perfil"));
		this.nameField.setMaxLength(32);

		this.confirmButton = Button.builder(Component.literal("Añadir"), button -> onConfirm())
				.bounds(centerX + 55, 30, 95, 20)
				.build();

		rebuildList();
	}

	private void onConfirm() {
		String name = this.nameField.getValue();
		if (this.editingProfileId != null) {
			ProfileManager.get().renameProfile(this.editingProfileId, name);
			this.editingProfileId = null;
			this.confirmButton.setMessage(Component.literal("Añadir"));
		} else {
			ProfileManager.get().addProfile(name);
		}
		this.nameField.setValue("");
		rebuildList();
	}

	private void onEdit(Profile profile) {
		this.editingProfileId = profile.getId();
		this.nameField.setValue(profile.getName());
		this.confirmButton.setMessage(Component.literal("Guardar"));
	}

	/** Cambia la sesión activa del cliente al nombre del perfil, sin reiniciar el juego. */
	private void onUse(Profile profile) {
		SessionUtil.switchSession(profile.getName());
		ProfileManager.get().selectProfile(profile.getId());
		rebuildList();
	}

	/**
	 * Reconstruye los widgets de la lista de perfiles.
	 * Se llama cada vez que la lista cambia (añadir, editar, usar, eliminar).
	 */
	private void rebuildList() {
		this.clearWidgets();
		this.addRenderableWidget(this.nameField);
		this.addRenderableWidget(this.confirmButton);
		this.addRenderableWidget(Button.builder(Component.literal("Volver"), button -> {
					if (this.minecraft != null) {
						this.minecraft.setScreen(this.parent);
					}
				})
				.bounds(this.width / 2 - 100, this.height - 30, 200, 20)
				.build());

		List<Profile> profiles = ProfileManager.get().getProfiles();
		String selectedId = ProfileManager.get().getSelectedProfileId();

		int centerX = this.width / 2;
		int y = LIST_TOP;

		for (Profile profile : profiles) {
			boolean isSelected = profile.getId().equals(selectedId);
			String label = (isSelected ? "» " : "") + profile.getName();

			this.addRenderableWidget(Button.builder(Component.literal(label), button -> onUse(profile))
					.bounds(centerX - 150, y, 110, 20)
					.build());

			this.addRenderableWidget(Button.builder(Component.literal("Usar"), button -> onUse(profile))
					.bounds(centerX - 35, y, 60, 20)
					.build());

			this.addRenderableWidget(Button.builder(Component.literal("Editar"), button -> onEdit(profile))
					.bounds(centerX + 30, y, 60, 20)
					.build());

			this.addRenderableWidget(Button.builder(Component.literal("Eliminar"), button -> {
						ProfileManager.get().removeProfile(profile.getId());
						if (profile.getId().equals(this.editingProfileId)) {
							this.editingProfileId = null;
							this.nameField.setValue("");
							this.confirmButton.setMessage(Component.literal("Añadir"));
						}
						rebuildList();
					})
					.bounds(centerX + 95, y, 75, 20)
					.build());

			y += ROW_HEIGHT;
		}
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		context.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);

		String activeSession = SessionUtil.getActiveUsername();
		context.drawCenteredString(this.font,
				Component.literal("Sesión activa: " + activeSession),
				this.width / 2, 20, 0x55FF55);

		List<Profile> profiles = ProfileManager.get().getProfiles();
		if (profiles.isEmpty()) {
			context.drawCenteredString(this.font,
					Component.literal("No hay perfiles guardados todavía."),
					this.width / 2, LIST_TOP + 10, 0xAAAAAA);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		if (this.minecraft != null) {
			this.minecraft.setScreen(this.parent);
		}
	}
}
