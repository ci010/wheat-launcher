package net.launcher.model.fx.profile;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import net.launcher.model.Profile;
import org.to2mbn.jmccc.option.JavaEnvironment;
import org.to2mbn.jmccc.option.WindowSize;

/**
 * @author ci010
 */
public class ProfilerImpl implements LaunchProfiler
{
	private ObjectProperty<Profile> delegate = new SimpleObjectProperty<>();

	private StringProperty id = new SimpleStringProperty(),
			name = new SimpleStringProperty(),
			mcVersion = new SimpleStringProperty();
	private ObjectProperty<WindowSize> resolution = new SimpleObjectProperty<>();
	private ObjectProperty<JavaEnvironment> environment = new SimpleObjectProperty<>();
	private IntegerProperty minMemory = new SimpleIntegerProperty(),
			maxMemory = new SimpleIntegerProperty();

	ProfilerImpl()
	{
		id.bind(Bindings.createStringBinding(() -> delegate.get().getId(), delegate));
		name.bind(Bindings.createStringBinding(() -> delegate.get().getName(), delegate));
		mcVersion.bind(Bindings.createStringBinding(() -> delegate.get().getMinecraftVersion(), delegate));

		resolution.bind(Bindings.createObjectBinding(() -> delegate.get().getResolution(), delegate));
		environment.bind(Bindings.createObjectBinding(() -> delegate.get().getJavaLocation(), delegate));

		minMemory.bind(Bindings.createIntegerBinding(() -> delegate.get().getMinMemory(), delegate));
		maxMemory.bind(Bindings.createIntegerBinding(() -> delegate.get().getMaxMemory(), delegate));
	}

	@Override
	public void load(Profile profile)
	{
		if (profile == null || profile == this) return;
		this.delegate.set(profile);
	}

	@Override
	public String getId()
	{
		return delegate.get().getId();
	}

	@Override
	public int getMaxMemory()
	{
		return delegate.get().getMaxMemory();
	}

	@Override
	public void setMaxMemory(int maxMemory)
	{
		delegate.get().setMaxMemory(maxMemory);
	}

	@Override
	public int getMinMemory()
	{
		return delegate.get().getMinMemory();
	}

	@Override
	public void setMinMemory(int minMemory)
	{
		delegate.get().setMinMemory(minMemory);
	}

	@Override
	public String getName()
	{
		return delegate.getName();
	}

	@Override
	public void setName(String name)
	{
		delegate.get().setName(name);
	}

	@Override
	public String getMinecraftVersion()
	{
		return delegate.get().getMinecraftVersion();
	}

	@Override
	public void setMinecraftVersion(String minecraftVersion)
	{
		delegate.get().setMinecraftVersion(minecraftVersion);
	}

	@Override
	public WindowSize getResolution()
	{
		return delegate.get().getResolution();
	}

	@Override
	public void setResolution(WindowSize resolution)
	{
		delegate.get().setResolution(resolution);
	}

	@Override
	public JavaEnvironment getJavaLocation()
	{
		return delegate.get().getJavaLocation();
	}

	@Override
	public void setJavaLocation(JavaEnvironment javaLocation)
	{
		delegate.get().setJavaLocation(javaLocation);
	}

	@Override
	public ReadOnlyStringProperty idProperty()
	{
		return id;
	}

	@Override
	public ReadOnlyIntegerProperty minMemoryProperty()
	{
		return minMemory;
	}

	@Override
	public ReadOnlyIntegerProperty maxMemoryProperty()
	{
		return maxMemory;
	}

	@Override
	public ReadOnlyStringProperty nameProperty()
	{
		return name;
	}

	@Override
	public ReadOnlyStringProperty versionProperty()
	{
		return mcVersion;
	}

	@Override
	public ReadOnlyObjectProperty<WindowSize> resolutionProperty()
	{
		return resolution;
	}

	@Override
	public ReadOnlyObjectProperty<JavaEnvironment> javaLocationProperty()
	{
		return environment;
	}
}
