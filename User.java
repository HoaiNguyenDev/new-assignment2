import java.util.*;

public class User extends Observable implements Visitor {
    private final String uid;
    private final Set<User> followers;
    private final Set<User> followings;
    private final List<Tweet> tweets;

    public User(String uid) {
        this.uid = uid;
        followers = new HashSet<>();
        followings = new HashSet<>();
        tweets = new LinkedList<>();
    }

    public String getUid() {
        return uid;
    }

    public Set<User> getFollowings() {
        return followings;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    @Override
    public String toString() {
        return this.uid;
    }

    boolean follow(User user) {
        boolean isSuccess = this.followings.add(user);
        user.followers.add(this);
        if (!isSuccess) return false;

        setChanged();
        notifyObservers();
        return true;
    }

    void tweet(String tweet) {
        this.tweets.add(new Tweet(new Date(), tweet));

        setChanged();
        notifyObservers();
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public boolean isUser() {
        return true;
    }

    public boolean isExisted(User u) {
        return this.uid.equalsIgnoreCase(u.uid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
