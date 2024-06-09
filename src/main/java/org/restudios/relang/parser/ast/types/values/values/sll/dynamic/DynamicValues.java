package org.restudios.relang.parser.ast.types.values.values.sll.dynamic;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.EnumClassInstance;
import org.restudios.relang.parser.ast.types.values.RLEnumClass;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLMethod;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLThread;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.utils.NativeMethodBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@SuppressWarnings({"unused"})
public class DynamicValues {


    public static final ArrayList<String> sllClasses = new ArrayList<>(Arrays.asList(DynamicSLLClass.OBJECT, DynamicSLLClass.ENUM, DynamicSLLClass.STRING, DynamicSLLClass.ARRAY, DynamicSLLClass.CAST, DynamicSLLClass.RUNNABLE, DynamicSLLClass.THREAD, DynamicSLLClass.REFL_CLASSVARIABLE, DynamicSLLClass.REFL_CLASSMETHOD));

    public static ArrayList<SLLMethod> Runnable(DynamicSLLClass ci){
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new NativeMethodBuilder(ci, "run")
                .setHandler((arguments, context, callContext, instance) -> {
                    RLRunnable runnable = (RLRunnable) instance;
                    return runnable.getFunction().runMethod(context, callContext);
                }).buildSLL(ci));
        return methods;
    }
    public static ArrayList<SLLMethod> Thread(DynamicSLLClass ci){
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new NativeMethodBuilder(ci, "run")
                .setHandler((arguments, context, callContext, instance) -> {
                    context.getThreadManager().run((RLThread) instance, callContext);
                }).buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "createName")
                .setStatic(true)
                .setStrHandler((arguments, context, callContext, instance) -> "Thread"+Objects.hash(System.currentTimeMillis()))
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "getCurrentThread")
                .setStatic(true)
                .setHandler((arguments, context, callContext, instance) -> {
                    return callContext.getThreadManager().getCurrentThread(callContext);
                })
                .buildSLL(ci));
        return methods;
    }

    public static ArrayList<SLLMethod> enumeration(DynamicSLLClass ci){
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new SLLMethod(
                "name", false,
                (arguments, context, callContext, clazz) -> {
                    EnumClassInstance eci = (EnumClassInstance) clazz;
                    return Value.value(eci.getEnumValue().name(), context);
                }
                , ci, ci.getCreatedContext()));
        methods.add(new SLLMethod(
                "hash", false,
                (arguments, context, callContext, clazz) -> {
                    EnumClassInstance eci = (EnumClassInstance) clazz;
                    return Value.value(Objects.hash(eci.getRLClass(), eci.getEnumValue().name()));
                }
                , ci, ci.getCreatedContext()));
        methods.add(new SLLMethod(
                "ordinal", false,
                (arguments, context, callContext, clazz) -> {
                    EnumClassInstance eci = (EnumClassInstance) clazz;
                    RLEnumClass rlEnumClass = ((RLEnumClass) eci.getRLClass());
                    return Value.value(rlEnumClass.values.indexOf(eci.getEnumValue()));
                }
                , ci, ci.getCreatedContext()));

        return methods;
    }
    public static ArrayList<SLLMethod> CastUtils(DynamicSLLClass ci){
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new NativeMethodBuilder(ci, "strToInt")
                        .stringArgument("seq")

                        .setStatic(true)
                        .setIntHandler((arguments, context, callContext,instance) -> {
                            String sequence = arguments.getString("seq");
                            int value;
                            try {
                                value = Integer.parseInt(sequence);
                            }catch (NumberFormatException ignored){
                                throw new RLException("Number format exception", Type.numberFormat(context), context);
                            }
                            return value;
                        })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "strToFloat")
                        .stringArgument("seq")

                        .setStatic(true)
                        .setFloatHandler((arguments, context, callContext,instance) -> {
                            String sequence = arguments.getString("seq");
                            double value;
                            try {
                                value = Double.parseDouble(sequence);
                            }catch (NumberFormatException ignored){
                                throw new RLException("Number format exception", Type.numberFormat(context), context);
                            }
                            return value;
                        })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "intToStr")
                        .intArgument("value")

                        .setStatic(true)
                        .setStrHandler((arguments, context, callContext,instance) -> {
                            int value = arguments.getInt("value");
                            return String.valueOf(value);
                        })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "floatToStr")
                        .intArgument("value")

                        .setStatic(true)
                        .setStrHandler((arguments, context, callContext,instance) -> {
                            double value = arguments.getFloat("value");
                            return String.valueOf(value);
                        })
                .buildSLL(ci));
        return methods;
    }
    public static ArrayList<SLLMethod> obj(DynamicSLLClass ci){
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new NativeMethodBuilder(ci, "hash")
                .setIntHandler((arguments, context, callContext,instance) -> instance.hashCode())
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "getClass")
                .setHandler((arguments, context, callContext,instance) -> {
                    return instance.getRLClass().getReflectionClass(context);
                })
                .buildSLL(ci));
        return methods;
    }
    public static ArrayList<SLLMethod> array(DynamicSLLClass ci){
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new NativeMethodBuilder(ci, "get")
                .intArgument("index")
                .setHandler((arguments, context, callContext,instance) -> {
                    RLArray array = (RLArray) instance;
                    int index = arguments.getInt("index");
                    if(array.getValuesPointers().size() <= index || index < 0){
                        throw new RLException("Index "+index+" out of bounds for array with size "+array.getValuesPointers().size(), Type.arrayOutOfBounds(context), context);
                    }
                    return array.getValuesPointers().get(index);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "removeAt")
                .intArgument("index")
                .setHandler((arguments, context, callContext,instance) -> {
                    RLArray array = (RLArray) instance;
                    int index = arguments.getInt("index");
                    if(array.getValuesPointers().size() <= index || index < 0){
                        throw new RLException("Index "+index+" out of bounds for array with size "+array.getValuesPointers().size(), Type.arrayOutOfBounds(context), context);
                    }
                    array.getValuesPointers().remove(index);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "add")
                .arg("value")
                .setHandler((arguments, context, callContext,instance) -> {
                    RLArray array = (RLArray) instance;
                    Value value = arguments.getVariable("value").finalExpression();
                    array.add(CastExpression.cast(array.type, value, context));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "size")
                .setIntHandler((arguments, context, callContext,instance) -> {
                    RLArray array = (RLArray) instance;
                    return array.getValuesPointers().size();
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "join")
                .stringArgument("delimiter")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLArray array = (RLArray) instance;
                    List<String> stringItems = new ArrayList<>();
                    for (Value value : array.getValues()) {
                        stringItems.add(value.finalExpression().stringValue());
                    }
                    return String.join(arguments.getString("delimiter"), stringItems);
                })
                .buildSLL(ci));
        return methods;

    }
    public static ArrayList<SLLMethod> str(DynamicSLLClass ci) {
        ArrayList<SLLMethod> methods = new ArrayList<>();

        methods.add(new NativeMethodBuilder(ci, "length")
                .setIntHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.length();
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "substring")
                .intArgument("fromIndex")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.substring(arguments.getInt("fromIndex"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "substring")
                .intArgument("fromIndex")
                .intArgument("length")
                .setStrHandler((arguments, context,callContext,instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.substring(arguments.getInt("fromIndex"), arguments.getInt("length"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "starts")
                .stringArgument("string")
                .setBoolHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.startsWith(arguments.getString("string"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "ends")
                .stringArgument("string")
                .setBoolHandler((arguments, context, callContext, instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.endsWith(arguments.getString("string"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "removeFirst")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.substring(1);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "removeLast")
                .setStrHandler((arguments, context, callContext, instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.substring(0, string.value.length()-1);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "upper")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.toUpperCase();
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "lower")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr) instance;
                    return string.value.toLowerCase();
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "capitalize")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    String value = string.value;
                    if(value.isEmpty()){
                        return "";
                    }
                    if(value.length() == 1){
                        return string.value.toUpperCase();
                    }
                    return value.substring(0, 1).toUpperCase()+value.substring(1);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "trim")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    return string.value.trim();
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "matches")
                .stringArgument("regex")
                .setBoolHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    return string.value.matches(arguments.getString("regex"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "contains")
                .stringArgument("seq")
                .setBoolHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    return string.value.contains(arguments.getString("seq"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "equalsIgnoreCase")
                .stringArgument("seq")
                .setBoolHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    return string.value.equalsIgnoreCase(arguments.getString("seq"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "equals")
                .stringArgument("seq")
                .setBoolHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    return string.value.equals(arguments.getString("seq"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "charAt")
                .intArgument("pos")
                .setCharHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    int position = arguments.getInt("pos");
                    if(position < 0 || string.value.length() <= position){
                        throw new RLException("Index "+position+" out of bounds for string with length "+string.value.length(), Type.arrayOutOfBounds(context), context);

                    }
                    return string.value.charAt(position);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "indexOf")
                .stringArgument("seq")
                .setIntHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    return string.value.indexOf(arguments.getString("seq"));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "replace")
                .stringArgument("find")
                .stringArgument("replace")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    String find = arguments.getString("find");
                    String replace = arguments.getString("replace");
                    return string.value.replace(find, replace);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "append")
                .stringArgument("string")
                .setStrHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    String what = arguments.getString("string");
                    return string.value+what;
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "split")
                .stringArgument("by")
                .setHandler((arguments, context, callContext,instance) -> {
                    RLStr string = (RLStr)instance;
                    String by = arguments.getString("by");
                    RLArray array = new RLArray(instance.type(), context);
                    for (String s : string.value.split(by)) {
                        array.add(Value.value(s, context));
                    }
                    return array;
                })
                .buildSLL(ci));

        return methods;

    }
    public static ArrayList<SLLMethod> ClassVariable(DynamicSLLClass ci) {
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new NativeMethodBuilder(ci, "setValueForce")
                .objectArgument("object")
                .objectArgument("value")
                .setHandler((arguments, context, callContext,instance) -> {
                    String varname = instance.getVariable("name").finalExpression().stringValue();
                    ClassInstance obj = arguments.getClassInstance("object");
                    Variable v = obj.getVariable(varname);
                    v.setValueForce(CastExpression.cast(v.getType(), arguments.getVariable("value").finalExpression(), context));
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "setValue")
                .objectArgument("object")
                .objectArgument("value")
                .setHandler((arguments, context, callContext,instance) -> {
                    String varname = instance.getVariable("name").finalExpression().stringValue();
                    ClassInstance obj = arguments.getClassInstance("object");
                    Variable v = obj.getVariable(varname);
                    v.setValue(CastExpression.cast(v.getType(), arguments.getVariable("value").finalExpression(), context), context);
                })
                .buildSLL(ci));
        methods.add(new NativeMethodBuilder(ci, "getValue")
                .objectArgument("object")
                .setHandler((arguments, context, callContext,instance) -> {
                    String varname = instance.getVariable("name").finalExpression().stringValue();
                    ClassInstance obj = arguments.getClassInstance("object");
                    return obj.getVariable(varname).getValue().finalExpression();
                })
                .buildSLL(ci));

        return methods;

    }
    public static ArrayList<SLLMethod> ClassMethod(DynamicSLLClass ci) {
        ArrayList<SLLMethod> methods = new ArrayList<>();
        methods.add(new SLLMethod(
                "getReturnType", false,
                (arguments, context, callContext, clazz) -> {
                    return new NullValue(); // todo
                }
        , ci, ci.getCreatedContext()));
        methods.add(new SLLMethod(
                "getArguments", false,
                (arguments, context, callContext, clazz) -> {
                    return new NullValue(); // todo
                }
        , ci, ci.getCreatedContext()));



        return methods;

    }

    public static ArrayList<SLLMethod> getForMe(DynamicSLLClass ci) {
        String name = ci.getName();
        switch (name) {
            case DynamicSLLClass.OBJECT:
                return obj(ci);
            case DynamicSLLClass.ENUM:
                return enumeration(ci);
            case DynamicSLLClass.STRING:
                return str(ci);
            case DynamicSLLClass.ARRAY:
                return array(ci);
            case DynamicSLLClass.CAST:
                return CastUtils(ci);
            case DynamicSLLClass.REFL_CLASSVARIABLE:
                return ClassVariable(ci);
            case DynamicSLLClass.REFL_CLASSMETHOD:
                return ClassMethod(ci);
            case DynamicSLLClass.RUNNABLE:
                return Runnable(ci);
            case DynamicSLLClass.THREAD:
                return Thread(ci);
        }
        return new ArrayList<>();
    }
}
