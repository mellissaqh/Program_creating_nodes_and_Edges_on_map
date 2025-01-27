import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.scene.control.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
//PROG2 VT2024, Inlämningsuppgift, del 2
//Grupp 025
//Olle Lindkvist olli5947
//Mellissa Qholizadeh meqho4654
public class PathFinder<N> extends Application{
	
	private ListGraph<Location> graph = new ListGraph<>();
	private boolean unsavedChanges = false;
	private String imageURL = "file:europa.gif";
	
	private Stage primaryStage;
	private ImageView imageView;
	private BorderPane borderPane;
    private Scene scene; 
    private Button btnNewPlace;
    private Pane outputArea;
	@Override
	public void start(Stage primaryStage) throws Exception {	
		
		this.primaryStage = primaryStage;
		primaryStage.setTitle("PathFinder");
		borderPane = new BorderPane();
	    imageView = new ImageView();
	    
		//Nedan kommer menybaren
		MenuBar menu = new MenuBar();
		menu.setId("menu");
		Menu menuFile = new Menu("File");
		menuFile.setId("menuFile");
		MenuItem menuNewMap = new MenuItem("New Map");
		menuNewMap.setId("menuNewMap");
		MenuItem menuOpenFile = new MenuItem("Open");
		menuOpenFile.setId("menuOpenFile");
		MenuItem menuSaveFile = new MenuItem("Save");
		menuSaveFile.setId("menuSaveFile");
		MenuItem menuSaveImage = new MenuItem("Save Image");
		menuSaveImage.setId("menuSaveImage");
		MenuItem menuExit = new MenuItem("Exit");
		menuExit.setId("menuExit");
		
		menuFile.getItems().addAll(menuNewMap, menuOpenFile, menuSaveFile, menuSaveImage, menuExit);
        menu.getMenus().add(menuFile);
        
        //Nedan skapas knapparna
        Button btnFindPath = new Button("Find Path");
        btnFindPath.setId("btnFindPath");
        Button btnShowConnection = new Button("Show Connection");
        btnShowConnection.setId("btnShowConnection");
        btnNewPlace = new Button("New Place");
        btnNewPlace.setId("btnNewPlace");
        Button btnNewConnection = new Button("New Connection");
        btnNewConnection.setId("btnNewConnection");
        Button btnChangeConnection = new Button("Change Connection");
        btnChangeConnection.setId("btnChangeConnection");
        
        //Här läggs knapparna till i en layout
        HBox buttonLayout = new HBox(5); 
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(btnFindPath, btnShowConnection, btnNewPlace, btnNewConnection, btnChangeConnection);
        
        //Här läggs menyn till ovanför knapparna
        VBox layout = new VBox(10);
        layout.getChildren().addAll(menu, buttonLayout);
        
        //Här lägger vi till imageView och layout i borderpane
        borderPane.setTop(layout);
        outputArea = new Pane(imageView);
        outputArea.setId("outputArea");
        borderPane.setCenter(outputArea);
 
        //Här skapas scenens storlek
        scene = new Scene(borderPane, 500, 75);
        primaryStage.setScene(scene);
        primaryStage.show();
		
        //Här läggs funktionalitet till för när menyvalen klickas
        menuNewMap.setOnAction(e -> handleNewMap());
        menuOpenFile.setOnAction(e -> handleOpen());
        menuSaveFile.setOnAction(e -> handleSave());
        menuSaveImage.setOnAction(e -> handleSaveImage());
        menuExit.setOnAction(e -> handleExit());
        
        //Här är funktionalliteten för knapparna när de klickas
        btnFindPath.setOnAction(e -> handleFindPath());
        btnShowConnection.setOnAction(e -> handleShowConnection());
        btnNewPlace.setOnAction(e -> handleNewPlace());
        btnNewConnection.setOnAction(e -> handleNewConnection());
        btnChangeConnection.setOnAction(e -> handleChangeConnection());	
	}
	
	//Menyvalet New map
    private void handleNewMap() {
    	//Rensar tidigare data
    	clearGraphData();
    	
    	try {
        //Här laddas bilden från URL
    	imageURL = "file:europa.gif";
    	Image image = new Image(imageURL);
        if (image.isError()) {
            throw new IOException("Image could not be loaded.");
        }
    	imageView.setImage(image);
    	
    	//Anpassar fönsterstorleken till bildens storlek med lite padding
        Platform.runLater(() -> {
            double newWidth = image.getWidth() + 20; 
            double newHeight = image.getHeight() + 80;
            primaryStage.setWidth(newWidth);
            primaryStage.setHeight(newHeight);
        });
    	
    	
    	unsavedChanges = true;
    } catch (IOException e) {
        showErrorMessage("Failed to load map image: " + e.getMessage());
    }
    }
    //Menyvalet Open
    private void handleOpen() {
        if (unsavedChanges) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Warning!");
            alert.setHeaderText("Unsaved changes, open new file anyway?");
            
            // Skapa OK och Cancel knappar
            ButtonType buttonTypeOK = new ButtonType("OK", ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

            // Hantera användarens val om den trycker på cancel
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeCancel) {
                return; 
            }
        }	
        
        //kollar så att den korrekta bilden finns
        File file = new File("europa.graph");
        if (!file.exists()) {
            showErrorMessage("File europa.graph does not exist!");
           return;
       }
        
       //try-with-resources används för att garantera att filer stängs korrekt efter de har använts
       try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Rensa tidigare data
            clearGraphData();

            // Läs URL till bilden
            String imageUrl = reader.readLine();
            if (imageUrl != null) {
            	imageURL = imageUrl;
                imageView.setImage(new Image(imageUrl));
            }
            // Läs in de sparade platserna
            String nodesLine = reader.readLine();
            if (nodesLine != null) {
                String[] nodeParts = nodesLine.split(";");
                for (int i = 0; i < nodeParts.length; i += 3) {
                    String nodeName = nodeParts[i];
                    double x = Double.parseDouble(nodeParts[i + 1]);
                    double y = Double.parseDouble(nodeParts[i + 2]);
                    
                         // Skapa noden och lägg till den i grafen
                    Location node = new Location(x, y, 10, Color.BLUE , nodeName);
                    graph.add(node);
                    graph.setNodeCoordinates(node, x, y);
                    // Lägg till noden till outputArea för att visa den
                    outputArea.getChildren().add(node);
                    
                    
                    // Lägg till text för platsens namn
                    Text placeText = new Text(x + 10, y, nodeName);
                    placeText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    placeText.setFill(Color.BLACK);
                    outputArea.getChildren().add(placeText);
                    
                    node.setUserData(false);
                    
                    
                    // Lägg till event-handler för klick
                    node.setOnMouseClicked(circleEvent -> {
                        handlePlaceClick(node);
                    });
     
                }
            }
         // Läs in de sparade förbindelserna
            String line;
            while ((line = reader.readLine()) != null) {
                String[] edgeParts = line.split(";");
                if (edgeParts.length == 4) {
                    String fromNodeName = edgeParts[0];
                    String toNodeName = edgeParts[1];
                    String edgeName = edgeParts[2];
                    double weightDouble = Double.parseDouble(edgeParts[3]); 
                    int weight = (int) weightDouble;     

                    Location fromNode = (Location) graph.getNodeByName(fromNodeName);
                    Location toNode = (Location) graph.getNodeByName(toNodeName);
                    
                    if (fromNode == null || toNode == null) {
                        throw new NoSuchElementException("En av noderna finns inte i grafen: " + fromNodeName + " eller " + toNodeName);
                    }
                    
                  //Undviker duplicering
                    if (!graph.hasEdge(fromNode, toNode, edgeName)) {
                        graph.connect(fromNode, toNode, edgeName, weight);
                    }
                    
                    Line connectionLine = new Line(fromNode.getCenterX(), fromNode.getCenterY(), toNode.getCenterX(), toNode.getCenterY());
                    connectionLine.setStroke(Color.BLACK);
                    connectionLine.setStrokeWidth(3.0);
                    connectionLine.setMouseTransparent(true);
                    
                    
                    outputArea.getChildren().add(connectionLine);
                    
                }
            }

            unsavedChanges = true;
        } catch (IOException e) {
            showErrorMessage("Failed to open file: " + e.getMessage());
        }
    }
    
    
    //Menyvalet för att spara
    private void handleSave() {
    	
    	//try-with-resources används för att garantera att filer stängs korrekt efter de har använts
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("europa.graph"))) {
                // Skriv URL till bildfilen
                writer.write(imageURL);
                writer.newLine();

                List<Location> nodes =  new ArrayList<>(graph.getNodes());
              
                // Spara semikolonseparerade uppgifter om alla noder
                 StringBuilder nodesLine = new StringBuilder();
                 for (Location node : nodes) {
            	  if ((boolean) node.getUserData()) {
                      // Om noden är markerad, avmarkera den
                      graph.unmarkPlace(node);
                  }
            	  node.setFill(Color.BLUE);
                  node.setUserData(false);
                  String nodeName = node.getName();
                  double x = Math.round(graph.getNodeX(node) * 100.0) / 100.0;
                   double y = Math.round(graph.getNodeY(node) * 100.0) / 100.0;
                   nodesLine.append(nodeName).append(";")
                           .append(x).append(";")
                            .append(y).append(";");
               }
               
                // Ta bort sista semikolonet
              if (nodesLine.length() > 0) {
                    nodesLine.setLength(nodesLine.length() - 1);
               }
               writer.write(nodesLine.toString());
               writer.newLine();
               
               StringBuilder edgesLine = new StringBuilder();
               for (Location node : nodes) {
                   Collection<Edge<Location>> edges = graph.getEdgesFrom(node);
                   for (Edge<Location> edge : edges) {
                       String fromNode = node.getName();
                       String toNode = edge.getDestination().getName();
                       String edgeName = edge.getName();
                       int weight = (int) edge.getWeight();
                       edgesLine.append(fromNode).append(";")
                                .append(toNode).append(";")
                                .append(edgeName).append(";")
                                .append(weight).append(System.lineSeparator());
                   }
               }

               // Skriv förbindelserna till filen
               writer.write(edgesLine.toString());
                 writer.flush();
                unsavedChanges = false;

            }catch (IOException e) {
            	//Undantaget visas i consolen
              e.printStackTrace();
              showErrorMessage("Failed to save file.");
          }
    }

    //Menyvalet spara bild
    private void handleSaveImage() {
    	
    	//Vi fick inte det att fungera med SwingFXUtil så vi gjorde på detta sätt istället, vi hämtade inspiration av ChatGPT
        try {
            // Hämta scenen från fönstret
            scene = primaryStage.getScene();
            
            // Skapa en WritableImage och ta en skärmbild av scenen
            WritableImage image = new WritableImage((int) scene.getWidth(), (int) scene.getHeight());
            scene.snapshot(image);

            // Skapa en PixelReader för att läsa pixlar från bilden
            PixelReader pixelReader = image.getPixelReader();

            // Skapa en BufferedImage av rätt storlek och fyll den med pixlar från WritableImage
            BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
                }
            }

            // Spara bilden som en PNG-fil med namnet "capture.png"
            File file = new File("capture.png");
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fel vid sparandet av bild " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleExit() {
       
   	 if (unsavedChanges) {
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setContentText ("Unsaved changes, exit anyway?");
            Optional<ButtonType> result = alert.showAndWait();
	        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
	        	primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));        
	   
	        }

	    } else {
	    	// Om inga ändringar finns, avsluta 
	    	primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
	    }
	    
    }
    
    //Knappen Find Path
    private void handleFindPath() {
    	List<Location> markedPlaces = graph.getMarkedPlace();
    	 // Kontrollera att exakt två platser är markerade
        if (markedPlaces.size() != 2) {
            showErrorMessage("Two places must be selected!");
            return;
        }

        // Hämta koordinaterna för de två markerade cirklarna
        Location node1 = markedPlaces.get(0);
        Location node2 = markedPlaces.get(1);
        
        // Hitta vägen mellan de två platserna
        List<Edge<Location>> path = graph.getPath(node1, node2);

        // Kontrollera om en väg hittades
        if (path.isEmpty()) {
            showErrorMessage("No path exists between the selected places!");
            return;
        }
        
        // Skapa en dialogruta för att visa path informationen
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Path Information");
        dialog.setHeaderText("Path from " + node1.getName() + " to " + node2.getName());

        StringBuilder pathInfo = new StringBuilder();
        int totalTime = 0;
        
        for (Edge<Location> edge : path) {
            String to = edge.getDestination().getName();
            String by = edge.getName();
            int time = edge.getWeight();
            totalTime += time;

        // Skapar en rad som innehåller all information om förbindelsen
        pathInfo.append("to ").append(to)
                .append(" by ").append(by)
                .append(" takes ").append(time)
                .append("\n");
        }
        
        // Lägg till total tid i slutet
        pathInfo.append("Total ").append(totalTime);
        
       

        // Skapar en TextArea för att visa vägens information
        TextArea textArea = new TextArea(pathInfo.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();
    
    }

    //Knappen Show Connection
    private void handleShowConnection() {
    	List<Location> markedPlaces = graph.getMarkedPlace();
    	// Visa felmeddelande om inte exakt två platser är markerade
        if (markedPlaces.size() != 2) {
	        showErrorMessage("Two places must be selected!");
        }
        
        // Hämta koordinaterna för de två markerade cirklarna
        Location node1 = markedPlaces.get(0);
        Location node2 = markedPlaces.get(1);
        
        // Kontrollera om en förbindelse existerar mellan de två platserna
        if (!graph.pathExists(node1, node2)) {
            showErrorMessage("No connection exists between the selected places!");
        }
        
        // Hämta förbindelsens namn och tid
        String connectionName = graph.getConnectionName(node1, node2);
        int connectionTime = graph.getConnectionTime(node1, node2);
        
        // Skapa och visa dialogrutan med förbindelsens detaljer
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Connection Details");
        dialog.setHeaderText("Connection from " + node1 + " to " + node2);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(connectionName);
        nameField.setEditable(false);
        TextField timeField = new TextField(String.valueOf(connectionTime));
        timeField.setEditable(false);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }   

  //Knappen New Place
    private void handleNewPlace() {
        // Ändra muspekaren till crosshair och inaktivera knappen
        scene.setCursor(Cursor.CROSSHAIR);
        btnNewPlace.setDisable(true);

        // Lägg till en EventHandler för klick på kartan
        outputArea.setOnMouseClicked(event -> {
        	
            // Skapa en textinputdialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Name");
            dialog.setHeaderText(null);
            dialog.setContentText("Name of place:");

            // Visa dialogrutan och vänta på att användaren matar in namnet
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                // Skapa en ny plats med det angivna namnet och koordinater
                Location placeLocation = new Location(event.getX(), event.getY(), 10, Color.BLUE, name);
                graph.add(placeLocation);
               
                graph.setNodeCoordinates(placeLocation, event.getX(), event.getY());
                
                //För att indikera att circeln inte är markerad
                placeLocation.setUserData(false);
                // Skapa en text för att visa platsens namn
                Text placeText = new Text(event.getX() + 10, event.getY(), name);
                placeText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                placeText.setFill(Color.BLACK);
                
                outputArea.getChildren().addAll(placeLocation, placeText);           
                placeLocation.setOnMouseClicked(circleEvent -> {
                    handlePlaceClick(placeLocation);
                }); 
                
            });

            // Återställ muspekaren och aktivera knappen igen
            scene.setCursor(Cursor.DEFAULT);
            btnNewPlace.setDisable(false);

            // Ta bort eventHandler för klick på kartan
            outputArea.setOnMouseClicked(null);

            unsavedChanges = true;
        });
    }
    
    private void handlePlaceClick(Location placeLocation) {
        boolean isMarked = (boolean) placeLocation.getUserData();
        
        if (isMarked) {
            placeLocation.setFill(Color.BLUE);
            placeLocation.setUserData(false);
            graph.unmarkPlace(placeLocation);
            
        } else {
            if (graph.getMarkedPlace().size() < 2) {
                placeLocation.setFill(Color.RED);
                placeLocation.setUserData(true);
                graph.markPlace(placeLocation);
                
            } else {
               
            }
        }
    }
   
    //Knappen new connection
    private void handleNewConnection() {
    	// Visa felmeddelande om inte exakt två platser är markerade
    	List<Location> markedPlaces = graph.getMarkedPlace();
        if (graph.getMarkedPlace().size() != 2) {
	        showErrorMessage("Two places must be selected!");
	        return;
        }
        
        Location node1 = markedPlaces.get(0);
        Location node2 = markedPlaces.get(1);
    	
        // Visa felmeddelande om det redan finns förbindelse mellan de valda platserna
        if (graph.pathExists(node1, node2)) {
 	       showErrorMessage("There already exists a path between these places!");
 	       return; 
        }     
  
        //Vi använder oss av Dialog för att skapa en dialogruta med två platser där användaren ska mata in namn och tid
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connection");
        dialog.setHeaderText("Connection from " + node1 + " to " + node2);
       
        // Ställ in knapparna i dialogrutan
        ButtonType buttonTypeOK = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOK, ButtonType.CANCEL);

        // Skapa inmatningsfält för namn och tid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField timeField = new TextField();
        timeField.setPromptText("Time");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Hämta värdena från inmatningsfälten när användaren klickar på OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOK) {
                return new Pair<>(nameField.getText(), timeField.getText());
            }
            return null;
        });

        //Här sparas värdet för namn och tid ner 
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(inputs -> {
            String connectionName = inputs.getKey();
            int connectionTime;
            try {
                connectionTime = Integer.parseInt(inputs.getValue());
            } catch (NumberFormatException e) {
                showErrorMessage("Time must be a number.");
                return;
            }
            
            // Hämta koordinaterna för de två platserna
            double x1 = graph.getNodeX(node1);
            double y1 = graph.getNodeY(node1);
            double x2 = graph.getNodeX(node2);
            double y2 = graph.getNodeY(node2);
            
            // Skapa en linje för att representera förbindelsen
            Line connectionLine = new Line(x1, y1, x2, y2);
            connectionLine.setStroke(Color.BLACK);
            connectionLine.setStrokeWidth(3.0);

            connectionLine.setMouseTransparent(true);
            
            // Lägg till linjen i imageView
            outputArea.getChildren().add(connectionLine);
            graph.connect(node1, node2, connectionName, connectionTime);
            
            unsavedChanges = true;
        });
    }
    
    
    // Metod för att visa felmeddelanden, på grund av att det behövdes många error meddelanden så skapade vi en metod som var lätt att anropa
    private void showErrorMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
     
    //Knappen för change connection
    private void handleChangeConnection() {
    	List<Location> markedPlaces = graph.getMarkedPlace();   
        // Kontrollera att exakt två platser är markerade
        if (markedPlaces.size() != 2) {
            showErrorMessage("Two places must be selected!");
        }
        
        // Hämta koordinaterna för de två markerade cirklarna
        Location node1 = markedPlaces.get(0);
        Location node2 = markedPlaces.get(1);
        
        // Kontrollera om en förbindelse existerar mellan de två platserna
        if (!graph.pathExists(node1, node2)) {
            showErrorMessage("No connection exists between the selected places!");  
        }  
        
        String connectionName = graph.getConnectionName(node1, node2);
        
        // Skapa och visa dialogrutan för att ändra tiden
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Connection");
        dialog.setHeaderText("Connection from " + node1 + " to " + node2);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        //Namnet går inte att ändra, men tiden går att ändra
        TextField nameField = new TextField(connectionName);
        nameField.setEditable(false);
        TextField timeField = new TextField();
        timeField.setPromptText("New Time");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        //Tiden ändras när användaren trycker på OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return timeField.getText();
            }
            return null;
        });

        //Här sparas den nya tiden
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int newTime = Integer.parseInt(result.get());
                graph.setConnectionWeight(node1, node2, newTime);
            } catch (NumberFormatException e) {
                showErrorMessage("Time must be a number.");
            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }
        unsavedChanges = true;
    }
	
    //Metod för att rensa grafen
    private void clearGraphData() {
    	
        if (graph != null) {
        	
        	List<Location> nodes = new ArrayList<>(graph.getNodes());
        	
            // Återställ markeringar och visuella egenskaper för noder
            for (Location node : nodes) {
                if ((boolean) node.getUserData()) {
                    // Om noden är markerad, avmarkera den
                    graph.unmarkPlace(node);
                }
                node.setFill(Color.BLUE);
                node.setUserData(false);
            }
        	
        	 // Ta bort alla förbindelser från grafen
            for (Location fromNode : nodes) {
                if (graph.getNodes().contains(fromNode)) {  
                    Collection<Edge<Location>> edges = new ArrayList<>(graph.getEdgesFrom(fromNode));
                    for (Edge<Location> edge : edges) {
                        graph.disconnect(edge.getSource(), edge.getDestination());
                    }
                }
            }

         // Ta bort alla platser från grafen
            for (Location node : nodes) {
                if (graph.getNodes().contains(node)) {  
                    graph.remove(node);
                }
            }
            
            
            imageURL = null; 
            imageView.setImage(null);
            
            // Ta bort alla visualiseringar från outputArea, förutom kartan
            outputArea.getChildren().removeIf(node -> node instanceof Location || node instanceof Text || node instanceof Line);
            
        }
    
           
        
    }
	 public static void main(String[] args) {
	        launch(args);
	    }

}
