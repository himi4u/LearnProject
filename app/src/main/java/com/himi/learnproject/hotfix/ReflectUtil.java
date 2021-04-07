package com.himi.learnproject.hotfix;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field declaredField = clazz.getDeclaredField(name);

                if (!declaredField.isAccessible())
                    declaredField.setAccessible(true);
                return declaredField;
            }catch (NoSuchFieldException e){

            }

        }

        throw new NoSuchFieldException("Filed " + name + " not found in " + instance.getClass());
    }

    public static Method findMethod(Object instance,String name,Class<?>... parameterTypes) throws NoSuchMethodException {
        for(Class<?> clazz = instance.getClass();clazz !=null;clazz= clazz.getSuperclass()){
            try {
                Method declaredMethod = clazz.getDeclaredMethod(name, parameterTypes);
                if(!declaredMethod.isAccessible()){
                    declaredMethod.setAccessible(true);
                }
                return declaredMethod;
            }catch (NoSuchMethodException e){

            }

        }
        throw new NoSuchMethodException("method "+ name+" not found in "+ instance.getClass());
    }

    public static void expendFieldArray(Object instance,String filedName,Object[] patchElements) throws NoSuchFieldException, IllegalAccessException {
        Field dexElementsField = findField(instance, filedName);
        Object[] dexElements = (Object[]) dexElementsField.get(instance);
        Object[] newElements = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(), dexElements.length + patchElements.length);

        System.arraycopy(patchElements,0,newElements,0,patchElements.length);
        System.arraycopy(dexElements,0,newElements,patchElements.length,dexElements.length);
        dexElementsField.set(instance,newElements);
    }
}
