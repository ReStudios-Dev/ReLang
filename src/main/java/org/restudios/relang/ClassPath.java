package org.restudios.relang;

import java.util.Objects;

@SuppressWarnings("unused")
public class ClassPath {
    private final String source;
    private final String code;
    private ClassSourceType sourceType;

    public ClassPath(String source, String code) {
        this.source = source;
        this.code = code;
    }

    public ClassSourceType getSourceType() {
        return sourceType;
    }

    public ClassPath setSourceType(ClassSourceType sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public String getSource() {
        return source;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "@"+source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassPath classPath = (ClassPath) o;
        return Objects.equals(source, classPath.source) && Objects.equals(code, classPath.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, code);
    }
    public enum ClassSourceType {
        SLL, NATIVE, USER
    }
}
