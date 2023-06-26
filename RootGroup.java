public class RootGroup extends Group {
    private static volatile RootGroup instance;

    private RootGroup() {
    }

    @Override
    public String toString() {
        return "ROOT";
    }

    public static RootGroup getInstance() {
        if (instance == null) {
            synchronized (RootGroup.class) {
                if (instance == null) {
                    instance = new RootGroup();
                }
            }
        }
        return instance;
    }

    @Override
    public int countGroup() {
        return super.countGroup() - 1;
    }

}
