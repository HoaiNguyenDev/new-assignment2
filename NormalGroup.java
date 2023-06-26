public class NormalGroup extends Group {
    private final String gid;

    public NormalGroup(String gid) {
        this.gid = gid;
    }

    @Override
    public String toString() {
        return this.gid;
    }

    @Override
    public boolean isExisted(Group g) {
        if (this.gid.equalsIgnoreCase(((NormalGroup) g).gid)) return true;
        return super.isExisted(g);
    }
}
