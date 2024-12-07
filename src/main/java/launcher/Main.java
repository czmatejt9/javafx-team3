package launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import classes.CanvasHistory;
import classes.ImageFxIO;
import classes.PixelXY;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// --------------------using FXML--------------------
		//Parent root = FXMLLoader.load(getClass().getResource("/views/jabaPaint.fxml"));

		// --------------------using Java--------------------
		/**/
		VBox root = new VBox();

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

		MenuItem menuItemOpenFile = new MenuItem("Open");
		menuItemOpenFile.setAccelerator(new KeyCodeCombination(
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
			
		MenuItem menuItemSaveFile = new MenuItem("Save");
		menuItemSaveFile.setAccelerator(new KeyCodeCombination( 
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
		
		MenuItem menuItemGenerateImage = new MenuItem("Save As");
		menuItemGenerateImage.setOnAction(e -> {
			saveAs();
		});
		
		MenuItem menuItemSaveAs = new MenuItem("Generate");
		menuItemSaveAs.setOnAction(e -> {
			generateImage();
		});
		
		menuFile.getItems().addAll(
			menuItemNewFile, 
			menuItemOpenFile, 
			menuItemSaveFile, 
			menuItemGenerateImage, 
			menuItemSaveAs
		);
		
		Menu menuEdit = new Menu("Edit");

		MenuItem menuItemUndo = new MenuItem("Undo");
		menuItemUndo.setAccelerator(new KeyCodeCombination(
			KeyCode.Z, 
			KeyCombination.SHIFT_ANY,
			KeyCombination.CONTROL_DOWN, 
			KeyCodeCombination.ALT_ANY, 
			KeyCombination.META_ANY,
			KeyCombination.SHORTCUT_ANY
		));
		menuItemUndo.setOnAction(e -> {
			undo();
		});
			
		MenuItem menuItemRedo = new MenuItem("Redo");
		menuItemRedo.setAccelerator(new KeyCodeCombination( 
			KeyCode.Y, 
			KeyCombination.SHIFT_ANY,
			KeyCombination.CONTROL_DOWN, 
			KeyCodeCombination.ALT_ANY, 
			KeyCombination.META_ANY,
			KeyCombination.SHORTCUT_ANY
		));
		menuItemRedo.setOnAction(e -> {
			redo();
		});

		menuEdit.getItems().addAll(menuItemUndo, menuItemRedo);

		Menu menuHelp = new Menu("Help");

		MenuItem menuItemAbout = new MenuItem("About");
		menuItemAbout.setOnAction(e -> {
			openAbout();
		});

		menuHelp.getItems().addAll(menuItemAbout);

		menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp);

		TabPane tabPane = new TabPane();
		tabPane.setPrefHeight(160);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		Tab tabHome = new Tab("Home");
		HBox hBoxHome = new HBox();

		VBox vBoxTools = new VBox();
		vBoxTools.setSpacing(20);
		vBoxTools.setPadding(new Insets(10));

		GridPane gridPaneTools = new GridPane();
		Button buttonPencil = new Button();
		buttonPencil.setId("buttonPencil");
		buttonPencil.setPrefHeight(60);
		buttonPencil.setPrefWidth(60);
		buttonPencil.setGraphic(new ImageView(new Image("file:src/main/resources/images/pencil.png")));
		buttonPencil.setOnMouseClicked(e -> {
			selectTool(e);
		});
		Button buttonEraser = new Button();
		buttonEraser.setId("buttonEraser");
		buttonEraser.setPrefHeight(60);
		buttonEraser.setPrefWidth(60);
		buttonEraser.setGraphic(new ImageView(new Image("file:src/main/resources/images/eraser.png")));
		buttonEraser.setOnMouseClicked(e -> {
			selectTool(e);
		});
		Button buttonBucket = new Button();
		buttonBucket.setId("buttonBucket");
		buttonBucket.setPrefHeight(60);
		buttonBucket.setPrefWidth(60);
		buttonBucket.setGraphic(new ImageView(new Image("file:src/main/resources/images/fill.png")));
		buttonBucket.setOnMouseClicked(e -> {
			selectTool(e);
		});
		Button buttonPicker = new Button();
		buttonPicker.setId("buttonPicker");
		buttonPicker.setPrefHeight(60);
		buttonPicker.setPrefWidth(60);
		buttonPicker.setGraphic(new ImageView(new Image("file:src/main/resources/images/pipette.png")));
		buttonPicker.setOnMouseClicked(e -> {
			selectTool(e);
		});

		gridPaneTools.add(buttonPencil, 0, 0);
		gridPaneTools.add(buttonEraser, 0, 1);
		gridPaneTools.add(buttonBucket, 1, 0);
		gridPaneTools.add(buttonPicker, 1, 1);

		Label labelTools = new Label("Tools");
		labelTools.setAlignment(Pos.BOTTOM_CENTER);
		labelTools.setContentDisplay(ContentDisplay.CENTER);
		labelTools.setPrefWidth(130);
		labelTools.setTextAlignment(TextAlignment.CENTER);

		vBoxTools.getChildren().addAll(gridPaneTools, labelTools);

		Separator separator1 = new Separator();
		separator1.setOrientation(Orientation.VERTICAL);

		VBox vBoxShapes = new VBox();
		vBoxShapes.setSpacing(20);
		vBoxShapes.setPadding(new Insets(10));

		GridPane gridPaneShapes = new GridPane();
		Button buttonRect = new Button();
		buttonRect.setId("buttonRect");
		buttonRect.setPrefHeight(60);
		buttonRect.setPrefWidth(60);
		buttonRect.setGraphic(new ImageView(new Image("file:src/main/resources/images/rectangle.png")));
		buttonRect.setOnMouseClicked(e -> {
			selectTool(e);
		});

		Button buttonRoundRect = new Button();
		buttonRoundRect.setId("buttonRoundRect");
		buttonRoundRect.setPrefHeight(60);
		buttonRoundRect.setPrefWidth(60);
		buttonRoundRect.setGraphic(new ImageView(new Image("file:src/main/resources/images/rounded-rectangle.png")));
		buttonRoundRect.setOnMouseClicked(e -> {
			selectTool(e);
		});

		Button buttonEllipse = new Button();
		buttonEllipse.setId("buttonEllipse");
		buttonEllipse.setPrefHeight(60);
		buttonEllipse.setPrefWidth(60);
		buttonEllipse.setGraphic(new ImageView(new Image("file:src/main/resources/images/ellipse-outline-shape-variant.png")));
		buttonEllipse.setOnMouseClicked(e -> {
			selectTool(e);
		});
		Label labelShapes = new Label("Shapes");
		labelShapes.setAlignment(Pos.BOTTOM_CENTER);
		labelShapes.setContentDisplay(ContentDisplay.CENTER);
		labelShapes.setPrefWidth(130);
		labelShapes.setTextAlignment(TextAlignment.CENTER);

		gridPaneShapes.add(buttonRect, 0, 0);
		gridPaneShapes.add(buttonRoundRect, 1, 0);
		gridPaneShapes.add(buttonEllipse, 2, 0);

		vBoxShapes.getChildren().addAll(gridPaneShapes, labelShapes);

		Separator separator2 = new Separator();
		separator2.setOrientation(Orientation.VERTICAL);

		hBoxHome.getChildren().addAll(vBoxTools, separator1, vBoxShapes, separator2);
		tabHome.setContent(hBoxHome);
		tabPane.getTabs().addAll(tabHome);

		root.getChildren().addAll(menuBar, tabPane);

		Scene scene = new Scene(root, 1100, 550);

		stage.setScene(scene);
		stage.setTitle("jabaPaint");
		stage.setMinWidth(1000);
		stage.setMinHeight(400);
		stage.show();
	}

	public static void main(String[] args) {
		launch(new String[0]);
	}

	// TODO: Kubek
	private void initialize() {
		/*context = canvas.getGraphicsContext2D();
		Button[] tools_ = { buttonPencil, eraserBtn, bucketBtn, pickerBtn, rectBtn, roundRectBtn, ellipseBtn };
		this.tools = tools_;
		bindZoom();
		bindSize();
		bindMouseXY();
		getCanvasHeightWidth();
		initializePinchZoom();
		initializeColors();
		initializeBrushSizes();
		setupDrawEvents();
		initializeHistory();*/
	}

	private void initializeHistory() {
		/*undoStack = new Stack<>();
		redoStack = new Stack<>();
		undoStack.add(new CanvasHistory(canvas.snapshot(null, null)));*/
	}

	private void setupDrawEvents() {
		/*canvas.setOnMousePressed((e) -> {
			if (selectedTool.isEmpty())
				return;
			if (e.isPrimaryButtonDown()) {
				context.setFill(color1);
				context.setStroke(color1);
			}
			if (e.isSecondaryButtonDown()) {
				context.setFill(color2);
				context.setStroke(color2);
			}
			switch (selectedTool) {
				case "pencil":
					prevX = e.getX();
					prevY = e.getY();
					context.strokeLine(prevX, prevY, prevX, prevY);
					break;
				case "eraser":
					prevX = e.getX();
					prevY = e.getY();
					context.setFill(Color.WHITE);
					context.setStroke(Color.WHITE);
					context.fillRect(e.getX(), e.getY(), lineWidth, lineWidth);
					break;
				case "bucket":
					if (e.isPrimaryButtonDown())
						bucketFill((int) e.getX(), (int) e.getY(), color1);
					if (e.isSecondaryButtonDown())
						bucketFill((int) e.getX(), (int) e.getY(), color2);
					break;
				case "picker":
					colorPicker1.setValue(
							canvas.snapshot(null, null).getPixelReader().getColor((int) e.getX(), (int) e.getY()));
					changeColor1();
					break;
				case "rect":
				case "roundrect":
				case "ellipse":
					prevX = e.getX();
					prevY = e.getY();
					break;
			}
		});

		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			if (selectedTool.isEmpty())
				return;
			if (e.isPrimaryButtonDown()) {
				context.setFill(color1);
				context.setStroke(color1);
			}
			if (e.isSecondaryButtonDown()) {
				context.setFill(color2);
				context.setStroke(color2);
			}

			switch (selectedTool) {
				case "pencil":
					context.strokeLine(prevX, prevY, e.getX(), e.getY());
					prevX = e.getX();
					prevY = e.getY();
					break;
				case "eraser":
					context.setFill(Color.WHITE);
					context.setStroke(Color.WHITE);
					context.fillRect(e.getX(), e.getY(), lineWidth, lineWidth);
					if (Math.abs(e.getX() - prevX) > Math.max(lineWidth / 2, 1)
							|| Math.abs(e.getY() - prevY) > Math.max(lineWidth / 2, 1)) {
						deleteExtraPoints(prevX, prevY, e.getX(), e.getY());
					}
					prevX = e.getX();
					prevY = e.getY();
					break;
				case "bucket":

					break;
				case "picker":

					break;
				case "rect":
					drawFileOnCanvas();
					context.strokeRect(Math.min(prevX, e.getX()), Math.min(prevY, e.getY()),
							Math.abs(e.getX() - prevX), Math.abs(e.getY() - prevY));
					break;
				case "roundrect":
				
					drawFileOnCanvas();
					context.strokeRoundRect(Math.min(prevX, e.getX()), Math.min(prevY, e.getY()),
							Math.abs(e.getX() - prevX), Math.abs(e.getY() - prevY),
							Math.min(Math.abs(e.getX() - prevX) / 5, 50), Math.min(Math.abs(e.getX() - prevX) / 5, 50));
					break;
				case "ellipse":
					drawFileOnCanvas();
					context.strokeOval(Math.min(prevX, e.getX()), Math.min(prevY, e.getY()),
							Math.abs(e.getX() - prevX), Math.abs(e.getY() - prevY));
					break;
				default:

					break;
			}
		});
		canvas.setOnMouseReleased((e) -> {
			if (undoStack.empty()) return;
			if (!compareImages(undoStack.peek().getImage(), (canvas.snapshot(null, null)))) {
				undoStack.add(new CanvasHistory(canvas.snapshot(null, null)));
				redoStack = new Stack<>();
			}
		});*/
	}

	private void bucketFill(int x, int y, Color fillColor) {
		/*WritableImage snapshot = canvas.snapshot(null, null);
		PixelReader pixelReader = snapshot.getPixelReader();
		LinkedList<PixelXY> queue = new LinkedList<>();
		queue.addLast(new PixelXY(x, y));
		while (!queue.isEmpty()) {
			PixelXY currentPixel = queue.pop();
			Color pixelColor = pixelReader.getColor(currentPixel.getX(), currentPixel.getY());
			if (!pixelColor.equals(fillColor)) {
				snapshot.getPixelWriter().setColor(currentPixel.getX(), currentPixel.getY(), fillColor);
				ArrayList<PixelXY> neighbors = getPixelNeighbors(currentPixel.getX(), currentPixel.getY(), snapshot);
				for (PixelXY neighbor : neighbors) {
					if (pixelReader.getColor(neighbor.getX(), neighbor.getY()).equals(pixelColor)) {
						queue.addLast(neighbor);
					}
				}
			}
		}
		context.drawImage(snapshot, 0, 0);*/
	}

	/*private ArrayList<PixelXY> getPixelNeighbors(int x, int y, WritableImage image) {
		ArrayList<PixelXY> neighbors = new ArrayList<>();
		if (x > 0)
			neighbors.add(new PixelXY(x - 1, y));
		if (x < image.getWidth() - 1)
			neighbors.add(new PixelXY(x + 1, y));
		if (y > 0)
			neighbors.add(new PixelXY(x, y - 1));
		if (y < image.getHeight() - 1)
			neighbors.add(new PixelXY(x, y + 1));

		return neighbors;
	}*/

	private void deleteExtraPoints(double prevX, double prevY, double x, double y) {
		/*if (Math.abs(x - prevX) > Math.max(lineWidth / 2, 1) || Math.abs(y - prevY) > Math.max(lineWidth / 2, 1)) {

			double midx = (int) ((x + prevX) / 2);
			double midy = (int) ((y + prevY) / 2);

			context.setFill(Color.WHITE);
			context.setStroke(Color.WHITE);
			context.fillRect(midx, midy, lineWidth, lineWidth);

			deleteExtraPoints(prevX, prevY, midx, midy);
			deleteExtraPoints(midx, midy, x, y);
		}*/
	}

	private void initializeBrushSizes() {
		/*ObservableList<String> options = FXCollections.observableArrayList("1 px", "3 px", "5 px", "8 px", "15 px");
		selectedSize.setItems(options);
		selectedSize.getSelectionModel().select(2);
		changeSize();*/

	}

	private void initializeColors() {
		/*colorPicker1.setValue(Color.BLACK);
		colorPicker2.setValue(Color.WHITE);
		color1 = colorPicker1.getValue();
		color2 = colorPicker2.getValue();*/
	}

	private void initializePinchZoom() {
		/*bigAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				if (e.getDeltaY() < 0) {
					scaleSlider.adjustValue(scaleSlider.getValue() - 25);
				}
				if (e.getDeltaY() > 0) {
					scaleSlider.adjustValue(scaleSlider.getValue() + 25);
				}
			}
		});
		canvasAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				e.consume();
				if (e.getDeltaY() < 0) {
					scaleSlider.adjustValue(scaleSlider.getValue() - 25);
				}
				if (e.getDeltaY() > 0) {
					scaleSlider.adjustValue(scaleSlider.getValue() + 25);
				}
			}
		});*/
	}

	private void getCanvasHeightWidth() {
		//canvasWidthHeight.setText((int) canvas.getWidth() + " x " + (int) canvas.getHeight() + "px");
	}

	private void bindMouseXY() {
		/*canvas.setOnMouseMoved((e) -> {
			pixelXY.setText((int) (e.getX() + 1) + ", " + (int) (e.getY() + 1));
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			pixelXY.setText((int) (e.getX() + 1) + ", " + (int) (e.getY() + 1));
		});*/
	}

	private void bindSize() {
		/*scrollPane.maxHeightProperty().bind(bigAnchor.heightProperty());
		scrollPane.maxWidthProperty().bind(bigAnchor.widthProperty());
		widthTextField.setText((int) canvas.getWidth() + "");
		heightTextField.setText((int) canvas.getHeight() + "");*/
	}

	private void bindZoom() {
		/*scaleSlider.valueProperty().addListener((e) -> {
			zoomLabel.setText((int) (scaleSlider.getValue()) + "%");
			double zoom = scaleSlider.getValue() / 100.0;
			canvasAnchor.setScaleX(zoom);
			canvasAnchor.setScaleY(zoom);
			group.setScaleX(zoom);
			group.setScaleY(zoom);
		});

		canvas.setOnMouseEntered((e) -> bigAnchor.setCursor(cursor));
		canvas.setOnMouseExited((e) -> bigAnchor.setCursor(Cursor.DEFAULT));*/
	}

	private void openFile() {
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

	private void saveFile() {
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

	private void saveAs() {
		/*ImageFxIO saver = new ImageFxIO((Stage) bigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		File f = saver.saveToFile(image);
		if (f != null) {
			projectName.setText(f.getName());
			file = f;
		}*/
	}

	private void undo() {
		/*if (undoStack.size() > 1) {
			redoStack.add(undoStack.pop());
			drawFileOnCanvas();
		}*/
	}

	private void drawFileOnCanvas() {
		/*if (file == null || undoStack.size() > 1) {
			context.drawImage(undoStack.peek().getImage(), 0, 0);
		} else {
			context.drawImage(image, 0, 0);
		}*/
	}

	private void redo() {
		/*if (!redoStack.empty()) {
			undoStack.add(redoStack.peek());
			CanvasHistory canvasHistory = redoStack.pop();
			context.drawImage(canvasHistory.getImage(), 0, 0);
		}*/
		
	}

	private void openAbout() {
		/*
		 * try {
		 * Desktop.getDesktop().browse(new
		 * URL("https://github.com/czmatejt9/javafx_team3").toURI());
		 * } catch (Exception ignored) {
		 * }
		 */
	}

	private void newFile() {
		/*context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		projectName.setText("Not Saved yet");
		file = null;
		initializeHistory();*/
	}

	private void selectTool(MouseEvent event) {
		/*Button b = (Button) event.getSource();
		b.getStyleClass().add("selected-tool");
		String id = b.getId();
		selectedTool = id;
		for (Button btn : tools) {
			if (btn != b) {
				btn.getStyleClass().remove("selected-tool");
			}
		}
		cursor = new ImageCursor();
		switch (id) {
			case "pencil":
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/pencil32.png")));
				break;
			case "eraser":
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/eraser32.png")));
				break;
			case "bucket":
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/fill32.png")));
				break;
			case "picker":
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/pipette32.png")));
				break;
			default:
				cursor = Cursor.CROSSHAIR;
				break;
		}*/
	}

	private void changeSize() {
		/*lineWidth = Integer
				.parseInt(selectedSize.getValue().split(" ")[0]);
		context.setLineWidth(lineWidth);*/
	}

	private void changeColor1() {
		//color1 = colorPicker1.getValue();
	}

	private void changeColor2() {
		// color2 = colorPicker2.getValue();
	}

	static private boolean compareImages(Image im1, Image im2) {
		for (int x = 0; x < im1.getWidth(); x++) {
			for (int y = 0; y < im1.getHeight(); y++) {
				if (!im1.getPixelReader().getColor(x, y).equals(im2.getPixelReader().getColor(x, y))) {
					return false;
				}
			}
		}
		return true;
	}

	private void changeDimensions() {
		/*int w = Integer.parseInt(widthTextField.getText());
		int h = Integer.parseInt(heightTextField.getText());
		canvas.setHeight(h);
		canvas.setWidth(w);
		canvasAnchor.setPrefHeight(h);
		canvasAnchor.setPrefWidth(w);
		getCanvasHeightWidth();
		double zoom = scaleSlider.getValue() / 100.0;
		canvasAnchor.setScaleX(zoom);
		canvasAnchor.setScaleY(zoom);
		group.setScaleX(zoom);
		group.setScaleY(zoom);*/
	}

	private void generateImage() {
		/*newFile();
		PixelWriter pxw = context.getPixelWriter();
		boolean raiseR = true;
		boolean raiseG = false;
		boolean raiseB = false;
		int r = 0;
		int g = 0;
		int b = 0;
		if (canvas.getHeight() >= canvas.getWidth()) {
			for (int x = 0; x < canvas.getWidth(); x++) {
				for (int y = 0; y < canvas.getHeight(); y++) {
					pxw.setColor(x, y, Color.rgb(r, g, b, 1.0));
					
					if (raiseR) {
						r++;
						if (r > 254) {
							r = 0;
							raiseR = false;
							raiseG = true;
						} else if (r < 1) {
							raiseR = true;
						}
					} else if (raiseG) {
						g++;
						if (g > 254) {
							g = 0;
							raiseG = false;
							raiseB = true;
						} else if (g < 1) {
							raiseG = true;
						}
					} else if (raiseB) {
						b++;
						if (b > 254) {
							b = 0;
							raiseB = false;
							raiseR = true;
						} else if (b < 1) {
							raiseB = true;
						}
					}
				}
			}
		} else {
			for (int y = 0; y < canvas.getHeight(); y++) {
				for (int x = 0; x < canvas.getWidth(); x++) {
					pxw.setColor(x, y, Color.rgb(r, g, b, 1.0));
					
					if (raiseR) {
						r++;
						if (r > 254) {
							r = 0;
							raiseR = false;
							raiseG = true;
						} else if (r < 1) {
							raiseR = true;
						}
					} else if (raiseG) {
						g++;
						if (g > 254) {
							g = 0;
							raiseG = false;
							raiseB = true;
						} else if (g < 1) {
							raiseG = true;
						}
					} else if (raiseB) {
						b++;
						if (b > 254) {
							b = 0;
							raiseB = false;
							raiseR = true;
						} else if (b < 1) {
							raiseB = true;
						}
					}
				}
			}
		}*/
	}
}
