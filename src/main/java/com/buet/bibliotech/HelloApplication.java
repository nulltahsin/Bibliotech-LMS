package com.buet.bibliotech;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import com.buet.bibliotech.db.Database;
import java.sql.Connection;
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException
    {
        Connection conn = Database.getConnection();
        if (conn != null) {
            System.out.println("SQLite connected successfully!");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 780, 460);
        stage.setTitle("BiblioTech");
        Image img=new Image(getClass().getResourceAsStream("logo.png"));
        stage.getIcons().add(img);
        stage.setScene(scene);
        stage.show();
    }
}
