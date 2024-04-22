module com.example.pt2024_30229_caraba_marian_assignment_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens GUI to javafx.fxml;
    exports GUI;
}