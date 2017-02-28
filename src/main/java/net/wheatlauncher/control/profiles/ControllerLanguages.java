package net.wheatlauncher.control.profiles;

import api.launcher.ARML;
import api.launcher.LaunchProfile;
import api.launcher.LaunchProfileManager;
import api.launcher.MinecraftAssetsManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTableView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.TableColumn;
import net.launcher.assets.MinecraftVersion;
import net.launcher.control.MinecraftOptionButton;
import net.launcher.game.Language;
import net.launcher.setting.Setting;
import net.launcher.setting.SettingMinecraft;
import net.launcher.setting.SettingProperty;

import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author ci010
 */
public class ControllerLanguages
{
	public JFXTableView<Language> languageTable;
	public TableColumn<Language, String> id;
	public TableColumn<Language, String> name;
	public TableColumn<Language, String> region;
	public TableColumn<Language, String> bidi;

	public JFXTextField search;
	public MinecraftOptionButton<Boolean> useUnicode;
	public JFXButton confirm;

	public ResourceBundle resources;
	private InvalidationListener refresh = observable -> refresh();

	private ChangeListener<MinecraftVersion> listener = (observable, oldV, newV) ->
	{
		if (oldV != null) oldV.stateProperty().removeListener(refresh);
		if (newV != null) newV.stateProperty().addListener(refresh);
		refresh();
	};

	private ObservableList<Language> languageLists;
	private Map<String, Language> lookup;

	private void refresh()
	{
		ARML.logger().info("try to refresh lang");

		LaunchProfile selecting = ARML.core().getProfileManager().selecting();
		MinecraftAssetsManager assetsManager = ARML.core().getAssetsManager();
		MinecraftVersion version = assetsManager.getVersion(selecting.getVersion());
		if (version != null)
		{
			Task<Language[]> task = assetsManager.getRepository().getLanguages(version);
			task.setOnSucceeded(event ->
			{
				Worker<Language[]> source = event.getSource();
				languageLists.setAll(source.getValue());
				lookup = languageLists.stream().collect(Collectors.toMap(Language::getId, Function.identity()));
				Language language = selecting.getGameSetting(SettingMinecraft.INSTANCE)
						.map(s -> s.getOption(SettingMinecraft.INSTANCE.LANGUAGE))
						.map(SettingProperty::getValue)
						.map(lookup::get)
						.orElse(lookup.get("en_us"));

				languageTable.getSelectionModel().select(language);
				languageTable.scrollTo(language);
			});
			ARML.core().getTaskCenter().runTask(task);
		}
	}

	public void initialize()
	{
		languageLists = FXCollections.observableArrayList();
		FilteredList<Language> filteredList = new FilteredList<>(languageLists);
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
				(Predicate<Language>) language -> language.getId().contains(search.getText()) ||
						language.getName().contains(search.getText()) ||
						language.getRegion().contains(search.getText()), search.textProperty()));
		SortedList<Language> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(languageTable.comparatorProperty());
		languageTable.setItems(sortedList);
		id.setCellValueFactory(param -> Bindings.createStringBinding(() -> param.getValue().getId()));
		region.setCellValueFactory(param -> Bindings.createStringBinding(() -> param.getValue().getRegion()));
		name.setCellValueFactory(param -> Bindings.createStringBinding(() -> param.getValue().getName()));
		bidi.setCellValueFactory(param -> Bindings.createStringBinding(() -> String.valueOf(param.getValue().isBidirectional())));
		confirm.disableProperty().bind(Bindings.createBooleanBinding(() -> languageTable.getSelectionModel().isEmpty(), languageTable.getSelectionModel().selectedIndexProperty()));
		confirm.setOnAction(event ->
				ARML.core().getProfileManager().selecting().getGameSetting(SettingMinecraft.INSTANCE)
						.map(setting -> setting.getOption(SettingMinecraft.INSTANCE.LANGUAGE)).ifPresent(property ->
						property.setValue(languageTable.getSelectionModel().getSelectedItem().getId())));
		ARML.core().getProfileManager().selectedProfileProperty().addListener((observable, oldV, newV) ->
		{
			LaunchProfileManager profileManager = ARML.core().getProfileManager();
			profileManager.getProfile(oldV).ifPresent(profile -> profile.versionBinding().removeListener(listener));
			profileManager.getProfile(newV).ifPresent(profile -> profile.versionBinding().addListener(listener));
		});
		useUnicode.setUserData(resources);
		useUnicode.setPropertyBinding(Bindings.createObjectBinding(() ->
				{
					Setting set = ensureSetting(ARML.core().getProfileManager().selecting());
					return (SettingProperty.Limited<Boolean>) set.getOption(SettingMinecraft.INSTANCE.FORCE_UNICODE);
				}
				, ARML.core().getProfileManager().selectedProfileProperty()));

		refresh();
	}

	private Setting ensureSetting(LaunchProfile profile)
	{
		Optional<Setting> optional = profile.getGameSetting(SettingMinecraft.INSTANCE);
		Setting setting;
		if (!optional.isPresent())
			profile.addGameSetting(setting = SettingMinecraft.INSTANCE.defaultInstance());// force the minecraft// setting exist
		else setting = optional.get();
		return setting;
	}
}
