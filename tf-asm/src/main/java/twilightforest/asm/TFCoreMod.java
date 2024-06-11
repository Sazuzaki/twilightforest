package twilightforest.asm;

import cpw.mods.modlauncher.api.ITransformer;
import net.neoforged.neoforgespi.coremod.ICoreMod;
import twilightforest.asm.transformers.armor.ArmorColorRenderingTransformer;
import twilightforest.asm.transformers.armor.CancelArmorRenderingTransformer;

import java.util.List;

public class TFCoreMod implements ICoreMod {
	@Override
	public Iterable<? extends ITransformer<?>> getTransformers() {
		return List.of(
			new CancelArmorRenderingTransformer(),
			new ArmorColorRenderingTransformer()
		);
	}
}