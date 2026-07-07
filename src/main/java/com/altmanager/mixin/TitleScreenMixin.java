package com.altmanager.mixin;

import com.altmanager.gui.AltManagerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Añade el botón "Alt Manager" a la pantalla principal (título),
 * cerca de los botones de Singleplayer / Multiplayer.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void altmanager$addButton(CallbackInfo ci) {
		int buttonWidth = 100;
		int x = this.width / 2 - buttonWidth / 2;
		int y = this.height / 4 + 48 + 72 + 12;

		this.addRenderableWidget(Button.builder(Component.literal("Alt Manager"), button -> {
					if (this.minecraft != null) {
						this.minecraft.setScreen(new AltManagerScreen((TitleScreen) (Object) this));
					}
				})
				.bounds(x, y, buttonWidth, 20)
				.build());
	}
}
