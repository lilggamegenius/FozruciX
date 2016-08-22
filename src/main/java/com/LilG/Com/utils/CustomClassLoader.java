package com.LilG.Com.utils;

/**
 * @author http://codeslices.net team
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Simple custom class loader implementation
 *
 */
public class CustomClassLoader extends ClassLoader {

    /**
     * The HashMap where the classes will be cached
     */
    private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

    /**
     * Simple usage of the CustomClassLoader implementation
     *
     * @param args
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    public static void main(String[] args) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            NoSuchMethodException, SecurityException, IllegalArgumentException,
            InvocationTargetException {
        CustomClassLoader loader = new CustomClassLoader();
        // This class should be in your application class path
        Class<?> c = loader.findClass("net.codeslices.test.TestClass");
        Object o = c.newInstance();
        Method m = c.getMethod("toString");
        System.out.println(m.invoke(o));
    }

    @Override
    public String toString() {
        return CustomClassLoader.class.getName();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        if (classes.containsKey(name)) {
            return classes.get(name);
        }

        byte[] classData;

        try {
            classData = loadClassData(name);
        } catch (IOException e) {
            throw new ClassNotFoundException("Class [" + name
                    + "] could not be found", e);
        }

        Class<?> c = defineClass(name, classData, 0, classData.length);
        resolveClass(c);
        classes.put(name, c);

        return c;
    }

    /**
     * Load the class file into byte array
     *
     * @param name
     *            The name of the class e.g. com.codeslices.test.TestClass}
     * @return The class file as byte array
     * @throws IOException
     */
    private byte[] loadClassData(String name) throws IOException {
        BufferedInputStream in = new BufferedInputStream(
                ClassLoader.getSystemResourceAsStream(name.replace(".", "/")
                        + ".class"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i;

        while ((i = in.read()) != -1) {
            out.write(i);
        }

        in.close();
        byte[] classData = out.toByteArray();
        out.close();

        return classData;
    }

}