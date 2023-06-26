import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

public class UserUI extends Stage implements Observer {
    private final TextArea txtUID = new TextArea("User ID");
    private final Button btnFollowUser = new Button("Follow User");
    private final TextArea txtTweet = new TextArea("Tweet Message");
    private final Button btnPostTweet = new Button("Post Tweet");
    final ObservableList<String> following = FXCollections.observableArrayList();
    final ObservableList<String> newFeed = FXCollections.observableArrayList();
    private final User mainUser;
    final Alert alert = new Alert(Alert.AlertType.ERROR);

    public UserUI(User mainUser) {
        super();
        this.mainUser = mainUser;
        this.mainUser.addObserver(this);
        init();
        updateFollowingListView();
        updateNewFeed();
    }

    private void init() {
        setTitle("User View: " + mainUser.getUid());

        GridPane mainPane = new GridPane();
        Scene scene = new Scene(mainPane, 400, 500);


        txtUID.setMaxSize(250, 250);
        mainPane.add(txtUID, 0, 0);
        txtUID.setOnMouseClicked(this::textareaOnClick);

        btnFollowUser.setMaxSize(250, 250);
        mainPane.add(btnFollowUser, 1, 0);
        btnFollowUser.setOnAction(actionEvent -> followUser());

        ListView<String> lsvFollowing = new ListView<>(following);
        mainPane.add(lsvFollowing, 0, 1, 2, 1);

        txtTweet.setMaxSize(250, 250);
        mainPane.add(txtTweet, 0, 2);
        txtTweet.setOnMouseClicked(this::textareaOnClick);

        btnPostTweet.setMaxSize(250, 250);
        mainPane.add(btnPostTweet, 1, 2);
        btnPostTweet.setOnAction(actionEvent -> tweet());

        ListView<String> lsvNewFeed = new ListView<>(newFeed);
        mainPane.add(lsvNewFeed, 0, 3, 2, 1);

        mainPane.setHgap(20);
        mainPane.setVgap(20);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        setScene(scene);
    }

    private void tweet() {
        mainUser.tweet(txtTweet.getText());
        txtTweet.setText("Tweet Message");
    }

    private void followUser() {
        User userToFollow = RootGroup.getInstance().findUser(txtUID.getText().trim());
        if (userToFollow == null) {
            showAlert("User has ID " + txtUID.getText() + " is not exist", "ERROR 1");
            return;
        }
        if (mainUser.equals(userToFollow)) {
            showAlert("Cannot follow yourself", "ERROR 2");
            return;
        }
        if (!mainUser.follow(userToFollow))
            showAlert("You has already follow this user", "ERROR 3");
        txtUID.setText("User ID");
        userToFollow.addObserver(this);
    }

    private void showAlert(String content, String title) {
        alert.setContentText(content);
        alert.setTitle(title);
        alert.show();
    }

    private void textareaOnClick(MouseEvent mouseEvent) {
        TextArea source = (TextArea) mouseEvent.getSource();
        source.selectAll();
    }

    @Override
    public void update(Observable o, Object arg) {
        updateFollowingListView();
        updateNewFeed();
    }

    private void updateFollowingListView() {
        this.following.clear();
        for (User u : mainUser.getFollowings()) {
            this.following.add(u.getUid());
        }
    }

    private void updateNewFeed() {
        this.newFeed.clear();
        Queue<Tweet> tweets = new PriorityQueue<>();
        tweets.addAll(mainUser.getTweets()
                .stream()
                .map(t -> new Tweet(t.getDate(), mainUser.getUid() + ": " + t.getContent()))
                .collect(Collectors.toList()));

        for (User u : mainUser.getFollowings()) {
            tweets.addAll(u.getTweets()
                    .stream()
                    .map(t -> new Tweet(t.getDate(), u.getUid() + ": " + t.getContent()))
                    .collect(Collectors.toList()));
        }
        while (!tweets.isEmpty()) {
            newFeed.add(tweets.poll().getContent());
        }
    }
}
