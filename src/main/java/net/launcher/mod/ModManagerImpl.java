package net.launcher.mod;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.MinecraftIcons;
import api.launcher.ModManager;
import api.launcher.setting.Setting;
import api.launcher.setting.SettingType;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.launcher.OptionLaunchElementManager;
import net.launcher.TransformTask;
import net.launcher.game.ServerStatus;
import net.launcher.game.forge.ForgeMod;
import net.launcher.game.nbt.NBTCompound;
import net.launcher.utils.resource.ArchiveRepository;
import net.launcher.utils.resource.Delivery;
import net.launcher.utils.resource.FetchOption;
import net.launcher.utils.resource.Resource;
import org.to2mbn.jmccc.option.LaunchOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
class ModManagerImpl extends OptionLaunchElementManager<ForgeMod, ServerStatus.ModInfo> implements ModManager
{
	private ArchiveRepository<ForgeMod[]> archiveResource;

	Map<String, String> hashMap = new TreeMap<>();
	private Map<String, ForgeMod> instanceMap = new TreeMap<>();

	private ObservableList<ForgeMod> list;

	ModManagerImpl(ArchiveRepository<ForgeMod[]> archiveResource)
	{
		this.archiveResource = archiveResource;
		list = FXCollections.observableArrayList();
		archiveResource.getResourceMap().forEach((k, v) ->
		{
			for (ForgeMod release : v.getContainData())
			{
				String s = getModKey(release);
				list.add(release);
				hashMap.put(s, v.getHash());
				instanceMap.put(s, release);
			}
		});
		archiveResource.getResourceMap().addListener((MapChangeListener<String, Resource<ForgeMod[]>>) change ->
		{
			if (change.wasAdded())
				for (ForgeMod modMeta : change.getValueAdded().getContainData())
				{
					String s = getModKey(modMeta);
					hashMap.put(s, change.getValueAdded().getHash());
					instanceMap.put(s, modMeta);
					list.addAll(modMeta);
				}
			if (change.wasRemoved())
				for (ForgeMod release : change.getValueRemoved().getContainData())
				{
					String s = getModKey(release);
					hashMap.remove(s);
					instanceMap.remove(s);
					list.remove(release);
				}
		});
	}

	@Override
	public ObservableList<ForgeMod> getAllElement()
	{
		return FXCollections.unmodifiableObservableList(list);
	}

	@Override
	protected SettingType.Option<ServerStatus.ModInfo> getOption()
	{
		return SettingMod.INSTANCE.MODS;
	}

	@Override
	protected void implementRuntimePath(LaunchProfile profile, Path path, Setting instance, LaunchOption option)
	{
//		ObservableList<ForgeMod> includeElementContainer = getIncludeElementContainer(profile);
//		includeElementContainer.stream().map(mod -> hashMap.get(getModKey(mod))).collect(Collectors.toSet()).forEach(
//				s -> deliveryCache.put(option, archiveResource.fetchResource(path.resolve("mods"), s, ArchiveRepository.FetchOption.HARD_LINK))
//		);
	}

	public void onClose(LaunchOption option, LaunchProfile profile)
	{
//		Repository.Delivery<Resource<ForgeMod[]>> delivery = deliveryCache.get(option);
//		if (delivery != null)
//			delivery.markRelease();
	}

	@Override
	protected List<ForgeMod> from(ServerStatus.ModInfo value)
	{
		List<ForgeMod> mods = new ArrayList<>();
		for (Map.Entry<String, String> entry : value.getModIdVersions().entrySet())
		{
			ForgeMod forgeMod = instanceMap.get(entry.getKey() + ":" + entry.getValue());
			if (forgeMod != null)
				mods.add(forgeMod);
		}
		return mods;
	}

	@Override
	protected ServerStatus.ModInfo to(List<ForgeMod> lst)
	{
		return new ServerStatus.ModInfo("fml", lst.stream().collect(Collectors.toMap(ForgeMod::getModId, v -> v.getMetaData
				().getVersion())), false);
	}

	@Override
	public Image getLogo(ForgeMod forgeMod)
	{
		Objects.requireNonNull(forgeMod);
		String logoFile = forgeMod.getMetaData().getLogoFile();
		if (logoFile != null && !logoFile.isEmpty())
		{
			String modKey = getModKey(forgeMod);
			String hash = hashMap.get(modKey);
			Resource<ForgeMod[]> resource = archiveResource.getResourceMap().get(hash);
			if (resource == null) return null;
			try
			{
				return new Image(archiveResource.openStream(resource, logoFile));
			}
			catch (IOException e)
			{
				ARML.taskCenter().reportError("ForgeGetLogo", e);
				return MinecraftIcons.UNKNOWN;
			}
		}
		return MinecraftIcons.UNKNOWN;
	}

	@Override
	public Task<ForgeMod[]> importMod(Path path)
	{
		return TransformTask.create("mod.import", archiveResource.importResource(path), Resource::getContainData);
	}

	String getModKey(ForgeMod forgeMod)
	{
		return forgeMod.getModId() + ":" + forgeMod.getMetaData().getVersion();
	}

	@Override
	public Task<Void> exportMod(Path path, ForgeMod mod)
	{
		Objects.requireNonNull(path);
		Objects.requireNonNull(mod);
		String hash = hashMap.get(getModKey(mod));
		if (hash == null)
			return null;
		return new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				Path dir;
				if (Files.isDirectory(path)) dir = path;
				else dir = path.getParent();
				Resource<ForgeMod[]> resource = archiveResource.getResourceMap().get(hash);
				Delivery<ForgeMod[]> delivery = archiveResource.fetchResource(dir, hash, FetchOption.COPY).get();
				Path path = delivery.getResourceVirtualPaths().iterator().next();
				if (dir == path)
					Files.move(path, dir.resolve(resource.getName() + resource.getType().getSuffix()));
				else Files.move(path, path);
				return null;
			}
		};
	}

	@Override
	public Task<?> update() {return archiveResource.update();}

	@Override
	public NBTCompound getMetadata(ForgeMod forgeMod)
	{
		String s = hashMap.get(getModKey(forgeMod));
		Resource<ForgeMod[]> resource = archiveResource.getResourceMap().get(s);
		return resource.getCompound();
	}
}
