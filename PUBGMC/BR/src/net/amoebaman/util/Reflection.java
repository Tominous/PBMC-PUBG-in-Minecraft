package net.amoebaman.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public final class Reflection
{
    private static String _versionString;
    private static final Map<String, Class<?>> _loadedNMSClasses;
    private static final Map<String, Class<?>> _loadedOBCClasses;
    private static final Map<Class<?>, Map<String, Field>> _loadedFields;
    private static final Map<Class<?>, Map<String, Map<ArrayWrapper<Class<?>>, Method>>> _loadedMethods;
    
    static {
        _loadedNMSClasses = new HashMap<String, Class<?>>();
        _loadedOBCClasses = new HashMap<String, Class<?>>();
        _loadedFields = new HashMap<Class<?>, Map<String, Field>>();
        _loadedMethods = new HashMap<Class<?>, Map<String, Map<ArrayWrapper<Class<?>>, Method>>>();
    }
    
    private Reflection() {
    }
    
    public static synchronized String getVersion() {
        if (Reflection._versionString == null) {
            if (Bukkit.getServer() == null) {
                return null;
            }
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            Reflection._versionString = String.valueOf(name.substring(name.lastIndexOf(46) + 1)) + ".";
        }
        return Reflection._versionString;
    }
    
    public static synchronized Class<?> getNMSClass(final String className) {
        if (Reflection._loadedNMSClasses.containsKey(className)) {
            return Reflection._loadedNMSClasses.get(className);
        }
        final String fullName = "net.minecraft.server." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        }
        catch (Exception e) {
            e.printStackTrace();
            Reflection._loadedNMSClasses.put(className, null);
            return null;
        }
        Reflection._loadedNMSClasses.put(className, clazz);
        return clazz;
    }
    
    public static synchronized Class<?> getOBCClass(final String className) {
        if (Reflection._loadedOBCClasses.containsKey(className)) {
            return Reflection._loadedOBCClasses.get(className);
        }
        final String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        }
        catch (Exception e) {
            e.printStackTrace();
            Reflection._loadedOBCClasses.put(className, null);
            return null;
        }
        Reflection._loadedOBCClasses.put(className, clazz);
        return clazz;
    }
    
    public static synchronized Object getHandle(final Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle", (Class<?>[])new Class[0]).invoke(obj, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static synchronized Field getField(final Class<?> clazz, final String name) {
        Map<String, Field> loaded;
        if (!Reflection._loadedFields.containsKey(clazz)) {
            loaded = new HashMap<String, Field>();
            Reflection._loadedFields.put(clazz, loaded);
        }
        else {
            loaded = Reflection._loadedFields.get(clazz);
        }
        if (loaded.containsKey(name)) {
            return loaded.get(name);
        }
        try {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            loaded.put(name, field);
            return field;
        }
        catch (Exception e) {
            e.printStackTrace();
            loaded.put(name, null);
            return null;
        }
    }
    
    public static synchronized Method getMethod(final Class<?> clazz, final String name, final Class<?>... args) {
        if (!Reflection._loadedMethods.containsKey(clazz)) {
            Reflection._loadedMethods.put(clazz, new HashMap<String, Map<ArrayWrapper<Class<?>>, Method>>());
        }
        final Map<String, Map<ArrayWrapper<Class<?>>, Method>> loadedMethodNames = Reflection._loadedMethods.get(clazz);
        if (!loadedMethodNames.containsKey(name)) {
            loadedMethodNames.put(name, new HashMap<ArrayWrapper<Class<?>>, Method>());
        }
        final Map<ArrayWrapper<Class<?>>, Method> loadedSignatures = loadedMethodNames.get(name);
        final ArrayWrapper<Class<?>> wrappedArg = new ArrayWrapper<Class<?>>(args);
        if (loadedSignatures.containsKey(wrappedArg)) {
            return loadedSignatures.get(wrappedArg);
        }
        Method[] methods;
        for (int length = (methods = clazz.getMethods()).length, i = 0; i < length; ++i) {
            final Method m = methods[i];
            if (m.getName().equals(name) && Arrays.equals(args, m.getParameterTypes())) {
                m.setAccessible(true);
                loadedSignatures.put(wrappedArg, m);
                return m;
            }
        }
        loadedSignatures.put(wrappedArg, null);
        return null;
    }
}
