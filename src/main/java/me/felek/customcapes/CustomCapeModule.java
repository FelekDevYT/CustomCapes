package me.felek.customcapes;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.setting.ModeSetting;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.ListSetting;
import org.rusherhack.core.setting.OptionSetting;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class CustomCapeModule extends ToggleableModule {
    public static CustomCapeModule INSTANCE;

    public ModeSetting cape = new ModeSetting("cape", null);
    public BooleanSetting onlyForMe = new BooleanSetting("Only For Me", "Show the cape only for yourself.", true)
            .setVisibility(() -> cape.getValue()!=null);

    public File capesDir = new File(RusherHackAPI.getPath().toFile(), "capes");
    private final Map<String, ResourceLocation> capeTextures = new HashMap<>();

    public CustomCapeModule() {
        super("CustomCape", "Allows you to use a custom cape.", ModuleCategory.RENDER);
        INSTANCE = this;
        registerSettings(cape, onlyForMe);
        loadCapes();
    }

    public void loadCapes() {
        capeTextures.clear();
        cape.getOptions().clear();
        cape.addOption("None");

        if (!capesDir.exists() && capesDir.mkdirs()) {
            ChatUtils.print("Created capes directory");
        }

        File[] capeFiles = capesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (capeFiles == null || capeFiles.length == 0) {
            return;
        }

        for (File cp : capeFiles) {
            String name = cp.getName().substring(0, cp.getName().length() - 4);
            try (FileInputStream fis = new FileInputStream(cp)) {
                NativeImage img = NativeImage.read(fis);
                DynamicTexture texture = new DynamicTexture(img);
                ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("custom_capes", name.toLowerCase().replaceAll("\\s+", "_"));

                mc.getTextureManager().register(resourceLocation, texture);

                capeTextures.put(name, resourceLocation);
                cape.addOption(name);
            } catch(Exception exc) {
                System.err.println("Failed to load cape: " + name);
                exc.printStackTrace();
            }
        }

        ChatUtils.print("Loaded " + capeTextures.size() + " capes!");
    }

    public ResourceLocation getSelectedCapeTexture() {
        if (!this.isToggled() || cape == null) {
            return null;
        }
        return this.capeTextures.get(this.cape.getValue());
    }

    public boolean isOnlyForMe() {
        return this.onlyForMe.getValue();
    }
}
