package net.launcher.mod;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.LaunchElementManager;
import net.launcher.game.forge.ForgeMod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 * @author ci010
 */
public interface ModManager extends LaunchElementManager<ForgeMod>
{
	Image getLogo(ForgeMod forgeMod) throws IOException;

	Task<ForgeMod[]> importMod(Path path);

	Task<Void> exportMod(Path path, ForgeMod mod);

	Future<?> update();
}