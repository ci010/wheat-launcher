package net.wheatlauncher.control.settings;

import api.launcher.Shell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.launcher.control.ImageCell;
import net.launcher.game.WorldInfo;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author ci010
 */
public class ControllerMap
{
	public JFXListView<WorldInfo> maps;
	public ResourceBundle resources;
	public JFXTextField search;
	public JFXButton exportBtn, importBtn;

	public void initialize()
	{
		FilteredList<WorldInfo> filteredList = new FilteredList<>(Shell.instance().getAllWorlds());
		filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Predicate<WorldInfo>) worldInfo ->
				worldInfo.getFileName().contains(search.getText()) ||
						worldInfo.getDisplayName().contains(search.getText()), search.textProperty()));
		maps.setItems(filteredList);

		maps.setCellFactory(param -> new JFXListCell<WorldInfo>()
		{
			@Override
			public void updateItem(WorldInfo item, boolean empty)
			{
				super.updateItem(item, empty);
				if (item == null || empty) return;
				WorldInfoCell worldInfoCell = new WorldInfoCell(item, null);
				Image icon = Shell.instance().executeImmediately(Shell.instance().buildTask("world.icon", item
						.getFileName()));
				worldInfoCell.setImage(icon);
//				try
//				{
//					worldInfoCell.setImage(ARML.core().getWorldManager().getWorldIcon(item));
//				}
//				catch (Exception e) { MainApplication.displayError(maps.getScene(), e);}
				this.setGraphic(worldInfoCell);
			}
		});

		importBtn.setOnAction(event ->
		{
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle(resources.getString("map.import"));
			File file = chooser.showDialog(importBtn.getScene().getWindow());
			Shell.instance().buildAndExecute("world.import", file.getAbsolutePath());
		});
		exportBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> maps.getSelectionModel().isEmpty(), maps
				.getSelectionModel().selectedIndexProperty()));
		exportBtn.setOnAction(event ->
		{
			WorldInfo selectedItem = maps.getSelectionModel().getSelectedItem();
			String fileName = selectedItem.getFileName();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(fileName);
			File file = fileChooser.showSaveDialog(exportBtn.getScene().getWindow());
			Shell.instance().buildAndExecute("world.export", maps.getSelectionModel().getSelectedItem().getFileName(),
					file.getAbsolutePath());
		});
	}

	public class WorldInfoCell extends ImageCell<WorldInfo> implements Observable
	{
		private List<InvalidationListener> list;

		public WorldInfoCell(WorldInfo worldInfo, Image image)
		{
			super(worldInfo, image);
			change();
		}

		@Override
		protected void init()
		{
			list = FXCollections.observableList(new ArrayList<>(3));
			super.init();
		}

		void change()
		{
			for (InvalidationListener listener : list)
				listener.invalidated(this);
		}

		@Override
		protected Node buildContent()
		{
			VBox box = new VBox();
			box.setSpacing(5);
			Label name = new Label(), fileInfo = new Label(), mode = new Label();
			box.getChildren().setAll(name, fileInfo, mode);
			name.setStyle("-fx-font-weight: BOLD;-fx-font-size:14px;");

			name.textProperty().bind(Bindings.createStringBinding(() ->
					{
						if (getValue() == null) return "";
						return getValue().getDisplayName();
					}, this
			));
			fileInfo.textProperty().bind(Bindings.createStringBinding(() ->
			{
				if (getValue() == null) return "";
				return getValue().getFileName() +
						" (" + new Date(getValue().getLastPlayed()) + ")";
			}, this));
			mode.textProperty().bind(Bindings.createStringBinding(() ->
			{
				WorldInfo value = getValue();
				if (value == null) return "";
				StringJoiner joiner = new StringJoiner(", ");
				joiner.add(value.getGameType().name());
				if (value.isEnabledCheat()) joiner.add("Cheat");
				if (value.isHardCore()) joiner.add("Hardcore");
				return joiner.toString();
			}, this));
			return box;
		}

		@Override
		public void addListener(InvalidationListener listener)
		{
			list.add(listener);
		}

		@Override
		public void removeListener(InvalidationListener listener) {}
	}
}
