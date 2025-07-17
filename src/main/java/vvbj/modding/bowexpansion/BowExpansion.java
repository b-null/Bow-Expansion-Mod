package vvbj.modding.bowexpansion;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vvbj.modding.bowexpansion.item.ModItems;

public class BowExpansion implements ModInitializer {
	public static final String MOD_ID = "bow-expansion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
	}
}