package ru.bookingsystem.entity.constant;

import lombok.Getter;

import java.util.Set;

public enum Role {
    OWNER(Set.of(
            Permission.BOOK_RESOURCE,
            Permission.ADD_RESOURCE,
            Permission.EDIT_RESOURCE,
            Permission.DELETE_RESOURCE,
            Permission.VIEW_AUDIT,
            Permission.MANAGE_USER,
            Permission.MANAGE_ROLE,
            Permission.INVITE_USER
    )),
    ADMIN(Set.of(
            Permission.BOOK_RESOURCE,
            Permission.ADD_RESOURCE,
            Permission.EDIT_RESOURCE,
            Permission.DELETE_RESOURCE,
            Permission.MANAGE_USER,
            Permission.VIEW_AUDIT,
            Permission.INVITE_USER
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
