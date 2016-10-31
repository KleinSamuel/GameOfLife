package application;

import java.io.File;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;


public class GameOfLife_View extends Application {
	
	private double xSize;
	private double ySize;
	private double pixelSize;
	private boolean isPlaying = false;
	private boolean isGridDisplayed = false;
	private double speed = 100;
	private int cycleCount = Animation.INDEFINITE;
	Timeline timeline;
	Popup pop;
	
	private Slider sliderSpeed, sliderSize;
	
	GraphicsContext gc;
	GameOfLife_Model model;
	
	Canvas canvas;
	
	public GameOfLife_View() {
		model = new GameOfLife_Model("/home/sam/Desktop/GameOfLife/radDerHlKat.txt");
		
		this.pixelSize = 10;
		
		resize();
	}
	
	public void resize(){
		this.xSize = model.getBoard()[0].length*pixelSize;
		this.ySize = model.getBoard().length*pixelSize;
	}
	
	public void resizeCanvas(){
		canvas.setWidth(xSize);
		canvas.setHeight(ySize);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			BorderPane root = new BorderPane();
			root.setPadding(new Insets(10, 10, 10, 10));
			
			Group group = new Group();
			
			canvas = new Canvas(xSize,ySize);
			gc = canvas.getGraphicsContext2D();
			
			canvas.setScaleX(1);
			canvas.setScaleY(1);
			
			canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					if(canvas.getScaleX() == 1){
						int x = (int)(event.getX()/(pixelSize));
						int y = (int)(event.getY()/(pixelSize));
						model.getBoard()[y][x] = !model.getBoard()[y][x];
						refresh(model.getBoard());						
					}
				};
			});
			
			group.getChildren().add(canvas);
			
			root.setCenter(group);
			
			Button btn = createButton("START");
			
			timeline = new Timeline(new KeyFrame(
			        Duration.millis(speed),
			        ae -> {
			        	model.nextCycle();
			        	refresh(model.getBoard());
			        }));
			timeline.setCycleCount(cycleCount);
			timeline.setRate(1);
			
			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if(isPlaying){
						timeline.pause();
						btn.setText("START");
					}else{
						timeline.play();
						btn.setText("PAUSE");
					}
					isPlaying = !isPlaying;
				}
			});
			
			Button buttonReset = createButton("RESET");
			
			buttonReset.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					model.resetBoard();
					refresh(model.getBoard());
				}
			});
			
			sliderSpeed = createSliderSpeed();
			Label sliderSpeedInfo = new Label("SPEED");
			sliderSize = createSliderSize();
			Label sliderSizeInfo = new Label("SIZE");
			
			Button buttonShowGrid = createButton("SHOW GRID");
			
			buttonShowGrid.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if(isGridDisplayed){
						buttonShowGrid.setText("SHOW GRID");
					}else{
						buttonShowGrid.setText("HIDE GRID");
					}
					isGridDisplayed = !isGridDisplayed;
					refresh(model.getBoard());
				}
			});
			
			Button buttonStep = createButton("STEP BY STEP");
			buttonStep.setPrefWidth(150);
			
			buttonStep.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					model.nextCycle();
					refresh(model.getBoard());
				}
			});
			
			Button buttonSave = createButton("SAVE");
			buttonSave.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Popup p = new Popup();
					p.setAutoHide(true);
					GridPane pane = new GridPane();
					Label l1 = new Label("Please enter name for file..");
					TextArea ta1 = new TextArea();
					ta1.setPrefHeight(20);
					Button b1 = new Button("SAVE");
					b1.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							DirectoryChooser fc = new DirectoryChooser();
							fc.setTitle("CHOOSE FILE");
							File tmp = fc.showDialog(primaryStage);
							model.saveToFile(tmp.getAbsolutePath()+"/"+ta1.getText()+".txt");							
						}
					});
					pane.add(l1, 0, 0);
					pane.add(ta1, 0, 1);
					pane.add(b1, 0, 2);
					p.getContent().addAll(pane);
					p.centerOnScreen();
					p.show(primaryStage);
				}
			});
			
			Button buttonNew = createButton("NEW");
			
			buttonNew.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					pop = new Popup();
					pop.setAutoHide(true);
					GridPane pane = new GridPane();
					Label l1 = new Label("Please enter size of new grid..");
					TextArea ta1 = new TextArea();
					ta1.setPrefSize(50, 20);
					TextArea ta2 = new TextArea();
					ta2.setPrefSize(50, 20);
					Label l2 = new Label("Pixel size:");
					TextArea ta3 = new TextArea();
					ta3.setPrefSize(50, 20);
					Button btN = new Button("CHOOSE FILE");
					
					btN.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							FileChooser fc = new FileChooser();
							fc.setTitle("OPEN CONFIG FILE");
							File tmp = fc.showOpenDialog(primaryStage);
							model = new GameOfLife_Model(tmp.getAbsolutePath());
							resize();
							resizeCanvas();
							refresh(model.getBoard());
						}
					});
					
					Button bt1 = new Button("CREATE NEW");
					bt1.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							System.out.println(ta1.getText());
							model.resetBoard(Integer.parseInt(ta1.getText()),Integer.parseInt(ta2.getText()));
							pixelSize = Integer.parseInt(ta3.getText());
							resize();
							resizeCanvas();
							refresh(model.getBoard());
						}
					});
					
					pane.add(l1, 0, 0);
					pane.add(ta1, 0, 1);
					pane.add(ta2, 0, 2);
					pane.add(l2, 0, 3);
					pane.add(ta3, 0, 4);
					pane.add(btN, 0, 5);
					pane.add(bt1, 0, 6);
					
					pop.getContent().addAll(pane);
					pop.centerOnScreen();
					pop.show(primaryStage);
				}
			});
			
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0,10,0,10));
			
			grid.add(btn, 0, 0);
			grid.add(buttonStep, 1, 0);
			grid.add(buttonReset, 2, 0);
			grid.add(sliderSpeedInfo, 3, 0);
			grid.add(sliderSpeed, 3, 1);
			grid.add(sliderSizeInfo, 4, 0);
			grid.add(sliderSize, 4, 1);
			grid.add(buttonShowGrid, 5, 0);
			grid.add(buttonSave, 6, 0);
			grid.add(buttonNew, 7, 0);
			
			root.setBottom(grid);
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			refresh(model.getBoard());
			
			canvas.setOnScroll(new EventHandler<ScrollEvent>() {
				@Override
				public void handle(ScrollEvent event) {
					if(event.getDeltaY() > 0){
						canvas.setScaleX(canvas.getScaleX()+0.5);
						canvas.setScaleY(canvas.getScaleY()+0.5);
					}else if(canvas.getScaleX() > 0.5){
						canvas.setScaleX(canvas.getScaleX()-0.5);
						canvas.setScaleY(canvas.getScaleY()-0.5);
					}
				}
			});
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("GAME OF LIFE");
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refresh(boolean[][] array){
		
		gc.setFill(Color.WHITE);
		
		gc.clearRect(0, 0, xSize, ySize);
		
		gc.setFill(Color.BLACK);
		
		if(isGridDisplayed){
			gc.setLineWidth(0.1);
			
	        for(int i = 0 ; i <= xSize; i+=pixelSize){
	            gc.strokeLine(i, 0, i, ySize);
	        }        
	        for(int i = 0 ; i <= ySize; i+=pixelSize){
	            gc.strokeLine(0, i, xSize, i);
	        } 
		}
		
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				if(array[i][j]){
					gc.fillRect(j*pixelSize, i*pixelSize, pixelSize, pixelSize);					
				}
			}
		}
	}
	
	private Button createButton(String text){
		Button btn = new Button(text);
		btn.setPrefSize(100, 60);
		return btn;
	}
	
	private Slider createSliderSpeed(){
		Slider slider = new Slider();
		slider.setMin(0);
		slider.setMax(10);
		slider.setValue(1);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(10);
		slider.setMinorTickCount(5);
		slider.setBlockIncrement(1);
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				timeline.setRate((double)newValue);
			}
		});
		
		return slider;
	}
	
	private Slider createSliderSize(){
		Slider slider = new Slider();
		slider.setMin(0.1);
		slider.setMax(1);
		slider.setValue(1);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(10);
		slider.setMinorTickCount(5);
		slider.setBlockIncrement(1);
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				canvas.setScaleX((double)newValue);
				canvas.setScaleY((double)newValue);
			}
		});
		
		return slider;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
