import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class AdminUI extends Application implements Observer {
    private final Button btnPositivePercent = new Button("Show Positive Percent");
    private final Button btnMessageTotal = new Button("Show Message Total");
    private final Button btnGroupTotal = new Button("Show Group Total");
    private final Button btnUserTotal = new Button("Show User Total");
    private final Button btnUserView = new Button("Open User View");
    private final Button btnAddGroup = new Button("Add Group");
    private final TextArea txtGID = new TextArea("Group ID");
    private final TextArea txtUID = new TextArea("User ID");
    private final Button btnAddUser = new Button("Add User");
    final GridPane centerPane = new GridPane();
    final BorderPane leftPane = new BorderPane();
    private final TreeView<Visitor> treeView = new TreeView<>();
    private final Image groupIcon = new Image("group.png", 20, 20, true, true);
    private final Image userIcon = new Image("user.png", 20, 20, true, true);
    final Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void start(Stage adminStage) {
        RootGroup.getInstance().addObserver(this);
        adminStage.setTitle("Admin Control Panel");
        BorderPane rootPane = new BorderPane();
        Scene scene = new Scene(rootPane);

        updateTreeView();
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        leftPane.setTop(new Label("Tree View"));
        leftPane.setLeft(treeView);
        rootPane.setLeft(leftPane);

        txtUID.setPrefSize(20, 10);
        centerPane.add(txtUID, 0, 0);
        txtUID.setOnMouseClicked(this::textareaOnClick);

        btnAddUser.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centerPane.add(btnAddUser, 1, 0);
        btnAddUser.setOnAction(event2 -> addUser());

        txtGID.setPrefSize(20, 10);
        centerPane.add(txtGID, 0, 1);
        txtGID.setOnMouseClicked(this::textareaOnClick);

        btnAddGroup.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centerPane.add(btnAddGroup, 1, 1);
        btnAddGroup.setOnAction(event1 -> addGroup());

        btnUserView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centerPane.add(btnUserView, 0, 2, 2, 1);
        btnUserView.setOnAction(event1 -> openUserView());

        centerPane.add(new Text(""), 0, 3);

        btnUserTotal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centerPane.add(btnUserTotal, 0, 4);
        btnUserTotal.setOnAction(event -> showAlert(Alert.AlertType.INFORMATION, String.valueOf(RootGroup.getInstance().countUser()), "Total user"));

        btnGroupTotal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centerPane.add(btnGroupTotal, 1, 4);
        btnGroupTotal.setOnAction(event -> showAlert(Alert.AlertType.INFORMATION, String.valueOf(RootGroup.getInstance().countGroup()), "Total group"));

        btnMessageTotal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centerPane.add(btnMessageTotal, 0, 5);
        btnMessageTotal.setOnAction(event -> showAlert(Alert.AlertType.INFORMATION, String.valueOf(RootGroup.getInstance().countMessage()), "Total message"));

        btnPositivePercent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centerPane.add(btnPositivePercent, 1, 5);
        btnPositivePercent.setOnAction(event -> {
            double percentage = RootGroup.getInstance().percentPositive();
            String result = "Not enough data";
            if (!Double.isNaN(percentage))
                result = NumberFormat.getPercentInstance().format(percentage);
            showAlert(Alert.AlertType.INFORMATION, result, "Percent positive message");
        });
        centerPane.setHgap(20);
        centerPane.setVgap(20);
        centerPane.setPadding(new Insets(10, 10, 10, 10));
        rootPane.setCenter(centerPane);

        adminStage.setScene(scene);
        adminStage.show();
    }

    private void textareaOnClick(MouseEvent mouseEvent) {
        TextArea source = (TextArea) mouseEvent.getSource();
        source.selectAll();
    }

    private void showAlert(Alert.AlertType alertType, String content, String title) {
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.show();
    }

    private void openUserView() {
        List<User> selectedUsers = getAllSelectedUsers();

        for (User user : selectedUsers) {
            Platform.runLater(() -> {
                try {
                    Stage userStage = new UserUI(user);
                    userStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void addUser() {
        if (txtUID.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Type user ID to add", "ERROR");
        }

        List<Group> selectedGroups = getAllSelectedGroups();
        if (selectedGroups.size() > 1) {
            showAlert(Alert.AlertType.ERROR, "Choose only one group to add user", "ERROR");
            return;
        }

        User newUser = new User(txtUID.getText().trim());
        if (RootGroup.getInstance().isExisted(newUser)) {
            showAlert(Alert.AlertType.ERROR, "User with this ID is existed", "ERROR");
            return;
        }
        if (selectedGroups.size() < 1) {
            RootGroup.getInstance().addUser(newUser);
        } else {
            selectedGroups.get(0).addUser(newUser);
        }
        txtUID.setText("User ID");
    }

    private void addGroup() {
        if (txtGID.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Type group ID to add", "ERROR");
        }

        List<Group> selectedGroups = getAllSelectedGroups();
        if (selectedGroups.size() > 1) {
            showAlert(Alert.AlertType.ERROR, "Choose only one group to add group", "ERROR");
            return;
        }

        NormalGroup newGroup = new NormalGroup(txtGID.getText().trim());
        if (RootGroup.getInstance().isExisted(newGroup)) {
            showAlert(Alert.AlertType.ERROR, "Group with this ID is existed", "ERROR");
            return;
        }
        if (selectedGroups.size() < 1) {
            RootGroup.getInstance().addGroup(newGroup);
        } else {
            selectedGroups.get(0).addGroup(newGroup);
        }
        txtGID.setText("Group ID");
        newGroup.addObserver(this);
    }

    private void updateTreeView() {
        TreeItem<Visitor> root = new TreeItem<>(RootGroup.getInstance());
        addChild(root, RootGroup.getInstance());
        root.setExpanded(true);
        treeView.setRoot(root);
    }

    private void addChild(TreeItem<Visitor> tree, Group group) {
        TreeItem<Visitor> treeItem;
        for (User u : group.getUsers()) {
            treeItem = new TreeItem<>(u, new ImageView(userIcon));
            tree.getChildren().add(treeItem);
        }

        for (NormalGroup g : group.getGroups()) {
            treeItem = new TreeItem<>(g, new ImageView(groupIcon));
            treeItem.setExpanded(true);
            tree.getChildren().add(treeItem);
            addChild(treeItem, g);
        }
    }

    private List<User> getAllSelectedUsers() {
        return treeView.getSelectionModel().getSelectedItems()
                .stream()
                .map(TreeItem::getValue)
                .filter(Visitor::isUser)
                .map(node -> (User) node)
                .collect(Collectors.toList());
    }

    private List<Group> getAllSelectedGroups() {
        return treeView.getSelectionModel().getSelectedItems()
                .stream()
                .map(TreeItem::getValue)
                .filter(Visitor::isGroup)
                .map(node -> (Group) node)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Observable o, Object arg) {
        updateTreeView();
    }
}
