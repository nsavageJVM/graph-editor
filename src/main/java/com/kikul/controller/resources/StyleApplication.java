package com.kikul.controller.resources;

import com.kikul.MainEditor;
import com.kikul.controller.RootLayout;
import javafx.scene.control.RadioMenuItem;

public class StyleApplication {

    private RootLayout rootLayout;
    private RadioMenuItem grid;
    private RadioMenuItem default_colors;

    private String default_theme = getClass().getResource("/css/default_theme_application.css").toExternalForm();
    private String dark_theme = getClass().getResource("/css/dark_theme_application.css").toExternalForm();

    private String dark_grid = "-fx-background-image: url(images/grid/transparent_grid_dark.png)";
    private String default_grid = "-fx-background-image: url(images/grid/transparent_grid_default.png)";

    public StyleApplication(RootLayout rootLayout, RadioMenuItem grid) {
        this.rootLayout = rootLayout;
        this.grid = grid;
    }

    public void settingsGrid() {
        grid.setOnAction(event -> {
            if (!grid.isSelected()) {
                grid.setSelected(false);
                rootLayout.right_pane.setStyle("-fx-background-image: none;");
            } else {
                grid.setSelected(true);
                if (!default_colors.isSelected())
                    changeGrid(dark_grid);
                else
                    changeGrid(default_grid);
            }
        });
    }

    public void settingsTheme(RadioMenuItem... colors) {
        default_colors = colors[0];
        colors[0].setOnAction(event -> {    // default_colors
            if (colors[0].isSelected()) {
                colors[1].setSelected(false);
                changeGrid(default_grid);
                changeStyleApp(dark_theme, default_theme);
            }
            colors[0].setSelected(true);
        });
        colors[1].setOnAction(event -> {    // dark_colors
            if (colors[1].isSelected()) {
                colors[0].setSelected(false);
                changeGrid(dark_grid);
                changeStyleApp(default_theme, dark_theme);
            }
            colors[1].setSelected(true);
        });
    }

    private void changeGrid(String css) {
        if (grid.isSelected()) {
            rootLayout.right_pane.setStyle(css);
        }
    }

    private void changeStyleApp(String default_theme, String dark_theme) {
        MainEditor.getScene().getStylesheets().remove(default_theme);
        if (!MainEditor.getScene().getStylesheets().contains(dark_theme))
            MainEditor.getScene().getStylesheets().add(dark_theme);
    }
}
