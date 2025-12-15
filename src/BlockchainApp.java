import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BlockchainApp extends Application {

    /* -------- CORE -------- */
    private final Blockchain blockchain = new Blockchain();
    private final Wallet userA = new Wallet();

    /* -------- UI DATA -------- */
    private final ObservableList<String> mempoolData =
            FXCollections.observableArrayList();
    private final ObservableList<String> blockData =
            FXCollections.observableArrayList();

    /* -------- UI CONTROLS -------- */
    private final ListView<String> mempoolView = new ListView<>();
    private final ListView<String> blockView = new ListView<>();
    private final TextArea logArea = new TextArea();
    private final TextArea blockDetails = new TextArea();
    private Label statusLabel;

    /* -------- CHART -------- */
    private XYChart.Series<Number, Number> difficultySeries;

    @Override
    public void start(Stage stage) {

        System.out.println("JavaFX UI started");

        /* -------- TOP BAR -------- */
        Button addTx = new Button("Add Transaction");
        Button mine = new Button("Mine Block");
        Button validate = new Button("Validate");
        Button tamper = new Button("Tamper Block");

        statusLabel = new Label("● IDLE");
        statusLabel.getStyleClass().add("status-good");

        HBox topBar = new HBox(10, addTx, mine, validate, tamper, statusLabel);
        topBar.setPadding(new Insets(10));

        /* -------- MEMPOOL -------- */
        mempoolView.setItems(mempoolData);
        VBox mempoolBox = card("🕒 Mempool (Pending)", mempoolView);
        mempoolBox.setPrefWidth(280);

        /* -------- BLOCKCHAIN -------- */
        blockView.setItems(blockData);
        VBox blockBox = card("⛓ Blockchain", blockView);
        blockBox.setPrefWidth(380);

        /* -------- BLOCK DETAILS -------- */
        blockDetails.setEditable(false);
        VBox detailsBox = card("📦 Block Details", blockDetails);
        detailsBox.setPrefWidth(320);

        HBox center = new HBox(12, mempoolBox, blockBox, detailsBox);
        center.setPadding(new Insets(10));

        /* -------- DIFFICULTY CHART -------- */
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Block Index");
        yAxis.setLabel("Difficulty");

        LineChart<Number, Number> chart =
                new LineChart<>(xAxis, yAxis);
        chart.setTitle("Mining Difficulty");
        chart.setLegendVisible(false);

        difficultySeries = new XYChart.Series<>();
        chart.getData().add(difficultySeries);

        /* -------- LOGS -------- */
        logArea.setEditable(false);
        logArea.setPrefHeight(120);

        VBox root = new VBox(10, topBar, center, chart, logArea);
        root.setPadding(new Insets(10));

        /* -------- BUTTON ACTIONS -------- */

        addTx.setOnAction(e -> {
            Transaction tx = new Transaction(
                    userA.getPublicKey(),
                    "UserB",
                    Math.round(Math.random() * 100)
            );
            tx.signTransaction(userA);
            blockchain.addTransaction(tx);
            log("Signed transaction added to mempool");
            refresh();
        });

        mine.setOnAction(e -> {
            if (blockchain.getMempool().isEmpty()) {
                log("No transactions to mine");
                return;
            }

            statusLabel.setText("⛏ MINING");
            statusLabel.getStyleClass().setAll("status-warn");

            Block block = new Block(
                    blockchain.getChain().size(),
                    blockchain.getMempool(),
                    blockchain.getLatestBlock().hash
            );
            blockchain.addBlock(block);

            statusLabel.setText("✔ BLOCK MINED");
            statusLabel.getStyleClass().setAll("status-good");
            log("Block mined successfully");
            refresh();
        });

        validate.setOnAction(e -> {
            boolean valid = blockchain.isChainValid();
            statusLabel.setText(valid ? "✔ CHAIN VALID" : "⚠ INVALID CHAIN");
            statusLabel.getStyleClass().setAll(
                    valid ? "status-good" : "status-bad"
            );
            log("Validation result: " + valid);
        });

        tamper.setOnAction(e -> {
            blockchain.tamperBlock(1);
            statusLabel.setText("⚠ DATA TAMPERED");
            statusLabel.getStyleClass().setAll("status-bad");
            log("Block tampered");
            refresh();
        });

        blockView.setOnMouseClicked(e -> showBlockDetails());

        refresh();

        /* -------- SCENE + CSS -------- */
        Scene scene = new Scene(root, 1100, 780);
        scene.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm()
        );

        stage.setTitle("Blockchain Explorer – JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    /* -------- HELPERS -------- */

    private VBox card(String title, Control content) {
        Label label = new Label(title);
        VBox box = new VBox(8, label, content);
        box.setPadding(new Insets(12));
        box.getStyleClass().add("card");
        return box;
    }

    private void refresh() {
        mempoolData.clear();
        blockchain.getMempool()
                .forEach(tx -> mempoolData.add(tx.toString()));

        blockData.clear();
        for (Block b : blockchain.getChain()) {
            blockData.add("Block " + b.index + " | " + b.hash.substring(0, 12));
            b.transactions.forEach(tx ->
                    blockData.add("   ↳ " + tx.toString()));
        }

        difficultySeries.getData().clear();
        int i = 0;
        for (Integer d : blockchain.getDifficultyHistory()) {
            difficultySeries.getData()
                    .add(new XYChart.Data<>(i++, d));
        }
    }

    private void showBlockDetails() {
        int index = blockView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        int blockIndex = index / 2;
        if (blockIndex >= blockchain.getChain().size()) return;

        Block b = blockchain.getChain().get(blockIndex);

        blockDetails.setText(
                "Block #" + b.index +
                "\n\nHash:\n" + b.hash +
                "\n\nPrevious Hash:\n" + b.previousHash +
                "\n\nMerkle Root:\n" + b.merkleRoot +
                "\n\nNonce: " + b.nonce +
                "\nTransactions: " + b.transactions.size()
        );
    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
    }
}
