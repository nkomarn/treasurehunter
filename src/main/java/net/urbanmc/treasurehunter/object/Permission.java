package net.urbanmc.treasurehunter.object;

public enum Permission {

    COMMAND_BASE ("command"),
    START_SUB ("start"),
    SPAWN_SUB ("spawn"),
    CANCEL_SUB ("cancel");




    private String permission;

    Permission(String permission) {
        this.permission = "treasurehunter." + permission;
    }

    @Override
    public String toString() {
        return permission;
    }
}
