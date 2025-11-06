package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

public class ASTVisitor extends CtScanner {

    List<CtElement> allElement = new ArrayList<>();

    public void printCSV(String arg) {
        try {
            FileWriter fw = new FileWriter("..\\create_data\\" + arg, false);
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {
                for (CtElement element : allElement) {
                    pw.print(element.getClass() + " : " + element);
                    pw.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*if(isField(fieldRead)){
            System.out.println(fieldRead.getVariable().getQualifiedName()+ "("+fieldRead.getPosition().getLine()+")");
        }
        
        System.out.println("1 : " + fieldRead.getVariable().getQualifiedName() + " : " + fieldRead.getPosition().getLine());
        if (fieldRead.getParent() != null) {
            System.out.println("Parent " + fieldRead.getParent().getClass());
        }
        if (fieldRead.getTarget() != null) {
            System.out.println("Target " + fieldRead.getTarget().getClass());
        }
        System.out.println("2 : " + fieldRead.getVariable().getDeclaringType().getQualifiedName());
        System.out.println("3 : " + fieldRead.getType().getQualifiedName());

        if (isOnlyField(fieldRead)) {
            System.out.println(fieldRead.getVariable().getQualifiedName() + " : " + fieldRead.getPosition().getLine());
            System.out.println(fieldRead.getVariable().getDeclaringType().getQualifiedName());
            System.out.println(fieldRead.getType().getQualifiedName());
        }
        CtElement parent = fieldRead.getParent();
        System.out.println("getType() : " + fieldRead.getType().getQualifiedName());
        System.out.println("getVariable() : " + fieldRead.getVariable().getQualifiedName() + " (fieldRead Line : " + fieldRead.getPosition().getLine() + " )");
        if (parent != null) {
            System.out.println("getParent() : " + parent.getClass());
        }
        System.out.println("getClass() : " + fieldRead.getClass());
        System.out.println("getTarget() : " + fieldRead.getTarget().getClass());
        if (parent instanceof CtInvocation parentMethod) {
            if (parentMethod.getTarget() instanceof CtFieldRead target) {
                System.out.println("getParent().getTarget() : " + target);
                System.out.println("getType() : " + target.getType().getQualifiedName() + "\n");
            }
        }
        //System.out.println("getParent().getTarget()"+);*/
    public boolean isField(CtFieldRead fieldRead) {
        if (!isInvocation(fieldRead) && isLastField(fieldRead)) {
            return true;
        }
        return false;
    }

    public boolean isInvocation(CtFieldRead fieldRead) {        //instance.method()かどうか判別
        CtElement parent = fieldRead.getParent();
        if (parent != null && parent instanceof CtInvocation parentMethod) {
            if (parentMethod.getTarget() != null && parentMethod.getTarget() instanceof CtFieldRead target) {
                if (target.getVariable().getQualifiedName().equals(fieldRead.getVariable().getQualifiedName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLastField(CtFieldRead fieldRead) {         //instance.fieldの時fieldのみを通す
        CtElement parent = fieldRead.getParent();
        if (parent != null && parent instanceof CtFieldAccess parentField) {
            if (fieldRead.getType().getQualifiedName().equals(parentField.getVariable().getDeclaringType().getQualifiedName())) {
                return false;
            }
        }
        return true;
    }

    public boolean isOnlyField(CtFieldRead fieldRead) {         //System.out.println(field)のfieldとclass.fieldを判断でき，
        CtElement parent = fieldRead.getParent();               //class.method()を弾くことができる．
        if (fieldRead.getTarget() instanceof CtTypeAccess) {      //System.outのように，あるクラスの静的フィールドへのアクセス
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
