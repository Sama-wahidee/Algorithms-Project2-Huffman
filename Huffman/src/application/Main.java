package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Main extends Application {
	Compress compress;
	Decompress decompress;
	File file;
	String filePath;
	Stage primaryStage = new Stage();

	public void start(Stage primaryStage) {
		start();

	}

	public void start() {
		BorderPane root = new BorderPane();
		VBox bVB = new VBox();
		Font font = new Font(20);
		Button compressBT = new Button("Compress File");
		compressBT.setFont(font);
		compressBT.setStyle(
				"-fx-background-color:#0000 ; -fx-border-color: #ffffff; -fx-border-width: 2px;-fx-text-fill: #ffffff");
		compressBT.setOnAction(e -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Load File");
			file = fileChooser.showOpenDialog(null);
			filePath = file.getPath();
			if (file != null) {

				try {
					compress = new Compress(filePath);
					try {
						compress.readFile();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					compress.createHeap();
					compress.writeHuffmanCode();
					dialog(AlertType.INFORMATION, "Compression Complete");
					secoundStage();

				} catch (IOException e1) {
					dialog(AlertType.ERROR, "Error, Compression process did NOT Complete");

				} finally {
				}

			}

		});
		Button extractBT = new Button("Extract File");
		extractBT.setFont(font);
		extractBT.setStyle(
				"-fx-background-color:#0000 ; -fx-border-color: #ffffff; -fx-border-width: 2px;-fx-text-fill: #ffffff");

		extractBT.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HUF", "*.hfm"));
			fileChooser.setTitle("Load File");
			file = fileChooser.showOpenDialog(null);
			filePath = file.getPath();
			if (!filePath.endsWith(".hfm")) {
				dialog(AlertType.ERROR, "You should select files with extention .huf , retry again!");
			}
			if (filePath.endsWith(".hfm")) {

				decompress = new Decompress(filePath);
				try {
					decompress.readHuffFile();
					primaryStage.close();
					dialog(AlertType.INFORMATION, "Extraction Complete");

				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		Label space = new Label("\n\n\n\n\n");
		bVB.getChildren().addAll(space, compressBT, extractBT);
		bVB.setAlignment(Pos.CENTER);
		bVB.setSpacing(50);
		root.setCenter(bVB);
		Scene scene = new Scene(root, 394, 700);
		backGround(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Huffman");
		primaryStage.show();
	}

	public void secoundStage() {
		primaryStage.close();
		Stage tabsS = new Stage();
		TabPane tabsP = new TabPane();
		Tab tableT = new Tab("Haffman Table");
		tableT.setStyle("-fx-background-color: #2F5C86;");
		TableView<Huffman> table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setStyle(
				"-fx-table-cell-border-color: #FFFFFF; -fx-table-border-color: #2F5C86; -fx-border-color: #2F5C86;");

		TableColumn<Huffman, Integer> byteCountColumn = new TableColumn<>("Byte");
		byteCountColumn.setCellValueFactory(new PropertyValueFactory<>("bytee"));
		byteCountColumn.setStyle(
				"-fx-background-color: #2F5C86; -fx-alignment: CENTER; -fx-font-size: 20px; -fx-font-weight: bold;");

		TableColumn<Huffman, Byte> asciiColumn = new TableColumn<>("Character");
		asciiColumn.setCellValueFactory(new PropertyValueFactory<>("ascii"));
		asciiColumn.setStyle(
				"-fx-background-color: #2F5C86; -fx-alignment: CENTER; -fx-font-size: 20px; -fx-font-weight: bold;");

		TableColumn<Huffman, String> huffmanCodeColumn = new TableColumn<>("Huffman Code");
		huffmanCodeColumn.setCellValueFactory(new PropertyValueFactory<>("huffman"));
		huffmanCodeColumn.setStyle(
				"-fx-background-color: #2F5C86; -fx-alignment: CENTER; -fx-font-size: 20px; -fx-font-weight: bold;");

		TableColumn<Huffman, Integer> intCountColumn = new TableColumn<>("Frequency");
		intCountColumn.setCellValueFactory(new PropertyValueFactory<>("intCount"));
		intCountColumn.setStyle(
				"-fx-background-color: #2F5C86; -fx-alignment: CENTER; -fx-font-size: 20px; -fx-font-weight: bold;");

		TableColumn<Huffman, Integer> lengthColumn = new TableColumn<>("Length");
		lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
		lengthColumn.setStyle(
				"-fx-background-color: #2F5C86; -fx-alignment: CENTER; -fx-font-size: 20px; -fx-font-weight: bold;");

		table.getColumns().addAll(byteCountColumn, asciiColumn, huffmanCodeColumn, lengthColumn, intCountColumn);

		Counter[] counters = compress.getCounter();
		Huffman[] huffTable = compress.getHuffTable();

		// Check if the lengths match
		
		if (counters.length != huffTable.length) {
		    // Handle the mismatch, print an error, throw an exception, or take appropriate action
		    System.err.println("Error: Counter and HuffTable lengths do not match.");
		} else {
		    for (int i = 0; i < counters.length; i++) {
		        int counter = counters[i].intCount;
		        Huffman huffman = huffTable[i];

		        if (counter != 0 && huffman != null) {
		            // Add the Huffman instance directly to the table
		            table.getItems().add(huffman);
		        }
		    }
		}
		tableT.setContent(table);
		Tab statisticsT = new Tab("Statistics");
		statisticsT.setStyle("-fx-background-color: #2F5C86;");
		Label headerL = new Label("Header: ");
		headerL.setFont(new Font(20));
		headerL.setStyle(
				"-fx-text-fill: #ffffff;-fx-alignment: CENTER;-fx-control-inner-background: #2F5C86;-fx-border-color: #2F5C86;");
		TextArea headerTA = new TextArea();
		headerTA.setEditable(false);
		headerTA.prefWidthProperty().bind(tabsS.widthProperty().subtract(90));
		headerTA.prefHeightProperty().bind(tabsS.heightProperty().divide(2).subtract(50));
		headerTA.setFont(new Font(20));
		headerTA.setWrapText(true);
		headerTA.setFont(new Font(15));
		headerTA.setStyle(
				"-fx-text-fill: #ffffff;-fx-alignment: CENTER;-fx-control-inner-background: #2F5C86;-fx-border-color: #2F5C86;");
		Header header = compress.getHeader();
		headerTA.setText("File Name: " + header.getFileName() + "\n\nFile Size: " + header.getBytes().length + " Byte");
		headerTA.setText(headerTA.getText() + "\n\nByte: Huffman Code \n");
		Huffman table1[] = compress.getHuffTable();
		for (int i = 0; i < table1.length; i++) {
		    String info = table1[i].getBytee() + ": " + table1[i].getHuffman() + "\n";
		    headerTA.appendText(info);
		}
		Label statisticsL = new Label("Statistics: ");
		statisticsL.setFont(new Font(20));
		statisticsL.setStyle(
				"-fx-text-fill: #ffffff;-fx-alignment: CENTER;-fx-control-inner-background: #2F5C86;-fx-border-color: #2F5C86;");
		TextArea statisticsTA = new TextArea();
		statisticsTA.setEditable(false);
		statisticsTA.prefWidthProperty().bind(tabsS.widthProperty().subtract(90));
		statisticsTA.prefHeightProperty().bind(tabsS.heightProperty().divide(2).subtract(50));
		statisticsTA.setFont(new Font(20));
		statisticsTA.setWrapText(true);
		statisticsTA.setFont(new Font(15));
		int n = compress.getHuffnumOfByte() + compress.getHeader().getBytes().length;
		System.out.println(compress.getCompressedFileSize());
		statisticsTA.setStyle(
				"-fx-text-fill: #ffffff;-fx-alignment: CENTER;-fx-control-inner-background: #2F5C86;-fx-border-color: #2F5C86;");
		statisticsTA.setText("Orginal File Size: " + compress.getNumOfByte() + " Byte\n " + "\nCompressed File Size: "
				+ compress.getCompressedFileSize() + " Byte\n " + "\nCompression Ratio: " + compress.getRatio()
				+ "%\n ");

		HBox hb1 = new HBox();
		hb1.setSpacing(10);
		hb1.setAlignment(Pos.CENTER);
		hb1.getChildren().addAll(headerL, headerTA);
		hb1.setStyle("-fx-control-inner-background: #2F5C86;-fx-border-color: #2F5C86;");
		HBox hb2 = new HBox();
		hb2.setSpacing(0);
		hb2.setAlignment(Pos.CENTER);
		hb2.getChildren().addAll(statisticsL, statisticsTA);
		Button startOver = new Button("Start over");
		startOver.setFont(new Font(20));
		startOver.setStyle(
				"-fx-background-color: #ffffff; -fx-border-color: #2F5C86; -fx-border-width: 2px;-fx-text-fill:#2F5C86");
		startOver.setOnAction(e -> {
			tabsS.close();
			primaryStage.show();
		});
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(hb1, hb2, startOver);
		Pane p = new Pane(vb);
		p.setBackground(new javafx.scene.layout.Background(
				new javafx.scene.layout.BackgroundFill(Color.valueOf("#2F5C86"), null, null)));

		statisticsT.setContent(p);
		tabsP.getTabs().addAll(tableT, statisticsT);
		tabsS.setFullScreen(true);
		Scene tabsSC = new Scene(tabsP, 1920, 1080);
		tabsS.setScene(tabsSC);
		tabsS.setTitle("Huffman code");
		tabsS.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private TableView<Object> createDynamicTable(Object[] dataArray) {
		TableView<Object> table = new TableView<>();
		ObservableList<Object> data = FXCollections.observableArrayList(dataArray);

		// Get the first element in the array to determine its type
		Object firstElement = dataArray.length > 0 ? dataArray[0] : null;

		if (firstElement != null) {
			// Iterate through the properties of the first element using reflection
			for (java.lang.reflect.Field field : firstElement.getClass().getDeclaredFields()) {
				if (field.getType().equals(String.class)) {
					// Handle String properties
					TableColumn<Object, String> column = new TableColumn<>(field.getName());
					column.setCellValueFactory(d -> {
						try {
							return new SimpleObjectProperty<>(field.get(d.getValue()).toString());
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							return new SimpleObjectProperty<>("");
						}
					});
					table.getColumns().add(column);
				} else if (field.getType().equals(Number.class)) {
					// Handle Number properties
					TableColumn<Object, Number> column = new TableColumn<>(field.getName());
					column.setCellValueFactory(d -> {
						try {
							return new SimpleObjectProperty<>((Number) field.get(d.getValue()));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							return new SimpleObjectProperty<>(0);
						}
					});
					table.getColumns().add(column);
				}
				// Add more conditions as needed for other data types
			}
		}

		// Set the items
		table.setItems(data);

		return table;
	}

	public void backGround(Pane p) {
		try {
			BackgroundImage bGI = new BackgroundImage(new Image(getClass().getResourceAsStream("/resources/Haffman.jpg")),
					BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					BackgroundSize.DEFAULT);
			Background bGround = new Background(bGI);
			p.setBackground(bGround);
		} catch (Exception e) {
			dialog(AlertType.ERROR, "Sorry, there was an error while uploading the background");
		}

	}

	public void dialog(AlertType t, String s) {
		Alert alert = new Alert(t);
		alert.setTitle("Dialog");
		alert.setHeaderText("");
		alert.setContentText(s);
		alert.showAndWait();
	}
}
