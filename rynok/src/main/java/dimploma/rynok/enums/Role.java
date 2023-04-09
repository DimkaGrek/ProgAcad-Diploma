package dimploma.rynok.enums;

public enum Role {
    USER,
    ADMIN;

    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
