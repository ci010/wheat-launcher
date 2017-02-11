package net.launcher.profile;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import net.launcher.setting.GameSetting;
import net.launcher.setting.GameSettingType;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.option.WindowSize;

import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ci010
 */
public class LaunchProfile
{
	public enum Source
	{
		CREATED, IMPORTED
	}

	private final String id;
	private final long createdDate;
	private final Source source;

	private ObjectProperty<LaunchProfile> parent = new SimpleObjectProperty<>();

	private StringProperty displayName = new SimpleStringProperty("");
	private StringProperty version = new SimpleStringProperty();
	private ObjectProperty<WindowSize> resolution = new SimpleObjectProperty<>(WindowSize.window(856, 482));
	private IntegerProperty memory = new SimpleIntegerProperty(512);
	private ObjectProperty<MinecraftDirectory> minecraftLocation = new SimpleObjectProperty<>(new MinecraftDirectory());
	private ObjectProperty<JavaEnvironment> javaEnvironment = new SimpleObjectProperty<>(JavaEnvironment.current());

	private ObservableMap<String, GameSetting> gameSettingInstanceMap = FXCollections.observableHashMap();

	public LaunchProfile(String id, long createdDate, Source source)
	{
		this.id = id;
		this.createdDate = createdDate;
		this.source = source;
	}

	public LaunchProfile(String id)
	{
		this(id, Calendar.getInstance().getTimeInMillis(), Source.CREATED);
	}

	public LaunchProfile()
	{
		this(Long.toString(System.currentTimeMillis()), Calendar.getInstance().getTimeInMillis(),
				Source.CREATED);
	}

	public LaunchProfile(Source source)
	{
		this(Long.toString(System.currentTimeMillis()), Calendar.getInstance().getTimeInMillis(),
				source);
	}

	public LaunchProfile getParent() {return parent.get();}

	public ObjectProperty<LaunchProfile> parentProperty() {return parent;}

	public void setParent(LaunchProfile parent) {this.parent.set(parent);}

	public String getDisplayName() {return displayName.get();}

	public StringProperty displayNameProperty() {return displayName;}

	public void setDisplayName(String displayName) {this.displayName.set(displayName);}

	public String getVersion() {return version.get();}

	public StringProperty versionProperty() {return version;}

	public void setVersion(String version) {this.version.set(version);}

	public ReadOnlyObjectProperty<WindowSize> resolutionProperty()
	{
		return resolution;
	}

	public ReadOnlyIntegerProperty memoryProperty()
	{
		return memory;
	}

	public ReadOnlyObjectProperty<MinecraftDirectory> minecraftLocationProperty()
	{
		return minecraftLocation;
	}

	public ReadOnlyObjectProperty<JavaEnvironment> javaEnvironmentProperty()
	{
		return javaEnvironment;
	}

	public WindowSize getResolution() {return resolution.get();}

	public void setResolution(WindowSize resolution)
	{
		Objects.requireNonNull(resolution);
		this.resolution.set(resolution);
	}

	public int getMemory() {return memory.get();}

	public void setMemory(int memory) {this.memory.set(memory);}

	public MinecraftDirectory getMinecraftLocation()
	{
		return minecraftLocation.get();
	}

	public void setMinecraftLocation(MinecraftDirectory minecraftLocation)
	{
		Objects.requireNonNull(minecraftLocation);
		this.minecraftLocation.set(minecraftLocation);
	}

	public JavaEnvironment getJavaEnvironment() {return javaEnvironment.get();}

	public void setJavaEnvironment(JavaEnvironment javaEnvironment)
	{
		Objects.requireNonNull(javaEnvironment);
		if (!javaEnvironment.getJavaPath().getName().equals("java.exe"))
			throw new IllegalArgumentException("java.invalid");
		this.javaEnvironment.set(javaEnvironment);
	}

	public ObservableMap<String, GameSetting> gameSettingsProperty()
	{
		return gameSettingInstanceMap;
	}

	public Optional<GameSetting> getGameSetting(GameSettingType setting)
	{
		Objects.requireNonNull(setting);
		String id = setting.getID();
		return Optional.ofNullable(gameSettingInstanceMap.get(id));
	}

	public void addGameSetting(GameSetting setting)
	{
		Objects.requireNonNull(setting);
		String id = setting.getGameSettingType().getID();
		gameSettingInstanceMap.put(id, setting);
	}

	public Collection<GameSetting> getAllGameSettings()
	{
		return gameSettingInstanceMap.values();
	}

	public String getId() {return id;}

	public long getCreatedDate() {return createdDate;}

	public Source getSource() {return source;}
}
