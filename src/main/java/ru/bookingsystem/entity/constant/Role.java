package ru.bookingsystem.entity.constant;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(
            Permission.BOOK_RESOURCE,
            Permission.ADD_RESOURCE,
            Permission.EDIT_RESOURCE,
            Permission.DELETE_RESOURCE,
            Permission.MANAGE_USER,
            Permission.VIEW_AUDIT
            )),
    USER(Set.of(Permission.BOOK_RESOURCE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions){
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission){
        return permissions.contains(permission);
    }
}
