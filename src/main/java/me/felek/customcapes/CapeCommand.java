package me.felek.customcapes;

import org.rusherhack.core.command.AbstractCommand;
import org.rusherhack.core.command.annotations.CommandExecutor;

public class CapeCommand extends AbstractCommand {
    public CapeCommand() {
        super("cape", "Manages custom capes.");
    }

    @CommandExecutor(subCommand = "reload")
    private String reloadCapes() {
        if (CustomCapeModule.INSTANCE != null) {
            CustomCapeModule.INSTANCE.loadCapes();
            return "Reloaded capes from the directory.";
        }

        return "CustomCape module not found or not loaded yet.";
    }
}
