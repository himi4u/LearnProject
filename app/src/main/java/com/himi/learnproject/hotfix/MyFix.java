package com.himi.learnproject.hotfix;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个是最基础的版本，记得手动添加存储权限，否则会找不到patch.dex
 */

public class MyFix {

    private static final String TAG = "MyFix";
    public static void installPatch(Application application, File patch) {
        ClassLoader classLoader = application.getClassLoader();

        ArrayList<File> files = new ArrayList<>();
        if (patch.exists()) {
            files.add(patch);
        }
        File cacheDir = application.getCacheDir();

        try {
            V23.install(classLoader,files,cacheDir);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static final class V23{
        private static void install(ClassLoader classLoader, List<File> additionalClassPathEntries, File optimizedDircetory) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            Field pathList = ReflectUtil.findField(classLoader, "pathList");
            Object dexPathList = pathList.get(classLoader);
            ArrayList<IOException> ioExceptions = new ArrayList<>();
            //从pathlist中找到makePathElements方法并执行
            Object[] pathElements = makePathElements(dexPathList, new ArrayList<File>(additionalClassPathEntries), optimizedDircetory, ioExceptions);
            ReflectUtil.expendFieldArray(dexPathList,"dexElements",pathElements);

            if(ioExceptions.size()>0){
                for (IOException ioException : ioExceptions) {
                    Log.e(TAG,"Exception in makePathElement",ioException);
                }
            }
        }
    }

    private static Object[] makePathElements(Object dexPathList, ArrayList<File> files, File optimizedDirectory, ArrayList<IOException> suppressedException) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method makePathElements = ReflectUtil.findMethod(dexPathList, "makePathElements", List.class, File.class, List.class);
        return (Object[]) makePathElements.invoke(dexPathList,files,optimizedDirectory,suppressedException);
    }
}
