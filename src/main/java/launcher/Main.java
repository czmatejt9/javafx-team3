package launcher;

import java.io.File;

import classes.ImageFxIO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// --------------------using FXML--------------------
		// Parent root =
		// FXMLLoader.load(getClass().getResource("/views/javaPaint.fxml"));

		// --------------------using Java--------------------
		/*
		AnchorPane root = new AnchorPane();

		VBox vbox1 = new VBox();

		MenuBar menuBar = new MenuBar();

		Menu menuFile = new Menu("File");

		MenuItem menuItemNewFile = new MenuItem("New");
		menuItemNewFile.setAccelerator(new KeyCodeCombination( // code, shift, control, alt, meta, shortcut
			KeyCode.N, 
			KeyCombination.SHIFT_ANY,
			KeyCombination.CONTROL_DOWN, 
			KeyCodeCombination.ALT_ANY, 
			KeyCombination.META_ANY,
			KeyCombination.SHORTCUT_ANY
		));
		menuItemNewFile.setOnAction(e -> {
			newFile();
		});

		menuFile.getItems().addAll(menuItemNewFile);

		MenuItem menuItemOpenFile = new MenuItem("Open");
		menuItemOpenFile.setAccelerator(new KeyCodeCombination( // code, shift, control, alt, meta, shortcut
		KeyCode.O, 
		KeyCombination.SHIFT_ANY,
		KeyCombination.CONTROL_DOWN, 
		KeyCodeCombination.ALT_ANY, 
		KeyCombination.META_ANY,
		KeyCombination.SHORTCUT_ANY
		));
		menuItemOpenFile.setOnAction(e -> {
			openFile();
		});
		menuFile.getItems().addAll(menuItemOpenFile);
		
		MenuItem menuItemSaveFile = new MenuItem("Save");
		menuItemSaveFile.setAccelerator(new KeyCodeCombination( // code, shift, control, alt, meta, shortcut
		KeyCode.S, 
		KeyCombination.SHIFT_ANY,
		KeyCombination.CONTROL_DOWN, 
		KeyCodeCombination.ALT_ANY, 
		KeyCombination.META_ANY,
		KeyCombination.SHORTCUT_ANY
		));
		menuItemSaveFile.setOnAction(e -> {
			saveFile();
		});
		menuFile.getItems().addAll(menuItemSaveFile);
		
		Menu menuEdit = new Menu("Edit");

		Menu menuHelp = new Menu("Help");

		menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp);
		vbox1.getChildren().addAll(menuBar);
		root.getChildren().addAll(vbox1);

		Scene scene = new Scene(root, 1100, 550);

		stage.setScene(scene);
		stage.setTitle("javaPaint");
		stage.setMinWidth(1000);
		stage.setMinHeight(400);
		stage.show();*/
	}

	public static void main(String[] args) {
		launch(new String[0]);
	}

	// TODO: Kubek
	public void openFile() {
		/*ImageFxIO loader = new ImageFxIO((Stage) bigAnchor.getScene().getWindow());
		Object[] result = loader.openFromFile();
		if (result == null) return;
		image = (Image) result[0];
		File f = (File) result[1];
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		canvas.setHeight(h);
		canvas.setWidth(w);
		canvasAnchor.setPrefHeight(h);
		canvasAnchor.setPrefWidth(w);
		getCanvasHeightWidth();

		initializeHistory();
		
		initializeColors();
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		context.drawImage(image, 0, 0);
		file = f;
		projectName.setText(file.getName());*/
	}

	public void saveFile() {
		/*ImageFxIO saver = new ImageFxIO((Stage) bigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		if (file == null) {
			File f = saver.saveToFile(image);
			if (f != null) {
				file = f;
				projectName.setText(file.getName());
			}
		} else {
			saver.saveToFile(image, file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase(), file);
		}*/
	}

	public void newFile() {
		/*context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		projectName.setText("Not Saved yet");
		file = null;
		initializeHistory();*/
	}
}
