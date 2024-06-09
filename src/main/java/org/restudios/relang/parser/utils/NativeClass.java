package org.restudios.relang.parser.utils;

import org.restudios.relang.parser.ast.types.values.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class NativeClass  {

    private final String name;
    private final List<NativeMethod> nativeMethods;
    private final ArrayList<Consumer<NativeMethodArguments>> onInstantiate = new ArrayList<>();

    public NativeClass(String name, List<NativeMethod> nativeMethods) {
        this.name = name;
        this.nativeMethods = nativeMethods;
    }
    public NativeClass(String name, NativeMethod... nativeMethods) {
        this.name = name;
        this.nativeMethods = new ArrayList<>(Arrays.asList(nativeMethods));
    }
    public NativeClass(String name, NativeMethodBuilder... nativeMethods) {
        this.name = name;
        this.nativeMethods = Arrays.stream(nativeMethods).map(NativeMethodBuilder::build).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public List<NativeMethod> getNativeMethods() {
        return nativeMethods;
    }

    public void init(Context context){
        context.getNativeClasses().add(this);
    }

    public NativeClass addOnInstantiate(Consumer<NativeMethodArguments> event){
        this.onInstantiate.add(event);
        return this;
    }
    public NativeClass removeOnInstantiate(Consumer<NativeMethodArguments> event){
        this.onInstantiate.remove(event);
        return this;
    }

    public ArrayList<Consumer<NativeMethodArguments>> getOnInstantiate() {
        return this.onInstantiate;
    }
}
