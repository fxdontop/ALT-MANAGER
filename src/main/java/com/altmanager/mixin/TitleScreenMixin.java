package com.altmanager.mixin;

import com.altmanager.gui.AltManagerScreen;
import com.altmanager.session.SessionUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Añade el botón "Alt Manager" arriba a la derecha de la pantalla principal (título),
 * junto con un texto que muestra la sesión activa.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	private static final int BUTTON_WIDTH = 100;
	private static final int MARGIN = 10;

	protected TitleScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void altmanager$addButton(CallbackInfo ci) {
		int x = this.width - BUTTON_WIDTH - MARGIN;
		int y = MARGIN + 14; // deja lugar arriba para el texto de la sesión activa

		this.addRenderableWidget(Button.builder(Component.literal("Alt Manager"), button -> {
					if (this.minecraft != null) {
						this.minecraft.setScreen(new AltManagerScreen((TitleScreen) (Object) this));
					}
				})
				.bounds(x, y, BUTTON_WIDTH, 20)
				.build());
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void altmanager$renderActiveSession(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		String activeSession = SessionUtil.getActiveUsername();
		int x = this.width - MARGIN;
		int y = MARGIN;
		context.drawString(this.font, "Sesión activa: " + activeSession, x - this.font.width("Sesión activa: " + activeSession), y, 0x55FF55);
	}
}
