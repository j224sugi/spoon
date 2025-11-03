package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

public class ASTVisitor extends CtScanner {

    List<CtElement> allElement = new ArrayList<>();

    @Override
    public void scan(CtElement element) {
        if (element != null) {
            allElement.add(element);
        }
        super.scan(element);
    }

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
}
