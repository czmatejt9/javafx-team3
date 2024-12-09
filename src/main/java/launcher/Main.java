package launcher;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import classes.CanvasHistory;
import classes.ImageFxIO;
import classes.PixelXY;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {

	TextField textFieldHeight;
	TextField textFieldWidth;
	Slider sliderScale;
	Label labelPixelXY;
	Label labelProjectName;
	Label labelCanvasSize;
	Label labelZoom;
	AnchorPane canvasAnchor;
	Canvas canvas;
	AnchorPane bigAnchor;
	ScrollPane scrollPane;
	ColorPicker colorPicker1;
	ColorPicker colorPicker2;
	ComboBox<String> comboBoxSize;
	Group groupLower;
	Button buttonRect;
	Button buttonRoundRect;
	Button buttonEllipse;
	Button buttonPencil;
	Button buttonEraser;
	Button buttonBucket;
	Button buttonPicker;

	Button[] tools;
	Cursor cursor;
	String selectedTool = "";
	GraphicsContext context;
	double prevX, prevY;
	Stack<CanvasHistory> undoStack;
	Stack<CanvasHistory> redoStack;
	int lineWidth = 5;
	Color color1, color2;
	File file = null;
	Image image = null;

	@Override
	public void start(Stage stage) throws Exception {
		// --------------------using FXML--------------------
		// Parent root = FXMLLoader.load(getClass().getResource("/views/jabaPaint.fxml"));

		// --------------------using Java--------------------
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
		tabPane.setMaxHeight(160);
		tabPane.setMinHeight(160);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		Tab tabHome = new Tab("Home");
		HBox hBoxHome = new HBox();

		VBox vBoxTools = new VBox();
		vBoxTools.setSpacing(20);
		vBoxTools.setPadding(new Insets(10));

		GridPane gridPaneTools = new GridPane();
		buttonPencil = new Button();
		buttonPencil.setId("pencil");
		buttonPencil.setPrefSize(60, 60);
		buttonPencil.setGraphic(new ImageView(new Image("file:src/main/resources/images/pencil.png")));
		buttonPencil.setOnMouseClicked(e -> {
			selectTool(e);
		});
		buttonEraser = new Button();
		buttonEraser.setId("eraser");
		buttonEraser.setPrefSize(60, 60);
		buttonEraser.setGraphic(new ImageView(new Image("file:src/main/resources/images/eraser.png")));
		buttonEraser.setOnMouseClicked(e -> {
			selectTool(e);
		});
		buttonBucket = new Button();
		buttonBucket.setId("bucket");
		buttonBucket.setPrefSize(60, 60);
		buttonBucket.setGraphic(new ImageView(new Image("file:src/main/resources/images/fill.png")));
		buttonBucket.setOnMouseClicked(e -> {
			selectTool(e);
		});
		buttonPicker = new Button();
		buttonPicker.setId("picker");
		buttonPicker.setPrefSize(60, 60);
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
		buttonRect = new Button();
		buttonRect.setId("rect");
		buttonRect.setPrefSize(60, 60);
		buttonRect.setGraphic(new ImageView(new Image("file:src/main/resources/images/rectangle.png")));
		buttonRect.setOnMouseClicked(e -> {
			selectTool(e);
		});

		buttonRoundRect = new Button();
		buttonRoundRect.setId("roundrect");
		buttonRoundRect.setPrefSize(60, 60);
		buttonRoundRect.setGraphic(new ImageView(new Image("file:src/main/resources/images/rounded-rectangle.png")));
		buttonRoundRect.setOnMouseClicked(e -> {
			selectTool(e);
		});

		buttonEllipse = new Button();
		buttonEllipse.setId("ellipse");
		buttonEllipse.setPrefSize(60, 60);
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

		VBox vBoxSpecialTools = new VBox();
		vBoxSpecialTools.setPadding(new Insets(10));

		Separator separator3 = new Separator();
		separator3.setOrientation(Orientation.VERTICAL);

		VBox vBoxCustomizations = new VBox();
		vBoxCustomizations.setPadding(new Insets(10));

		HBox hBoxCustomizations = new HBox();
		hBoxCustomizations.setSpacing(20);

		VBox vBoxSize = new VBox();
		vBoxSize.setAlignment(Pos.TOP_CENTER);
		vBoxSize.setPrefSize(80, 80);
		vBoxSize.setSpacing(6);

		ImageView imageViewSize = new ImageView(new Image("file:src/main/resources/images/width.png"));
		comboBoxSize = new ComboBox<>();
		comboBoxSize.setPrefSize(70, 25);
		ObservableList<String> options = FXCollections.observableArrayList("1 px", "3 px", "5 px", "8 px", "15 px");
		comboBoxSize.setItems(options);
		comboBoxSize.getSelectionModel().select(2);
		comboBoxSize.setOnAction(e -> {
			changeSize();
		});

		vBoxSize.getChildren().addAll(imageViewSize, comboBoxSize);

		VBox vBoxColor = new VBox();
		vBoxColor.setAlignment(Pos.TOP_CENTER);
		vBoxColor.setPrefSize(80, 80);
		vBoxColor.setSpacing(6);

		colorPicker1 = new ColorPicker();
		colorPicker1.setOnAction(e -> {
			changeColor1();
		});
		colorPicker2 = new ColorPicker();
		colorPicker2.setOnAction(e -> {
			changeColor2();
		});

		vBoxColor.getChildren().addAll(colorPicker1, colorPicker2);

		hBoxCustomizations.getChildren().addAll(vBoxSize, vBoxColor);

		Label labelCustomizations = new Label("Customizations");
		labelCustomizations.setAlignment(Pos.BOTTOM_CENTER);
		labelCustomizations.setContentDisplay(ContentDisplay.CENTER);
		labelCustomizations.setPrefWidth(130);
		labelCustomizations.setTextAlignment(TextAlignment.CENTER);

		vBoxCustomizations.getChildren().addAll(hBoxCustomizations, labelCustomizations);

		hBoxHome.getChildren().addAll(
			vBoxTools, 
			separator1, 
			vBoxShapes, 
			separator2, 
			vBoxSpecialTools, 
			separator3,
			vBoxCustomizations
		);

		tabHome.setContent(hBoxHome);

		Tab tabCanvas = new Tab("Canvas");

		VBox vBoxCanvas = new VBox();
		GridPane gridPaneCanvas = new GridPane();

		Label labelWidth = new Label("Width");
		textFieldWidth = new TextField();
		
		Label labelHeight = new Label("Height");
		textFieldHeight = new TextField();
		
		Button buttonChangeDimensions = new Button("Change");
		buttonChangeDimensions.setOnAction(e -> {
			changeDimensions();
		});

		gridPaneCanvas.add(labelWidth, 0, 0);
		gridPaneCanvas.add(textFieldWidth, 1, 0);
		gridPaneCanvas.add(labelHeight, 0, 1);
		gridPaneCanvas.add(textFieldHeight, 1, 1);
		gridPaneCanvas.add(buttonChangeDimensions, 1, 2);

		Label labelDimensions = new Label("Dimensions");
		labelDimensions.setAlignment(Pos.BOTTOM_CENTER);
		labelDimensions.setContentDisplay(ContentDisplay.CENTER);
		labelDimensions.setPrefWidth(130);
		labelDimensions.setTextAlignment(TextAlignment.CENTER);
		
		vBoxCanvas.getChildren().addAll(gridPaneCanvas, labelDimensions);

		tabCanvas.setContent(vBoxCanvas);

		tabPane.getTabs().addAll(tabHome, tabCanvas);

		bigAnchor = new AnchorPane();
		bigAnchor.setStyle("fx-background-color: rgb(238,238,238)");
		VBox.setVgrow(bigAnchor, Priority.ALWAYS);
		scrollPane = new ScrollPane();
		AnchorPane.setBottomAnchor(scrollPane, 0.0);
		AnchorPane.setLeftAnchor(scrollPane, 0.0);
		AnchorPane.setRightAnchor(scrollPane, 0.0);
		AnchorPane.setTopAnchor(scrollPane, 0.0);

		Group groupUpper = new Group();
		groupLower = new Group();

		canvasAnchor = new AnchorPane();
		canvasAnchor.setPrefSize(1000, 400);
		canvasAnchor.setStyle("-fx-background-color: white");

		canvas = new Canvas();
		canvas.setHeight(400);
		canvas.setWidth(1000);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setBlurType(BlurType.GAUSSIAN);
		dropShadow.setColor(Color.GREY);
		dropShadow.setHeight(38.93);
		dropShadow.setWidth(20.77);
		dropShadow.setOffsetX(5);
		dropShadow.setOffsetY(5);
		dropShadow.setRadius(14.425);
		canvasAnchor.setEffect(dropShadow);

		canvasAnchor.getChildren().addAll(canvas);

		groupLower.getChildren().addAll(canvasAnchor);

		groupUpper.getChildren().addAll(groupLower);
		scrollPane.setContent(groupUpper);

		bigAnchor.getChildren().addAll(scrollPane);

		GridPane gridPaneBottomBar = new GridPane();
		gridPaneBottomBar.setPadding(new Insets(10));
		gridPaneBottomBar.setAlignment(Pos.BASELINE_CENTER);
		gridPaneBottomBar.setStyle("-fx-background-color: rgb(240,242,243)");
		gridPaneBottomBar.setSnapToPixel(false);
		gridPaneBottomBar.setHgap(50);

		labelPixelXY = new Label("0, 0");
		labelPixelXY.setPrefWidth(100);

		labelProjectName = new Label("Not Saved yet");
		labelProjectName.setPrefWidth(100);

		labelCanvasSize = new Label("Width, Height");
		labelCanvasSize.setPrefWidth(100);

		labelZoom = new Label("100%");
		labelZoom.setPrefWidth(100);

		sliderScale = new Slider();
		sliderScale.setMax(800);
		sliderScale.setMin(25);
		sliderScale.setMaxWidth(300);
		sliderScale.setValue(100);

		gridPaneBottomBar.add(labelPixelXY, 0, 0);
		gridPaneBottomBar.add(labelProjectName, 1, 0);
		gridPaneBottomBar.add(labelCanvasSize, 2, 0);
		gridPaneBottomBar.add(labelZoom, 3, 0);
		GridPane.setValignment(labelZoom, VPos.CENTER);
		gridPaneBottomBar.add(sliderScale, 4, 0);
		GridPane.setHgrow(sliderScale, Priority.NEVER);
		GridPane.setValignment(sliderScale, VPos.CENTER);

		root.getChildren().addAll(menuBar, tabPane, bigAnchor, gridPaneBottomBar);

		Scene scene = new Scene(root, 1100, 550);

		stage.setScene(scene);
		stage.setTitle("jabaPaint");
		stage.setMinWidth(1000);
		stage.setMinHeight(400);
		stage.show();

		initialize();
	}

	public static void main(String[] args) {
		launch(new String[0]);
	}

	private void initialize() {
		context = canvas.getGraphicsContext2D();
		Button[] tools_ = {
			buttonPencil, 
			buttonEraser, 
			buttonBucket, 
			buttonPicker, 
			buttonRect, 
			buttonRoundRect, 
			buttonEllipse 
		};
		this.tools = tools_;
		bindZoom();
		bindSize();
		bindMouseXY();
		getCanvasHeightWidth();
		initializePinchZoom();
		initializeColors();
		setupDrawEvents();
		initializeHistory();
	}

	private void initializeHistory() {
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		undoStack.add(new CanvasHistory(canvas.snapshot(null, null)));
	}

	private void setupDrawEvents() {
		canvas.setOnMousePressed((e) -> {
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
					prevX = e.getX();
					prevY = e.getY();
					break;
				case "roundrect":
					prevX = e.getX();
					prevY = e.getY();
					break;
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
		});
	}

	private void bucketFill(int x, int y, Color fillColor) {
		WritableImage snapshot = canvas.snapshot(null, null);
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
		context.drawImage(snapshot, 0, 0);
	}

	private ArrayList<PixelXY> getPixelNeighbors(int x, int y, WritableImage image) {
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
	}

	private void deleteExtraPoints(double prevX, double prevY, double x, double y) {
		if (Math.abs(x - prevX) > Math.max(lineWidth / 2, 1) || Math.abs(y - prevY) > Math.max(lineWidth / 2, 1)) {

			double midx = (int) ((x + prevX) / 2);
			double midy = (int) ((y + prevY) / 2);

			context.setFill(Color.WHITE);
			context.setStroke(Color.WHITE);
			context.fillRect(midx, midy, lineWidth, lineWidth);

			deleteExtraPoints(prevX, prevY, midx, midy);
			deleteExtraPoints(midx, midy, x, y);
		}
	}

	private void initializeColors() {
		colorPicker1.setValue(Color.BLACK);
		colorPicker2.setValue(Color.WHITE);
		color1 = colorPicker1.getValue();
		color2 = colorPicker2.getValue();
	}

	private void initializePinchZoom() {
		bigAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				if (e.getDeltaY() < 0) {
					sliderScale.adjustValue(sliderScale.getValue() - 25);
				}
				if (e.getDeltaY() > 0) {
					sliderScale.adjustValue(sliderScale.getValue() + 25);
				}
			}
		});
		canvasAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				e.consume();
				if (e.getDeltaY() < 0) {
					sliderScale.adjustValue(sliderScale.getValue() - 25);
				}
				if (e.getDeltaY() > 0) {
					sliderScale.adjustValue(sliderScale.getValue() + 25);
				}
			}
		});
	}

	private void getCanvasHeightWidth() {
		labelCanvasSize.setText((int) canvas.getWidth() + " x " + (int) canvas.getHeight() + "px");
	}

	private void bindMouseXY() {
		canvas.setOnMouseMoved((e) -> {
			labelPixelXY.setText((int) (e.getX() + 1) + ", " + (int) (e.getY() + 1));
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			labelPixelXY.setText((int) (e.getX() + 1) + ", " + (int) (e.getY() + 1));
		});
	}

	private void bindSize() {
		scrollPane.maxHeightProperty().bind(bigAnchor.heightProperty());
		scrollPane.maxWidthProperty().bind(bigAnchor.widthProperty());
		textFieldWidth.setText((int) canvas.getWidth() + "");
		textFieldHeight.setText((int) canvas.getHeight() + "");
	}

	private void bindZoom() {
		sliderScale.valueProperty().addListener((e) -> {
			labelZoom.setText((int) (sliderScale.getValue()) + "%");
			double zoom = sliderScale.getValue() / 100.0;
			canvasAnchor.setScaleX(zoom);
			canvasAnchor.setScaleY(zoom);
			groupLower.setScaleX(zoom);
			groupLower.setScaleY(zoom);
		});

		canvas.setOnMouseEntered((e) -> bigAnchor.setCursor(cursor));
		canvas.setOnMouseExited((e) -> bigAnchor.setCursor(Cursor.DEFAULT));
	}

	private void openFile() {
		ImageFxIO loader = new ImageFxIO((Stage) bigAnchor.getScene().getWindow());
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
		labelProjectName.setText(file.getName());
	}

	private void saveFile() {
		ImageFxIO saver = new ImageFxIO((Stage) bigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		if (file == null) {
			File f = saver.saveToFile(image);
			if (f != null) {
				file = f;
				labelProjectName.setText(file.getName());
			}
		} else {
			saver.saveToFile(image, file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase(), file);
		}
	}

	private void saveAs() {
		ImageFxIO saver = new ImageFxIO((Stage) bigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		File f = saver.saveToFile(image);
		if (f != null) {
			labelProjectName.setText(f.getName());
			file = f;
		}
	}

	private void undo() {
		if (undoStack.size() > 1) {
			redoStack.add(undoStack.pop());
			drawFileOnCanvas();
		}
	}

	private void drawFileOnCanvas() {
		if (file == null || undoStack.size() > 1) {
			context.drawImage(undoStack.peek().getImage(), 0, 0);
		} else {
			context.drawImage(image, 0, 0);
		}
	}

	private void redo() {
		if (!redoStack.empty()) {
			undoStack.add(redoStack.peek());
			CanvasHistory canvasHistory = redoStack.pop();
			context.drawImage(canvasHistory.getImage(), 0, 0);
		}
		
	}

	private void openAbout() {
		try {
			Desktop.getDesktop().browse(new
			URL("https://github.com/czmatejt9/javafx_team3").toURI());
		} catch (Exception ignored) {}
	}

	private void newFile() {
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		labelProjectName.setText("Not Saved yet");
		file = null;
		initializeHistory();
	}

	private void selectTool(MouseEvent event) {
		Button b = (Button) event.getSource();
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
				Image image = new Image(getClass().getResourceAsStream("/images/pencil32.png"));
				cursor = new ImageCursor(image, 0, image.getHeight());
				break;
			case "eraser":
				Image image2 = new Image(getClass().getResourceAsStream("/images/eraser32.png"));
				cursor = new ImageCursor(image2, 0, image2.getHeight());
				break;
			case "bucket":
				Image image3 = new Image(getClass().getResourceAsStream("/images/fill32.png"));
				cursor = new ImageCursor(image3, 0, image3.getHeight());
				break;
			case "picker":
				Image image4 = new Image(getClass().getResourceAsStream("/images/pipette32.png"));
				cursor = new ImageCursor(image4, 0, image4.getHeight());
				break;
			default:
				cursor = Cursor.CROSSHAIR;
				break;
		}
	}

	private void changeSize() {
		lineWidth = Integer
				.parseInt(comboBoxSize.getValue().split(" ")[0]);
		context.setLineWidth(lineWidth);
	}

	private void changeColor1() {
		color1 = colorPicker1.getValue();
	}

	private void changeColor2() {
		color2 = colorPicker2.getValue();
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
		int w = Integer.parseInt(textFieldWidth.getText());
		int h = Integer.parseInt(textFieldHeight.getText());
		canvas.setHeight(h);
		canvas.setWidth(w);
		canvasAnchor.setPrefHeight(h);
		canvasAnchor.setPrefWidth(w);
		getCanvasHeightWidth();
		double zoom = sliderScale.getValue() / 100.0;
		canvasAnchor.setScaleX(zoom);
		canvasAnchor.setScaleY(zoom);
		groupLower.setScaleX(zoom);
		groupLower.setScaleY(zoom);
	}

	private void generateImage() {
		newFile();
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
		}
	}
}
