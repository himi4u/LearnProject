package com.himi.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;

public class ASMTest {

    public static void main(String[] args) {

        try {
            /**
             * 获得待插桩的字节码数据
             */
            FileInputStream fileInputStream = new FileInputStream("/Users/wh-js/Downloads/LearnProject/asm/src/test/java/com/himi/asm/InjectTest.class");
            /**
             * 执行插桩修改class数据
             */
            ClassReader classReader = new ClassReader(fileInputStream);
            //创建ClassWrite 输出修改后的class
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7,classWriter){
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    System.out.println(name);
                    MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new MyMethodVisitor(Opcodes.ASM7,methodVisitor,access,name,descriptor);
                }
            };
            classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES);

            FileOutputStream fileOutputStream = new FileOutputStream("/Users/wh-js/Downloads/LearnProject/asm/src/test/java/com/himi/asm/InjectTest.class");
            fileOutputStream.write(classWriter.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    static class  MyMethodVisitor extends AdviceAdapter{

        private int start;
        private boolean canInject;
        protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            if(!canInject)
                return;
//            long l = System.currentTimeMillis()
            invokeStatic(Type.getType("Ljava/lang/System;"),new Method("currentTimeMillis","()J"));
            //本地局部变量接收结果
            //start 是索引，索引代表了第几个局部变量
            start = newLocal(Type.LONG_TYPE);
            storeLocal(start); //把结果赋值给局部变量
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            if(!canInject)
                return;
            invokeStatic(Type.getType("Ljava/lang/System;"),new Method("currentTimeMillis","()J"));//返回值是Long类型，L对应的签名是J
            //如果方法有int参数(I)J，如果方法有多个参数int long int :(ILI)J
            //本地局部变量接收结果
            int end = newLocal(Type.LONG_TYPE);//start 是索引，索引代表了第几个局部变量
            storeLocal(end); //把结果赋值给局部变量
            //sout输出
//            invokeStatic(Type.getType("java/io/PrintStream."));

            getStatic(Type.getType("Ljava/lang/System;"),"out",Type.getType("Ljava/io/PrintStream;"));
            //new指令 是申请内存 这里创建了一个StringBuilder  这里用的字符串的+，是java提供的语法糖，使用的是StringBuilder
            newInstance(Type.getType("Ljava/lang/StringBuilder;"));
            dup();
            //初始化，执行StringBuilder的构造方法
            invokeConstructor(Type.getType("Ljava/lang/StringBuilder;"),new Method("<init>","()V"));//void:V
            //字符串压入操作数栈
            visitLdcInsn("时间：");
            // append
            invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"),new Method("append","(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            // 结束时间-开始时间
            loadLocal(end);
            loadLocal(start);
            math(SUB,Type.LONG_TYPE);
            //把结果append
            invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"),new Method("append","(J)Ljava/lang/StringBuilder;"));
            //toString
            invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"),new Method("toString","()Ljava/lang/String;"));
            //打印
            invokeVirtual(Type.getType("Ljava/io/PrintStream;"),new Method("println","(Ljava/lang/String;)V"));
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            System.out.println(descriptor);
            if("Lcom/himi/asm/Himi;".equals(descriptor)){
                canInject = true;
            }else {
                canInject = false;
            }
            return super.visitAnnotation(descriptor, visible);
        }
    }
}
