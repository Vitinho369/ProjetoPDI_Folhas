package visao;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import dominio.Entity;
import dominio.Processor3;

public class Main extends Application {
    private static final String CONFIG_FILE_PATH = "config.txt";
    private List<File> selectedFiles = new ArrayList<>();
    private ImageDisplay imageDisplay = new ImageDisplay();
    private Button processButton;
    private float areaIpe=0;
    private float areaPauBrasil=0;
    private float areaXanana=0;
    private float areaRoseira=0;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            VBox root = new VBox(20);
            Scene scene = new Scene(root, 800, 600);

            // Controls
            Button loadButton = new Button("Carregar Arquivo(s)");
            loadButton.setOnAction(e -> handleLoadButton());

            processButton = new Button("Processar");
            processButton.setOnAction(e -> process());

            HBox buttonBox = new HBox(10);
            buttonBox.getChildren().addAll(loadButton, processButton);
            
            Button clearButton = new Button("Limpar Arquivos");
            clearButton.setOnAction(e -> handleClearButton());
            clearButton.setStyle("-fx-font-size: 14;");
            buttonBox.getChildren().add(clearButton);


            // Style
            root.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");
            buttonBox.setStyle("-fx-padding: 10; -fx-background-color: #d3d3d3;");

            root.getChildren().addAll(buttonBox, imageDisplay.createImageGridScrollPane());
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #f0f0f0;");

            loadButton.setStyle("-fx-font-size: 14;");
            processButton.setStyle("-fx-font-size: 14;");

            primaryStage.setScene(scene);
            primaryStage.setTitle("Aplicação PDI");
            primaryStage.show();

            loadSelectedFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //PROCESS
    private void process() {
    	processButton.setDisable(true);
        
        	Processor3 processor = new Processor3();
            ArrayList<Entity> entities = new ArrayList<>();
            if (selectedFiles != null) {
                for (File file : selectedFiles) {
                    try{
                    	entities.addAll(processor.process(file));
                    }catch(Exception e) {
                        processButton.setDisable(true);  
                        System.out.println(e.toString());
                    
                    }
                }
            }
            
            if(entities.size() > 0) {
            	openDataTableView(entities); 
//            	for(int i=0; i < entities.size();i++) {
//            		if(entities.get(i).getClassification().equals("Ipe"))
//            			areaIpe += entities.get(i).getArea();
//            		else if (entities.get(i).getClassification().equals("Pau Brasil"))
//            			areaPauBrasil += entities.get(i).getArea();
//            		else if(entities.get(i).getClassification().equals("Xanana"))
//            			areaXanana += entities.get(i).getArea();
//            		else if(entities.get(i).getClassification().equals("Roseira"))
//            			areaRoseira += entities.get(i).getArea();
//            			
//            	}
            	
//            	System.out.println("AreaIpe = "+areaIpe);
//            	System.out.println("AreaPauBrasil = "+areaPauBrasil);
//            	System.out.println("AreaXanana = "+areaXanana);
//            	System.out.println("AreaRoseira = "+areaRoseira);
        	}
            processButton.setDisable(false);
        
    }

    private void openDataTableView(List<Entity> entities) {
        DataTableView dataTableView = new DataTableView(entities);
        dataTableView.displayDataTable();
    }



    //LOAD
    private void handleLoadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivos");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*")
        );
        
        this.selectedFiles.clear();
        
        var selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            this.selectedFiles.addAll(selectedFiles);
            imageDisplay.loadImages(this.selectedFiles);
            saveSelectedFiles();
        }
    }    
    

    // SAVE CONFIGURATION
    private void saveSelectedFiles() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_FILE_PATH))) {
            oos.writeObject(selectedFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedFiles() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CONFIG_FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                selectedFiles = (List<File>) obj;
                imageDisplay.loadImages(selectedFiles);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    //CLEAR SELECTED FILES
    private void handleClearButton() {
        selectedFiles.clear();
        imageDisplay.clearImages();
        saveSelectedFiles();  // Save the updated file list
    }

}
