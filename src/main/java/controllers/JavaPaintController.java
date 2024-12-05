package controllers;

// import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import classes.CanvasHistory;
import classes.ImageFxIO;
import classes.PixelXY;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JavaPaintController {

	@FXML
	Slider ScaleSlider;
	@FXML
	Label pixelXY;
	@FXML
	Label projectName;
	@FXML
	Label canvasWidthHeight;
	@FXML
	AnchorPane CanvasAnchor;
	@FXML
	Canvas canvas;
	@FXML
	AnchorPane BigAnchor;
	@FXML
	ScrollPane ScrollPane;
	@FXML
	Label zoomLabel;
	@FXML
	Group group;
	@FXML
	Button pencilBtn;
	@FXML
	Button EraserBtn;
	@FXML
	Button bucketBtn;
	@FXML
	Button pickerBtn;
	@FXML
	Button rectBtn;
	@FXML
	Button roundRectBtn;
	@FXML
	Button ellipseBtn;
	@FXML
	ComboBox<String> selectedSize;
	@FXML
	ColorPicker colorPicker1;
	@FXML
	ColorPicker colorPicker2;
	@FXML
	TextField heightTextField;
	@FXML
	TextField widthTextField;

	boolean zoomLocked = true;
	Button[] tools;
	Cursor cursor;
	String selectedTool = "";
	GraphicsContext context;
	double prevX, prevY;
	Stack<CanvasHistory> undoStack;
	Stack<CanvasHistory> redoStack;
	int lineWidth = 1;
	Color color1, color2;
	File file = null;
	Image image = null;

	public void initialize() {
		context = canvas.getGraphicsContext2D();
		Button[] tools_ = { pencilBtn, EraserBtn, bucketBtn, pickerBtn, rectBtn, roundRectBtn, ellipseBtn };
		this.tools = tools_;
		bindSize();
		bindZoom();
		bindMouseXY();
		getCanvasHeightWidth();
		initializePinchZoom();
		initializeColors();
		initializeSizes();
		SetupDrawEvents();
		initializeHistory();
		
	}

	private void initializeHistory() {
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		undoStack.add(new CanvasHistory(canvas.snapshot(null, null)));
	}

	private void SetupDrawEvents() {
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
					if (pixelReader.getColor(neighbor.getX(), neighbor.getY()).equals(pixelColor))
						queue.addLast(neighbor);
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

	private void initializeSizes() {
		ObservableList<String> options = FXCollections.observableArrayList("1 px", "3 px", "5 px", "8 px", "15 px");
		selectedSize.setItems(options);
		selectedSize.getSelectionModel().select(2);
		changeSize();

	}

	private void initializeColors() {
		colorPicker1.setValue(Color.BLACK);
		colorPicker2.setValue(Color.WHITE);
		color1 = colorPicker1.getValue();
		color2 = colorPicker2.getValue();
	}

	private void initializePinchZoom() {
		BigAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				if (e.getDeltaY() < 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() - 25);

				if (e.getDeltaY() > 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() + 25);
			}
		});
		CanvasAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				e.consume(); // prevent scrolling while zooming
				if (e.getDeltaY() < 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() - 25);
				if (e.getDeltaY() > 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() + 25);
			}
		});
	}

	private void getCanvasHeightWidth() {
		canvasWidthHeight.setText((int) canvas.getWidth() + " x " + (int) canvas.getHeight() + "px");
	}

	private void bindMouseXY() {
		canvas.setOnMouseMoved((e) -> {
			pixelXY.setText((int) (e.getX() + 1) + ", " + (int) (e.getY() + 1));
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			pixelXY.setText((int) (e.getX() + 1) + ", " + (int) (e.getY() + 1));
		});
	}

	private void bindSize() {
		ScrollPane.maxHeightProperty().bind(BigAnchor.heightProperty());
		ScrollPane.maxWidthProperty().bind(BigAnchor.widthProperty());
		widthTextField.setText((int) canvas.getWidth() + "");
		heightTextField.setText((int) canvas.getHeight() + "");
	}

	private void bindZoom() {
		ScaleSlider.valueProperty().addListener((e) -> {
			if (!zoomLocked || ScaleSlider.getValue() % 25 == 0) {
				zoomLabel.setText((int) (ScaleSlider.getValue()) + "%");
				double zoom = ScaleSlider.getValue() / 100.0;
				group.setScaleX(zoom);
				group.setScaleY(zoom);
			}
		});

		canvas.setOnMouseEntered((e) -> BigAnchor.setCursor(cursor));
		canvas.setOnMouseExited((e) -> BigAnchor.setCursor(Cursor.DEFAULT));
	}

	@FXML
	public void openFile() {
		ImageFxIO loader = new ImageFxIO((Stage) BigAnchor.getScene().getWindow());
		Object[] result = loader.openFromFile();
		if (result == null) return;
		image = (Image) result[0];
		File f = (File) result[1];
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		canvas.setHeight(h);
		canvas.setWidth(w);
		CanvasAnchor.setPrefHeight(h);
		CanvasAnchor.setPrefWidth(w);
		getCanvasHeightWidth();

		initializeHistory();
		
		initializeColors();
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		context.drawImage(image, 0, 0);
		file = f;
		projectName.setText(file.getName());
	}

	@FXML
	public void saveFile() {
		ImageFxIO saver = new ImageFxIO((Stage) BigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		if (file == null) {
			File f = saver.saveToFile(image);
			if (f != null) {
				file = f;
				projectName.setText(file.getName());
			}
		} else {
			saver.saveToFile(image, file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase(), file);
		}
	}

	@FXML
	public void saveAs() {
		ImageFxIO saver = new ImageFxIO((Stage) BigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		File f = saver.saveToFile(image);
		if (f != null) {
			projectName.setText(f.getName());
			file = f;
		}
	}

	@FXML
	public void closeApp() {
		System.exit(0);
	}

	@FXML
	public void undo() {
		if (undoStack.size() > 1) {
			redoStack.add(undoStack.pop());
			if (undoStack.peek().DimensionsChanged()) {
				changeDimensions(undoStack.peek().getWidth(), undoStack.peek().getHeight());
			}
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

	@FXML
	public void redo() {
		if (!redoStack.empty()) {
			undoStack.add(redoStack.peek());
			CanvasHistory canvasHistory = redoStack.pop();
			if (canvasHistory.DimensionsChanged()) {
				changeDimensions(canvasHistory.getWidth(), canvasHistory.getHeight());
			}
			context.drawImage(canvasHistory.getImage(), 0, 0);
		}
		
	}

	

	@FXML
	public void openAbout() {
		/*
		 * try {
		 * Desktop.getDesktop().browse(new
		 * URL("https://github.com/czmatejt9/javafx_team3").toURI());
		 * } catch (Exception ignored) {
		 * }
		 */
	}

	@FXML
	public void newFile() {
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		projectName.setText("Not Saved yet");
		file = null;
		initializeHistory();
		
	}

	@FXML
	public void selectTool(MouseEvent event) {
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
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/pencil32.png")),
						0, 32);
				break;
			case "eraser":
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/eraser32.png")),
						10, 30);
				break;
			case "bucket":
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/fill32.png")),
						3, 28);
				break;
			case "picker":
				cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/pipette32.png")),
						4, 29);
				break;
			default:
				cursor = Cursor.CROSSHAIR;
				break;
		}

	}

	@FXML
	public void changeSize() {
		lineWidth = Integer
				.parseInt(selectedSize.getValue().toString().substring(0,
						selectedSize.getValue().toString().indexOf(" ")));
		context.setLineWidth(lineWidth);
	}

	@FXML
	public void changeColor1() {
		color1 = colorPicker1.getValue();
	}

	@FXML
	public void changeColor2() {
		color2 = colorPicker2.getValue();
	}

	static public boolean compareImages(Image im1, Image im2) {
		for (int i = 0; i < im1.getWidth(); i++) {
			for (int j = 0; j < im1.getHeight(); j++) {
				if (!im1.getPixelReader().getColor(i, j).equals(im2.getPixelReader().getColor(i, j)))
					return false;
			}
		}
		return true;
	}

	@FXML
	public void discardDimensions() {
		widthTextField.setText((int) canvas.getWidth() + "");
		heightTextField.setText((int) canvas.getHeight() + "");
	}

	@FXML
	public void applyDimensions() {
		int w = Integer.parseInt(widthTextField.getText());
		int h = Integer.parseInt(heightTextField.getText());
		if (w < 1 || h < 1) {
			widthTextField.setText((int) canvas.getWidth() + "");
			heightTextField.setText((int) canvas.getHeight() + "");
			return;
		}

		undoStack.add(new CanvasHistory((int) canvas.getWidth(), (int) canvas.getHeight(),
				undoStack.pop().getImage()));

		changeDimensions(w, h);

		undoStack.add(new CanvasHistory((int) canvas.getWidth(), (int) canvas.getHeight(),
				canvas.snapshot(null, null)));
		
	}

	private void changeDimensions(int w, int h) {
		try {
			canvas.setHeight(h);
			canvas.setWidth(w);
			CanvasAnchor.setPrefHeight(h);
			CanvasAnchor.setPrefWidth(w);
			getCanvasHeightWidth();
			double zoom = ScaleSlider.getValue() / 100.0;
			group.setScaleX(zoom + 0.01);
			group.setScaleY(zoom + 0.01);
			Thread.sleep(1);
			group.setScaleX(zoom);
			group.setScaleY(zoom);
		} catch (InterruptedException e) {
			widthTextField.setText((int) canvas.getWidth() + "");
			heightTextField.setText((int) canvas.getHeight() + "");
		}
	}

	@FXML
	public void exitedCanvasTab() {
		discardDimensions();
	}

	@FXML
	public void checkEscape(KeyEvent e) {
		if (e.getCode() == KeyCode.ESCAPE) {
			context.drawImage(undoStack.peek().getImage(), 0, 0);
		}
	}

}
