package org.jenjetsu.com.core.entity;

public enum Role {
    MANAGER("MANAGER"), ABONENT("ABONENT");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
