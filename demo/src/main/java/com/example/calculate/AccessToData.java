package com.example.calculate;

import java.util.ArrayList;
import java.util.List;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtScanner;

public class AccessToData extends CtScanner implements IAttribute {

    private List<String> ListOfATFD = new ArrayList<>();
    private List<String> ListOfATLD = new ArrayList<>();
    private List<String> ListOfError = new ArrayList<>();
    private List<String> ListOfClassInvoked = new ArrayList<>();
    private String nameOfParentClass;
    private List<String> namesOfSuperClasses;

    @Override
    public String getName() {
        return "ATFD";
    }

    @Override
    public void calculate(ClassMetrics node) {
        float sumOfATFD = 0;
        for (MethodMetrics methodMetrics : node.getMethodsMetrics()) {
            sumOfATFD = sumOfATFD + methodMetrics.getMetric(getName());
        }
        node.setMetric(getName(), sumOfATFD);
    }

    @Override
    public void calculate(MethodMetrics node) {
        ListOfError = new ArrayList<>();
        ListOfATLD = new ArrayList<>();
        ListOfATFD = new ArrayList<>();
        ListOfClassInvoked = new ArrayList<>();
        namesOfSuperClasses = new ArrayList<>();

        nameOfParentClass = node.getDeclaration().getParent(CtType.class).getQualifiedName();
        node.getDeclaration().accept(this);
        node.setAttribute("ListOfATFD", ListOfATFD);
        node.setAttribute("ListOfATLD", ListOfATLD);
        node.setAttribute("ListOfError", ListOfError);
    }

    @Override
    public <T extends Object> void visitCtInvocation(CtInvocation<T> invocation) {
        try {
            if (invocation.getExecutable().getType() != null) {
                String nameOfClass = invocation.getExecutable().getDeclaringType().getQualifiedName();
                String nameOfMethod = invocation.getExecutable().getSimpleName();
                if (!nameOfClass.equals(nameOfParentClass)) {
                    if (nameOfMethod.startsWith("get") || nameOfMethod.startsWith("set")) {
                        ListOfATFD.add(nameOfClass);
                    }
                } else {
                    ListOfATLD.add(nameOfClass);
                }
            } else {
                ListOfError.add(invocation.getExecutable().getSimpleName() + " error");
            }
        } catch (Exception e) {
            ListOfError.add(invocation.getExecutable().getSimpleName() + " " + e.getMessage());
        }
        super.visitCtInvocation(invocation);
    }

    @Override
    public <T extends Object> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
        try {
            if (fieldRead.getType() != null) {

            }
        } catch (Exception e) {
        }
    }

    @Override
    public <T extends Object> void visitCtFieldWrite(spoon.reflect.code.CtFieldWrite<T> fieldWrite) {

    }

    private boolean isForeignClass(String nameOfClass) {
        return true;
    }
}
