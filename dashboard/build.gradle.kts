plugins {
    application
    id("myproject.java-conventions")
    alias(libs.plugins.javafxplugin)
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.controls", "javafx.fxml", "javafx.swing", "javafx.graphics")
}

dependencies {

}

application {
    mainClass = "roofsense.dashboard.Launcher"
}
