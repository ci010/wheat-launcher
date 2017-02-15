package net.launcher.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * @author ci010
 */
public abstract class ImageCell<T> extends HBox
{
	private ImageView icon = new ImageView();
	protected StackPane imageContainer;

	private ObjectProperty<Image> image = icon.imageProperty();

	public Image getImage()
	{
		return image.get();
	}

	public ObjectProperty<Image> imageProperty()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image.set(image);
	}

	private IntegerProperty iconWidth = new SimpleIntegerProperty(64),
			iconHeight = new SimpleIntegerProperty(64);

	public int getIconWidth()
	{
		return iconWidth.get();
	}

	public IntegerProperty iconWidthProperty()
	{
		return iconWidth;
	}

	public void setIconWidth(int iconWidth)
	{
		this.iconWidth.set(iconWidth);
	}

	public int getIconHeight()
	{
		return iconHeight.get();
	}

	public IntegerProperty iconHeightProperty()
	{
		return iconHeight;
	}

	public void setIconHeight(int iconHeight)
	{
		this.iconHeight.set(iconHeight);
	}

	private ObjectProperty<T> value = new SimpleObjectProperty<>();

	public T getValue()
	{
		return value.get();
	}

	public ObjectProperty<T> valueProperty()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value.set(value);
	}

	public ImageCell()
	{
		init();
	}

	protected abstract Node buildContent();

	protected void init()
	{
		imageContainer = new StackPane();
		imageContainer.setMaxSize(64, 64);
		icon.setFitHeight(64);
		icon.setFitWidth(64);
		iconWidth.addListener(observable -> icon.setFitWidth(iconWidth.get()));
		iconHeight.addListener(observable -> icon.setFitHeight(iconHeight.get()));

		imageContainer.getChildren().addAll(icon);

		this.getChildren().addAll(imageContainer, buildContent());
		this.setSpacing(10);
		this.setPadding(new Insets(10));
	}
}
