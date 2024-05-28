package org.zeith.trims_on_tools.mixins.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.atlas.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteSources.class)
public interface SpriteSourcesAccessor
{
	@Invoker
	static SpriteSourceType callRegister(String p_262175_, Codec<? extends SpriteSource> p_261464_) {throw new UnsupportedOperationException();}
}
