package com.example;

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

public class Visitor extends CtScanner {

    @Override
    public <T extends Object> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
        if(isOnlyField(fieldRead)){
            System.out.println(fieldRead.getVariable().getQualifiedName()+" : "+fieldRead.getPosition().getLine());
        }
        /*CtElement parent = fieldRead.getParent();
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

        super.visitCtFieldRead(fieldRead);

    }

    public boolean isOnlyField(CtFieldRead fieldRead) {         //System.out.println(field)のfieldとclass.fieldを判断でき，
        CtElement parent = fieldRead.getParent();               //class.method()を弾くことができる．
        if(fieldRead.getTarget() instanceof CtTypeAccess){      //System.outのように，あるクラスの静的フィールドへのアクセス
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
                }else{
                    return true;
                }
            }
        }else if(parent instanceof CtFieldRead){            //class.fieldの時，classとfieldで2回判別されるのを防ぐ
            return false;
        } else {
            return true;
        }
        return false;
    }

    @Override
    public <T extends Object> void visitCtInvocation(spoon.reflect.code.CtInvocation<T> invocation) {
        super.visitCtInvocation(invocation);
    }

    @Override
    public <T extends Object> void visitCtFieldWrite(spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
        //System.out.println("getType() : "+fieldWrite.getType().getQualifiedName());
        //System.out.println("getVariable() : "+fieldWrite.getVariable().getQualifiedName()+" (Line : "+fieldWrite.getPosition().getLine()+")");
        super.visitCtFieldWrite(fieldWrite);
    }

    /*List<String> nameOfClasses=new ArrayList<>();
    List<IAttribute> metricForMethod=new ArrayList<>();
    HashMap<CtClass,ClassMetrics> classesMetrics=new HashMap<>();


    public Visitor(){
        metricForMethod.add(new AccessToData());
    }

    @Override
    public <T extends Object> void visitCtClass(CtClass<T> ctClass) {
        nameOfClasses.add(ctClass.getQualifiedName());
        ClassMetrics classMetrics=new ClassMetrics(ctClass);
        classesMetrics.put(ctClass,classMetrics);
        super.visitCtClass(ctClass);
    }

    public void excuteMetrics(){
        for(CtClass clazz:classesMetrics.keySet()){
            Set<CtMethod> methods=clazz.getMethods();
            ClassMetrics classMetrics=classesMetrics.get(clazz);
            for(CtMethod method:methods){
                MethodMetrics methodMetrics=new MethodMetrics(method, classMetrics);
                for(IAttribute metric:metricForMethod){
                    metric.calculate(methodMetrics);
                }
                classMetrics.getMethodsMetrics().add(methodMetrics);
            }
        }
    }

        @SuppressWarnings("unchecked")
    public void printCSV(String arg) throws IOException {
        int allMethod=0;
        int allMethodError=0;
        int flag=0;
        int allMethodExpr = 0;
        int allMethodExprError = 0;
        try {
            FileWriter fw = new FileWriter("..\\create_data\\" + arg, false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.print("class");
            pw.print(",");
            pw.print("method");
            pw.print(",");
            pw.print("ATFD");
            pw.print(",");
            pw.print("ATLD");
            pw.print(",");
            pw.print("Error");
            pw.println();
            for (CtClass clazz : classesMetrics.keySet()) {
                ClassMetrics classMetrics = classesMetrics.get(clazz);
                for (MethodMetrics method : classMetrics.getMethodsMetrics()) {
                    allMethod+=1;
                    flag=0;
                    pw.print(clazz.getQualifiedName());
                    pw.print(",");
                    pw.print(method.getDeclaration().getSimpleName());
                    pw.print(",");
                    for (String ATFD : (List<String>) method.getAttribute("ListOfATFD")) {
                        pw.print(ATFD + " | ");
                        allMethodExpr += 1;
                    }
                    pw.print(",");
                    for (String ATLD : (List<String>) method.getAttribute("ListOfATLD")) {
                        pw.print(ATLD + " | ");
                        allMethodExpr += 1;

                    }
                    pw.print(",");
                    for (String Error : (List<String>) method.getAttribute("ListOfError")) {
                        if(flag==0){
                            allMethodError+=1;
                            flag=1;
                        }
                        pw.print(Error + " | ");
                        allMethodExpr += 1;
                        allMethodExprError += 1;

                    }
                    pw.println();

                }
            }
            pw.close();
            System.out.println("総メソッド数 : "+allMethod);
            System.out.println("総エラーありメソッド数 : "+allMethodError);
            System.out.println("総呼び出しメソッド数 : "+allMethodExpr);
            System.out.println("総エラー数 : "+allMethodExprError);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
