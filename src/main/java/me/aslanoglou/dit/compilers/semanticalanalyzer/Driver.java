package me.aslanoglou.dit.compilers.semanticalanalyzer;

import me.aslanoglou.dit.compilers.semanticalanalyzer.syntaxtree.*;

import java.io.*;

/**
 * Semantical Analyzer of MiniJava language
 * Compilers - 2nd Assignmnent
 * Author: Chris Aslanoglou
 *
 * The analysis consists of three phases (a.k.a. 3 Visitors)
 *
 * Some info on the Visitors:
 *      1.  The 1st one is collecting classNames and the superClassRelations
 *      2.  This one, is collecting the info about each class, its fields and methods
 *              There are some Containers here, that represent a Class, a Function (method) and a Variable
 *              This were used for the implementation and representation of the equivalent structures of the given program
 *      3.  This one does the type-checking, supported by a symbolTable
 *              The symbol table contains all the info gathered by the two previous visitors, and the actual symbol
 *              table is a VariableName to VariableContainer mapping.
 *              Since all variables are to be defined at the start of a method, this simplifies things, and generally
 *              two Scopes can be defined (there could be only one, but two was more modular)
 *              The two Scopes, are: [a] Class Scope (all its fields) and [b] Function Scope (all its formal parameters
 *              and its local vars).
 *
 *  For error reporting, a RuntimeException is thrown
 */
class Driver {
    public static void main (String [] args){
        if(args.length == 0){
            System.err.println("Usage: java Driver [<inputFile>]+");
            System.exit(1);
        }
        for (int i = 0; i < args.length; i++) {
            System.out.println("================================================");
            System.out.println("Checking file '" + args[i] + "'");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(args[i]);
                MiniJavaParser parser = new MiniJavaParser(fis);
                // Pass 1 - ClassNames collection ------------------------------------------------------------------
                ClassNameCollector classNameCollector = new ClassNameCollector();
                Goal tree = parser.Goal();
                tree.accept(classNameCollector, null);
                System.out.println("[1/3] Class name collection phase completed");
                // Pass 2 - ClassMembers collection ----------------------------------------------------------------
                ClassMembersVisitor classMembersVisitor =
                        new ClassMembersVisitor(classNameCollector.verifiedClassNames,
                                classNameCollector.superClassRelation);
                tree.accept(classMembersVisitor, "Phase2");
                System.out.println("[2/3] Class members and methods info collection phase completed");
                // Pass 3 - Type checking  -------------------------------------------------------------------------
                TypeCheckingVisitor typeCheckingVisitor =
                        new TypeCheckingVisitor(classMembersVisitor.verifiedClassNames,
                                classMembersVisitor.superClassRelation, classMembersVisitor.classInfo);
                tree.accept(typeCheckingVisitor, "Phase3");
                System.out.println("[3/3] Type checking phase completed");
                System.out.println("[\u2713] All checks passed");
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (fis != null) fis.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            System.out.println("================================================");
        }
    }
}
