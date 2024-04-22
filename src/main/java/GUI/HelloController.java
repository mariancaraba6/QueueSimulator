package GUI;

import Business.Logic.SimulationManager;
import Model.Policies;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HelloController {

    @FXML
    private TextField numberOfClients;
    @FXML
    private TextField numberOfServers;
    @FXML
    private TextField minArrivalTime;
    @FXML
    private TextField maxArrivalTime;
    @FXML
    private TextField minServiceTime;
    @FXML
    private TextField maxServiceTime;
    @FXML
    private TextArea output;
    @FXML
    private ChoiceBox<Policies.SelectionPolicy> strategy;
    @FXML
    private TextField timeLimitField;
    @FXML
    private Button start;
    @FXML
    void initialize () {
        strategy.getItems().add(Policies.SelectionPolicy.SHORTEST_TIME);
        strategy.getItems().add(Policies.SelectionPolicy.SHORTEST_QUEUE);
    }
    @FXML
    void handleClicks(ActionEvent event) {
        int numClients = Integer.parseInt(numberOfClients.getText());
        int numServers = Integer.parseInt(numberOfServers.getText());
        int timeLimit = Integer.parseInt(timeLimitField.getText());
        int minArrival = Integer.parseInt(minArrivalTime.getText());
        int maxArrival = Integer.parseInt(maxArrivalTime.getText());
        int minProcessing = Integer.parseInt(minServiceTime.getText());
        int maxProcessing = Integer.parseInt(maxServiceTime.getText());
        Policies.SelectionPolicy chosenStrategy = strategy.getSelectionModel().getSelectedItem();
        if(event.getSource() == start) {
            SimulationManager manager = new SimulationManager(numClients, numServers,
                    timeLimit, minArrival, maxArrival, minProcessing, maxProcessing, chosenStrategy);
            Thread mainThread = new Thread(manager);
            mainThread.start();

            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                output.setText(manager.output);
                String filePath = "output.txt";
                try {
                    // Create a BufferedWriter to write to the file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

                    // Write the content to the file
                    writer.write(output.getText());

                    // Close the writer to release resources
                    writer.close();

                    System.out.println("Content has been written to the file successfully!");
                } catch (IOException e) {
                    // Handle any potential IO exceptions
                    e.printStackTrace();
                }
            });

        }

    }

}
