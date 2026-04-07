package com.buet.bibliotech;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class BookDetailsController {

    @FXML private ImageView coverImageView;
    @FXML private Label titleLabel, idLabel, authorLabel, categoryLabel, stockLabel;

    public void setBookData(Books book) {
        titleLabel.setText(book.getBookName());
        idLabel.setText("Book ID: " + book.getBookID());
        authorLabel.setText("Author: " + book.getAuthor());
        categoryLabel.setText("Category: " + book.getCategory());
        stockLabel.setText("Stock: " + book.getAvailableCopies() + " / " + book.getTotalCopies());


        try {
            if (book.getImagePath() != null && !book.getImagePath().isEmpty()) {
                File file = new File(book.getImagePath());
                if (file.exists()) {
                    coverImageView.setImage(new Image(file.toURI().toString()));
                } else {
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }
        } catch (Exception e) {
            setDefaultImage();
        }
    }

    private void setDefaultImage() {

        coverImageView.setImage(new Image(getClass().getResourceAsStream("memberdashboard.png")));
    }
}