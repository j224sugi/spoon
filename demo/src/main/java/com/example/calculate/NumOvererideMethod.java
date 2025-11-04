package com.example.calculate;

import java.util.List;
import java.util.Set;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

public class NumOvererideMethod implements IAttribute {

    @Override
    public String getName() {
        return "BOvR";
    }

    @Override
    public void calculate(MethodMetrics node) {
        //do not do anything
    }

    @Override
    public void calculate(ClassMetrics node) {
        int overrideNum = 0;
        float ration = 0;
        List<CtMethod> superMethods = (List<CtMethod>) node.getAttribute("superClassMethods");
        CtTypeReference superClass = node.getDeclaration().getSuperclass();
        Set<CtMethod> classMethods = node.getDeclaration().getMethods();
        if (superClass == null) {       // 親クラスなし
            node.setMetric(getName(), -1);
            return;
        }
        if (classMethods != null && superMethods != null) {
            for (CtMethod method : classMethods) {
                if (isOverride(method, superMethods)) {
                    overrideNum += 1;
                }
            }
            ration = (float) overrideNum / classMethods.size();
            System.out.println(node.getDeclaration().getQualifiedName() + " : " + overrideNum + "  " + classMethods.size());
        }
        node.setMetric(getName(), ration);
    }

    public boolean isOverride(CtMethod newMethod, List<CtMethod> methods) {
        for (CtMethod method : methods) {
            if (isOverride(newMethod, method)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOverride(CtMethod childMethod, CtMethod superMethod) {
        if (!childMethod.getSimpleName().equals(superMethod.getSimpleName())) {
            return false;
        }

        List<CtParameter> childMethodParam = childMethod.getParameters();
        List<CtParameter> superMethodParam = superMethod.getParameters();
        if (childMethodParam.size() != superMethodParam.size()) {
            return false;
        }
        for (int i = 0; i < childMethodParam.size(); i++) {
            if (!childMethodParam.get(i).getType().equals(superMethodParam.get(i).getType())) {
                return false;
            }
        }
        CtTypeReference childMethodReturn = childMethod.getType();
        CtTypeReference superMethodReturn = superMethod.getType();
        if (!childMethodReturn.equals(superMethodReturn)) {
            return false;
        }
        return childMethodReturn.isSubtypeOf(superMethodReturn);
    }
}
