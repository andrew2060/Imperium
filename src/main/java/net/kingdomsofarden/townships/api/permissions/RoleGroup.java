package net.kingdomsofarden.townships.api.permissions;

public class RoleGroup {

    public static final RoleGroup ROOT = new RoleGroup("ROOT");
    public static final RoleGroup OWNER = new RoleGroup("OWNER");
    public static final RoleGroup ADMINISTRATOR = new RoleGroup("ADMINISTRATOR");
    public static final RoleGroup MODERATOR = new RoleGroup("MODERATOR");
    public static final RoleGroup MEMBER = new RoleGroup("MEMBER");
    public static final RoleGroup GUEST = new RoleGroup("GUEST");
    public static RoleGroup valueOf(String name) {
        return new RoleGroup(name.toUpperCase());
    }


    private final String name;

    public RoleGroup(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RoleGroup && ((RoleGroup) other).name.equals(this.name);
    }

    @Override
    public String toString() {
        return name;
    }



}
