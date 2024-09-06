package org.restudios;

public class VisibilityInfo {
    public boolean isPublic, isPrivate, isProtected, isStatic;

    public VisibilityInfo(boolean isPublic, boolean isPrivate, boolean isProtected, boolean isStatic) {
        this.isPublic = isPublic;
        this.isPrivate = isPrivate;
        this.isProtected = isProtected;
        this.isStatic = isStatic;
    }
}
