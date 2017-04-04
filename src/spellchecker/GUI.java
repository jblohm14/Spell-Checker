
package spellchecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static spellchecker.SpellChecker.getNumIncorrect;

public class GUI extends StackPane {
    
    // To-do:   change the location of saved file
    //          fix ignore button
    
    public boolean        debug   = false;
    public static boolean browse  = false;
    public static boolean start   = false;
    public static boolean change  = false;
    public static boolean newDict = false;
    
    Image folder = new Image(getClass().getResourceAsStream("squarefolder.png"));
    Image edit   = new Image(getClass().getResourceAsStream("file.png"));
    Image check  = new Image(getClass().getResourceAsStream("checked.png"));
    Image back   = new Image(getClass().getResourceAsStream("back.png"));
    Image next   = new Image(getClass().getResourceAsStream("next.png"));
    Image save   = new Image(getClass().getResourceAsStream("save.png"));
    
    final Button browseBtn    = new Button("Open File", new ImageView(folder));
    final Button startBtn     = new Button("Spellcheck", new ImageView(check));
    final Button saveBtn      = new Button("Save file", new ImageView(save));
    final Button arrowLeft    = new Button(null, new ImageView(back));
    final Button arrowRight   = new Button(null, new ImageView(next));
    final Button quit         = new Button("Quit");
    final Button changeBtn    = new Button("Change");
    final Button addBtn       = new Button("Add word");
    final Button ignore       = new Button("Ignore");
    
    public Label misspellings = new Label();
    public Label fileLabel    = new Label();
    public Label authors;
    
    public static FileChooser fc = new FileChooser();
    public static String fileName;
    
    public static TextFlow   flow = new TextFlow();
    public static ScrollPane sp   = new ScrollPane();
    public static TextArea   ta   = new TextArea();
    public static GridPane   grid = new GridPane();
    public static TextField  tf   = new TextField();
    
    public static String previousWord;
    public static String newWord;
    
    public static HBox authorBox = new HBox();
    
    public static int count = 0;
    
    GUI() {
        
        browseBtn.setOnAction((ActionEvent e) -> {
            browse = true;
            
            try {
                
                configureFileChooser();
                
                if(!fileName.equals("nullCase.txt")){
                    browseBtn.setDisable(true);
                    
                    grid.add(formatTextFlow(), 1, 1, 1, 4);
                    
                    fileLabel.setText("File Name: " + fileName);
                    fileLabel.setVisible(true);
                }
                
                SpellChecker.readWords();
                SpellChecker.checkSpelling();
                
                misspellings.setVisible(true);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        saveBtn.setOnAction((ActionEvent e) -> {
            
            try {
                configureSaveButton(fileName);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        startBtn.setOnAction((ActionEvent e) -> {
            startBtn.setDisable(true);
            saveBtn.setVisible(true);
            
            start = true;
            
            authorBox.setStyle("-fx-background-color: #A9ACB6");
            authors.setStyle("-fx-text-fill: white;");
            
            if (browse){
                SpellChecker.checkSpelling();
            }
            
            misspellings.setText("Words Misspelled: " + getNumIncorrect());
            misspellings.setVisible(true);
            grid.add(formatOutPut(), 1, 5, 2, 1);
            
            previousWord = tf.getText().toLowerCase();
        });
        
        arrowLeft.setOnAction((ActionEvent e) -> {
            tf.setEditable(true);
            
            if (count < SpellChecker.incorrectSpell.size()){
                
                if (count > 0){
                    count--;
                }    
                
                tf.setText(SpellChecker.incorrectSpell.get(count));
            }
            
            previousWord = tf.getText().toLowerCase();
        });
        
        arrowRight.setOnAction((ActionEvent e) -> {
            arrowLeft.setDisable(false);
            tf.setEditable(true);
            
            if (count < SpellChecker.incorrectSpell.size()){
                
                count++;
                tf.setText(SpellChecker.incorrectSpell.get(count));
            }
            
            previousWord = tf.getText().toLowerCase();
        });
        
        quit.setOnAction((ActionEvent e) -> {
            Platform.exit();
        });
        
        addBtn.setOnAction((ActionEvent e) -> {
            tf.setEditable(false);
            
            HashTable.putNewWord(tf.getText());
            
            changeBtn.fire();
            arrowLeft.fire();
        });
        
        ignore.setOnAction((ActionEvent e) -> {
            
            tf.setEditable(false);
            SpellChecker.ignoredWords.add(tf.getText());
            
            changeBtn.fire();
        });
        
        changeBtn.setOnAction((ActionEvent e) -> {
            
            tf.setEditable(false);
            change = true;
            flow.getChildren().clear();
            newWord = tf.getText();
            SpellChecker.changeWord();
            SpellChecker.checkSpelling();
            arrowRight.fire();
            
            if (count > 0){
                    count--;
            } 
            
            if (getNumIncorrect() == 0){
                tf.setText(null);
                tf.setPromptText("All set!");
                arrowRight.setDisable(true);
                arrowLeft.setDisable(true);
                changeBtn.setDisable(true);
            }
            
            misspellings.setText("Words Misspelled: " + getNumIncorrect());
        });
        
        super.getChildren().addAll(formatLayout());
    }
    
    public GridPane formatLayout(){
        
        if (debug){
            grid.setGridLinesVisible(true);
        }
        
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(25);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(75);
        
        grid.getColumnConstraints().addAll(column0, column1);
        
        RowConstraints row0 = new RowConstraints();
            row0.setPrefHeight(60);
        RowConstraints row1 = new RowConstraints();
            row1.setPrefHeight(150);
        RowConstraints row2 = new RowConstraints();
            row2.setPrefHeight(150);
        RowConstraints row3 = new RowConstraints();
            row3.setPrefHeight(150);
        RowConstraints row4 = new RowConstraints();
            row4.setPrefHeight(30);
        RowConstraints row5 = new RowConstraints();
            row5.setMinHeight(40);
        RowConstraints row6 = new RowConstraints();
            row6.setPrefHeight(20);
        
        grid.getRowConstraints().addAll(row0, row1, row2, row4, row3, row5, row6);
        
        Pane p1 = new Pane();
        p1.setId("default-pane1");
        Pane p2 = new Pane();
        p2.setId("default-pane2");
        
        grid.add(p1,                   1, 0);
        grid.add(p2,                   1, 5);
        grid.add(formatEmptyPane(),    1, 1, 1, 4);
        
        grid.add(formatBrowse(),       0, 1);
        grid.add(formatStartButton(),  0, 2);
        grid.add(formatNumIncorrect(), 0, 3);
        grid.add(formatSaveButton(),   0, 4);
        
        grid.add(formatTitle(),        1, 0);
        grid.add(formatAuthors(),      1, 6);
        return grid;
    }
    
    public HBox formatNumIncorrect(){
        HBox hb1 = new HBox();
        
        misspellings.setVisible(false);
        misspellings.setId("misspell");
        
        hb1.getChildren().add(misspellings);
        hb1.setAlignment(Pos.CENTER);
        
        return hb1;
    }
    
    public VBox formatBrowse(){
        VBox vb1 = new VBox(10);
        
        browseBtn.setId("browse");
        browseBtn.setContentDisplay(ContentDisplay.TOP);
        browseBtn.setMinWidth(50);
        
        fileLabel.setVisible(false);
        
        vb1.getChildren().addAll(browseBtn, fileLabel);
        vb1.setAlignment(Pos.CENTER);
        
        return vb1;
    }
    
    public VBox formatStartButton(){
        VBox vb1 = new VBox(10);
        
        startBtn.setMinWidth(75);
        startBtn.setId("start");
        startBtn.setContentDisplay(ContentDisplay.TOP);
        
        vb1.getChildren().addAll(startBtn);
        vb1.setAlignment(Pos.CENTER);
        
        return vb1;
    }
    
    public VBox formatSaveButton(){
        VBox vb1 = new VBox(10);
        
        saveBtn.setMinWidth(50);
        saveBtn.setId("save");
        saveBtn.setContentDisplay(ContentDisplay.TOP);
        saveBtn.setVisible(false);
        
        vb1.getChildren().addAll(saveBtn);
        vb1.setAlignment(Pos.CENTER);
        
        return vb1;
    }
   
    
    public HBox formatTitle(){
        
        HBox hb1 = new HBox();
        
        Label title = new Label("Spell Checker");
        
        title.setId("title");
        
        hb1.getChildren().addAll(title);
        hb1.setAlignment(Pos.CENTER);
        
        return hb1;
    }
    
    public TextField formatTextField(){
        
        TextField tf = new TextField();
        tf.setPromptText("");
        
        return tf;
    }
    
    public HBox formatAuthors(){
        authorBox.setId("authors-box");
        
        authors = new Label("Written by Josh Blohm: A Study of Hash Tables");
        authors.setId("authors");
        
        authorBox.getChildren().addAll(authors);
        authorBox.setAlignment(Pos.CENTER);
        
        return authorBox;
    }
    
    public static void configureFileChooser(){
        File selectedFile = fc.showOpenDialog(null);
        
        if (selectedFile != null){
            fileName = selectedFile.getName();
        } else {
            fileName = "nullCase.txt";
        }
    }
    
    public static boolean hasNoFile(){
        return !fileName.equals("nullCase.txt");
    }
    
    public ScrollPane formatTextFlow(){
        sp.setHbarPolicy(ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        
        sp.setPadding(new Insets(20, 20, 20, 20));
        flow.setLineSpacing(5);
        flow.setId("flow");
        sp.setContent(flow);
        
        return sp;
    }
    
    public Pane formatEmptyPane(){
        Pane pane = new Pane();
        pane.setId("empty");
        
        return pane;
    }
    
    public HBox formatOutPut(){
        HBox hb1 = new HBox(25);
        HBox hb2 = new HBox();
        HBox hb3 = new HBox(10);
        
        hb1.setPadding(new Insets(10));
        
        quit.setMinWidth(75);
        saveBtn.setMinWidth(75);
        changeBtn.setMinWidth(75);
        addBtn.setMinWidth(75);
        ignore.setMinWidth(75);
        
        hb1.setId("output-box");
        arrowLeft.setId("arrow-left");
        arrowRight.setId("arrow-right");
        quit.setId("quit");
        saveBtn.setId("save");
        changeBtn.setId("change");
        addBtn.setId("add");
        ignore.setId("ignore");
        tf.setId("text-field");
        
        //arrowLeft.setDisable(true);
        
        tf.setText(SpellChecker.incorrectSpell.get(0));
        quit.setAlignment(Pos.CENTER);
        
        hb3.getChildren().addAll(quit);
        hb2.getChildren().addAll(tf, arrowLeft, arrowRight, changeBtn, addBtn, ignore);
        hb1.getChildren().addAll(hb2, hb3);
        
        hb3.setAlignment(Pos.CENTER);
        hb2.setAlignment(Pos.CENTER);
        hb1.setAlignment(Pos.CENTER_LEFT);
        
        return hb1;
    }
    
    public void configureSaveButton(String fileName) throws FileNotFoundException{
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        
        for (Word w : SpellChecker.test){
            pw.print(w.unformatted);
        }
        
        pw.close();
        
        Label saved = new Label("Your file has been saved.");
        saved.setStyle("-fx-font-size: 18;");
        
        StackPane sp = new StackPane();
        sp.getChildren().add(saved);
        
        Scene scene = new Scene(sp, 300, 100);
        
        Stage stage = new Stage();
        stage.setTitle("file saved");
        stage.setScene(scene);
        
        stage.show();        
    }
}
