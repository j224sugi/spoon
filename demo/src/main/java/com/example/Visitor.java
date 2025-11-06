package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.example.calculate.AccessToData;
import com.example.calculate.IAttribute;
import com.example.calculate.NumOvererideMethod;
import com.example.calculate.NumProtMembersInParent;
import com.example.calculate.SuperClass;
import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;

public class Visitor extends CtScanner {

    List<String> nameOfClasses = new ArrayList<>();
    List<IAttribute> metricForMethod = new ArrayList<>();
    List<IAttribute> metricForClass = new ArrayList<>();
    HashMap<CtClass, ClassMetrics> classesMetrics = new HashMap<>();

    public Visitor() {
        metricForMethod.add(new AccessToData());

        metricForClass.add(new AccessToData());
        metricForClass.add(new NumProtMembersInParent(nameOfClasses));
        metricForClass.add(new NumOvererideMethod());

    }

    @Override
    public <T extends Object> void visitCtClass(CtClass<T> ctClass) {
        nameOfClasses.add(ctClass.getQualifiedName());
        ClassMetrics classMetrics = new ClassMetrics(ctClass);
        classesMetrics.put(ctClass, classMetrics);
        super.visitCtClass(ctClass);
    }

    public void excuteMetrics() {
        for (CtClass clazz : classesMetrics.keySet()) {
            ClassMetrics classMetrics = classesMetrics.get(clazz);
            IAttribute superClass = new SuperClass();
            superClass.calculate(classMetrics);

        }
        classesMetrics.keySet().forEach(clazz -> {
            Set<CtMethod> methods = clazz.getMethods();
            ClassMetrics classMetrics = classesMetrics.get(clazz);
            for (CtMethod method : methods) {
                MethodMetrics methodMetrics = new MethodMetrics(method, classMetrics);
                for (IAttribute metric : metricForMethod) {
                    metric.calculate(methodMetrics);
                }
                classMetrics.getMethodsMetrics().add(methodMetrics);
            }
            for (IAttribute metric : metricForClass) {
                metric.calculate(classMetrics);
            }
        });
    }

    public void printCSV(String arg) throws IOException {
        int allMethod = 0;
        int allMethodError = 0;
        int allMethodExpr = 0;
        int allMethodExprError = 0;
        try {
            FileWriter fw = new FileWriter("..\\create_data\\" + arg, false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.print("class");
            pw.print(",");
            pw.print("NprotM");
            pw.print(",");
            pw.print("BOvR");
            pw.print(",");
            pw.print("ATFD");
            pw.print(",");
            pw.print("ATLD");
            pw.println();
            for (CtClass clazz : classesMetrics.keySet()) {
                ClassMetrics classMetrics = classesMetrics.get(clazz);
                pw.print(clazz.getQualifiedName());
                pw.print(",");
                pw.print(classMetrics.getMetric("NprotM"));
                pw.print(",");
                pw.print(classMetrics.getMetric("BOvR"));
                pw.print(",");
                pw.print(classMetrics.getMetric("ATFD"));
                pw.print(",");
                pw.print(classMetrics.getMetric("ATLD"));
                pw.println();
            }

            pw.close();
            System.out.println("総メソッド数 : " + allMethod);
            System.out.println("総エラーありメソッド数 : " + allMethodError);
            System.out.println("総呼び出しメソッド数 : " + allMethodExpr);
            System.out.println("総エラー数 : " + allMethodExprError);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
