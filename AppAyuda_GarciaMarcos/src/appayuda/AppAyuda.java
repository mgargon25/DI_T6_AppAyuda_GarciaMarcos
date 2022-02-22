package appayuda;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
 
public class AppAyuda extends Application {
 
    private Scene scene;
 
    @Override 
    public void start(Stage stage) {
        // create scene
        stage.setTitle("Web View");
        scene = new Scene(new Browser(),900,600, Color.web("#666970"));
        stage.setScene(scene);
        //scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        // show stage
        stage.show();
    }
 
    public static void main(String[] args){
        launch(args);
    }
}
class Browser extends Region {
   private HBox toolBar;
 
   final private static String[] imageFiles = new String[]{
        "images/producto.png",
        "images/blog.png",
        "images/documentacion.png",
        "images/partners.png",
        "images/blog2.png"
    };
    final private static String[] captions = new String[]{
        "Productos",
        "Blogs",
        "Documentacion",
        "Partners",
        "Ayuda"
    };
 
    final private static String[] urls = new String[]{
        "http://aula.ieslosmontecillos.es/",
        "https://es-es.facebook.com/",
        "https://twitter.com/?lang=es",
        "TopicSalonHabana.html",
        AppAyuda.class.getResource("help.html").toExternalForm()
    };
 
    final ImageView selectedImage = new ImageView();
    final Hyperlink[] hpls = new Hyperlink[captions.length];
    final Image[] images = new Image[imageFiles.length];
    
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final Button toggleHelpTopics = new Button("Toggle Help Topics");
    private boolean needDocumentationButton = false;
 
    public Browser() {       
        //apply the styles
        getStyleClass().add("browser");
        
        for (int i = 0; i < captions.length; i++) {
            final Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
            Image image = images[i] =
                new Image(getClass().getResourceAsStream(imageFiles[i]));
            hpl.setGraphic(new ImageView (image));
            final String url = urls[i];
            final boolean addButton = (hpl.getText().equals("Help"));
 
            hpl.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    needDocumentationButton = addButton;
                    webEngine.load(url);   
                }
            });
        }        
       
        webEngine.load("http://www.ieslosmontecillos.es/wp/");

        // create the toolbar
        toolBar = new HBox();
        toolBar.setAlignment(Pos.CENTER);
        toolBar.getStyleClass().add("browser-toolbar");
        toolBar.getChildren().addAll(hpls); 
        toolBar.getChildren().add(createSpacer());

        //set action for the button
        toggleHelpTopics.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                webEngine.executeScript("toggle_Visibility('help_topics')");
            } 
        });

        // process page loading 
        webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
               @Override
               public void changed(ObservableValue<? extends State> ov, State
                oldState, State newState) {
                    toolBar.getChildren().remove(toggleHelpTopics);
                    if (newState == State.SUCCEEDED) {
                        if (needDocumentationButton) {
                       toolBar.getChildren().add(toggleHelpTopics);
                        }
                    }
                }
            }
        ); 
        
        // load the web page
        webEngine.load("http://www.oracle.com/products/index.html ");
        
        //add components
        getChildren().add(toolBar);
        getChildren().add(browser); 
    }
 
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double tbHeight = toolBar.prefHeight(w);
        layoutInArea(browser,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER);
        layoutInArea(toolBar,0,h-tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 900;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 600;
    }
}