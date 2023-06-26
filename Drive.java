import javafx.application.Application;

public class Drive {
    public static void main(String[] args) {
        try {
            Application.launch(AdminUI.class, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
