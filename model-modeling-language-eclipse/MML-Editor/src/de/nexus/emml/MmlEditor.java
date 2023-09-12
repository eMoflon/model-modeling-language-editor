package de.nexus.emml;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MmlEditor extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setClassLoader(getClass().getClassLoader());
			loader.setLocation(getClass().getResource("MmlEditorDesign.fxml"));
			
			javafx.application.Platform.setImplicitExit(false);
			

			Parent root = loader.load();

			Scene scene = new Scene(root);

			primaryStage.setTitle("Model Modeling Language - Editor");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		Platform.exit();
	}
}
