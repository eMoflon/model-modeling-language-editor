package de.nexus.emml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MmlEditor extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// Parent root =
			// FXMLLoader.load(getClass().getResource("MmlEditorDesign.fxml"));

			FXMLLoader loader = new FXMLLoader();
			loader.setClassLoader(getClass().getClassLoader());
			loader.setLocation(getClass().getResource("MmlEditorDesign.fxml"));
			

			Parent root = loader.load();

			Scene scene = new Scene(root);

			primaryStage.setTitle("Model Modeling Language - Editor");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
