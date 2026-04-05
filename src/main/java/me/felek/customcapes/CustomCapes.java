package me.felek.customcapes;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Example rusherhack plugin
 *
 * @author John200410
 */
public class CustomCapes extends Plugin {
	
	@Override
	public void onLoad() {
        RusherHackAPI.getModuleManager().registerFeature(new CustomCapeModule());
        RusherHackAPI.getCommandManager().registerFeature(new CapeCommand());

		CustomCapeModule.INSTANCE.loadCapes();
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Example core plugin unloaded!");
	}
	
}