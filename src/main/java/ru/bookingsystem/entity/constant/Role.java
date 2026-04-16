package ru.bookingsystem.entity.constant;

import lombok.Getter;

import java.util.Set;

public enum Role {
    ROOT(Set.of(
            Permission.BOOK_RESOURCE,
            Permission.ADD_RESOURCE,
            Permission.EDIT_RESOURCE,
            Permission.DELETE_RESOURCE,
            Permission.VIEW_AUDIT,
            Permission.MANAGE_USER,
            Permission.MANAGE_ROLE,
            Permission.MANAGE_COMPANIES
    )),
    OWNER(Set.of(
            Permission.BOOK_RESOURCE,
            Permission.ADD_RESOURCE,
            Permission.EDIT_RESOURCE,
            Permission.DELETE_RESOURCE,
            Permission.VIEW_AUDIT,
            Permission.MANAGE_USER,
            Permission.MANAGE_ROLE
    )),
    ADMIN(Set.of(
            Permission.BOOK_RESOURCE,
            Permission.ADD_RESOURCE,
            Permission.EDIT_RESOURCE,
            Permission.DELETE_RESOURCE,
            Permission.VIEW_AUDIT,
            Permission.MANAGE_USER
            )),
    USER(Set.of(Permission.BOOK_RESOURCE));

    @Getter
    private String name;
    private final Set<Permission> permissions;

    Role(Set<Permission> permissions){
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission){
        return permissions.contains(permission);
    }
}
