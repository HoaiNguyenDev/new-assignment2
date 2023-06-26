import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class Group extends Observable implements Visitor {
    protected final List<User> users;
    protected final List<NormalGroup> subGroups;

    public Group() {
        users = new ArrayList<>();
        subGroups = new ArrayList<>();
    }

    @Override
    public abstract String toString();

    public List<User> getUsers() {
        return users;
    }

    public List<NormalGroup> getGroups() {
        return subGroups;
    }

    public int countUser() {
        int result = users.size();
        for (Group g : subGroups) {
            result += g.countUser();
        }
        return result;
    }

    public int countGroup() {
        int result = 1;
        for (Group g : subGroups) {
            result += g.countGroup();
        }
        return result;
    }

    public int countMessage() {
        int result = 0;
        for (User user : users) {
            result += user.getTweets().size();
        }
        for (Group g : subGroups) {
            result += g.countMessage();
        }
        return result;
    }

    public double percentPositive() {
        double total = 0, positive = 0;
        for (User user : users) {
            total += user.getTweets().size();
            for (Tweet tweet : user.getTweets()) {
                if(tweet.isPositive())
                    positive++;
            }
        }
        for (Group g : subGroups) {
            total += g.countMessage();
        }
        return positive / total;
    }

    public void addUser(User user) {
        this.users.add(user);
        setChanged();
        notifyObservers();
    }

    public void addGroup(NormalGroup group) {
        this.subGroups.add(group);
        setChanged();
        notifyObservers();
    }

    public User findUser(String uid) {
        for (User u : this.users) {
            if (u.getUid().equalsIgnoreCase(uid)) return u;
        }
        for (Group g : this.subGroups) {
            User result = g.findUser(uid);
            if (result != null)
                return result;
        }
        return null;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public boolean isUser() {
        return false;
    }

    public boolean isExisted(Group g) {
        if(this == g) return true;
        return this.subGroups.stream().anyMatch(group -> group.isExisted(g));
    }

    public boolean isExisted(User u) {
        return this.users.stream().anyMatch(user -> user.isExisted(u));
    }

}
