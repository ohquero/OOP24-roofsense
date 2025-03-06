package roofsense.dashboard;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import roofsense.dashboard.boundary.javafxview.RootJavaFXView;

/**
 * Application entrypoint.
 */
public class Launcher extends Application {

    @Override
    public final void start(final Stage primaryStage) {
        final var rootView = RootJavaFXView.getInstance();
        final Parent root = (Parent) rootView.getRootNode();
        final Scene scene = new Scene(root);

        //TODO" Set scene size to view's pref size
//        final var screenBounds = Screen.getPrimary().getVisualBounds();
//        if (root.prefWidth(-1) > screenBounds.getWidth() || root.prefHeight(-1) > screenBounds.getHeight()) {
//            primaryStage.setMaximized(true);
//        }

        primaryStage.setTitle("RoofSense");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
