package com.example.calculate;

import java.util.ArrayList;
import java.util.List;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
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
        if (isOnlyField(fieldRead)) {
            //宣言クラスを特定して、同じ場合ATLDに違う場合ATFDに追加する
        }
    }

    @Override
    public <T extends Object> void visitCtFieldWrite(spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
        if(isOnlyField(fieldWrite)){
            //宣言クラスを特定して、同じ場合ATLDに違う場合ATFDに追加する
        }

    }

    private boolean isForeignClass(String nameOfClass) {
        return true;
    }

    public boolean isOnlyField(CtFieldAccess fieldRead) {         //System.out.println(field)のfieldとclass.fieldを判断でき，
        CtElement parent = fieldRead.getParent();               //class.method()を弾くことができる．
        if (fieldRead.getTarget() instanceof CtTypeAccess) {      //System.outのように，静的フィールドへのアクセス
            return true;
        }

        if (parent == null) {                                   //親要素がなく∧fieldRead⇒純粋
            return true;
        }
        if (parent instanceof CtInvocation parentMethod) {
            if (parentMethod.getTarget() == null) {
                return true;
            } else {
                if (parentMethod.getTarget() instanceof CtFieldRead target) {
                    if (!target.getType().getQualifiedName().equals(fieldRead.getType().getQualifiedName())) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        } else if (parent instanceof CtFieldRead) {            //class.fieldの時，classとfieldで2回判別されるのを防ぐ
            return false;
        } else {
            return true;
        }
        return false;
    }
}
