package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

public class addbookController implements Initializable {

    @FXML private TextField AuthorField, bookNameField, bookIDField, categoryField;
    @FXML private ComboBox<Integer> copiesComboBox;
    @FXML private ImageView imagePreview;

    private File selectedImageFile; // To keep track of the selected image

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generateAndSetID();
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        copiesComboBox.setItems(list);
        copiesComboBox.setValue(1);
    }

    @FXML
    void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Book Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    public void addbook(ActionEvent actionEvent) throws SQLException {
        String author = AuthorField.getText();
        String bookname = bookNameField.getText();
        String bookid = bookIDField.getText();
        String category = categoryField.getText();
        Integer copies = copiesComboBox.getValue();

        if (bookid.isEmpty() || bookname.isEmpty() || author.isEmpty() || category.isEmpty()) {
            showAlert("Error", "Required information missing.");
            return;
        }

        String savedImagePath = "default_book.png"; // Default if no image uploaded

        if (selectedImageFile != null) {
            try {
                // Create a folder 'data/covers' in your project root if it doesn't exist
                File dir = new File("data/covers");
                if (!dir.exists()) dir.mkdirs();

                // Generate a safe name for the file using the BookID
                String extension = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf("."));
                String fileName = bookid.replace("#", "B") + extension;
                File destination = new File(dir, fileName);

                // Physically copy the file from your laptop into the project folder
                Files.copy(selectedImageFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                savedImagePath = destination.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Database db = Database.getInstance();
        boolean success = db.addbook(bookid, bookname, author, category, copies, savedImagePath);

        if (success) {
            showAlert("Successful", "Book and Image added successfully.");
            clearFields();
            generateAndSetID();
        } else {
            showAlert("Error", "Database error occurred.");
        }
    }

    private void generateAndSetID() {
        Database db = Database.getInstance();
        Random random = new Random();
        String newID;
        while (true) {
            int num = random.nextInt(901) + 100;
            newID = "#" + num;
            if (!db.isBookIdExists(newID)) break;
        }
        bookIDField.setText(newID);
    }

    public void clearFields() {
        AuthorField.clear();
        bookNameField.clear();
        categoryField.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;
        copiesComboBox.setValue(1);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleback(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Books.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Available Books");
        } catch (IOException e) { e.printStackTrace(); }
    }
}