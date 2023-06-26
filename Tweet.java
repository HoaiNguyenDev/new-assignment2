import java.util.Date;

public class Tweet implements Comparable<Tweet> {
    private final Date date;
    private final String content;

    public Tweet(Date date, String content) {
        this.date = date;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean isPositive() {
        return content.contains("good") || content.contains("excellent") || content.contains("great");
    }

    @Override
    public int compareTo(Tweet o) {
        return date.compareTo(o.date);
    }

    public Date getDate() {
        return this.date;
    }
}
