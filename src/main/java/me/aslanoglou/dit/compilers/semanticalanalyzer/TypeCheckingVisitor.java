package me.aslanoglou.dit.compilers.semanticalanalyzer;


import me.aslanoglou.dit.compilers.semanticalanalyzer.containers.ClassContainer;
import me.aslanoglou.dit.compilers.semanticalanalyzer.containers.FunctionContainer;
import me.aslanoglou.dit.compilers.semanticalanalyzer.containers.VariableContainer;
import me.aslanoglou.dit.compilers.semanticalanalyzer.symboltable.SymbolTable;
import me.aslanoglou.dit.compilers.semanticalanalyzer.syntaxtree.*;
import me.aslanoglou.dit.compilers.semanticalanalyzer.visitor.GJDepthFirst;

import java.util.*;

public class TypeCheckingVisitor extends GJDepthFirst<String, String> {
    private SymbolTable symbolTable;
    private Map<String, ClassContainer> classInfo;
    private ClassContainer currentClass;
    private FunctionContainer currentFunction;

    public TypeCheckingVisitor(Set<String> verifiedClassNames,
                               Map<String, List<String>> superClassRelation,
                               Map<String, ClassContainer> classInfo) {
        symbolTable = new SymbolTable(verifiedClassNames, superClassRelation, classInfo);
        this.classInfo = classInfo;
    }

    //**************************************************//
    // SymbolTablePopulate related functions
    //**************************************************//
    // Add all fields of the given class into the current level of the symbol table
    public void insertVariables(Map<String, VariableContainer> variables) {
        if (variables.size() == 0)
            return;
        Iterator it = variables.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry fieldsEntry = (Map.Entry) it.next();
            // insert into current scope
            symbolTable.insert((String) fieldsEntry.getKey(), (VariableContainer) fieldsEntry.getValue());
        }
    }

    //**************************************************//
    //**************************************************//
    // Starting with a Class, there are two cases
    // Simple Class and Class with extends
    //**************************************************//
    @Override
    public String visit(ClassDeclaration n, String argu) {
        // Find out in which class we are now, and add its fields at the scope
        String className = n.f1.accept(this, "name");
        currentClass = symbolTable.getClass(className);
        if (n.f3.present()) {
            insertVariables(currentClass.getFields());
        }
        if (n.f4.present()) {
            n.f4.accept(this, argu);
        }
        return argu;
    }

    @Override
    public String visit(ClassExtendsDeclaration n, String argu) {
        // Find out in which class we are now, and add its fields at the scope
        String className = n.f1.accept(this, "name");
        currentClass = symbolTable.getClass(className);
        if (n.f5.present()) {
            insertVariables(currentClass.getFields());
        }
        if (n.f6.present()) {
            n.f6.accept(this, argu);
        }
        return argu;
    }

    @Override
    public String visit(MainClass n, String argu) {
        // special case
        // Find out in which class we are now, and add its fields at the scope
        String className = n.f1.accept(this, "name");
        currentClass = symbolTable.getClass(className);
        symbolTable.enter();
        FunctionContainer main = currentClass.getFunctions().get("main");
        currentFunction = main;
        // Add local vars into symbol table
        insertVariables(main.getVars());
        // check statements of main function
        if (n.f15.present()) {
            Enumeration<Node> statements = n.f15.elements();
            while (statements.hasMoreElements()) {
                Statement stmt = (Statement) statements.nextElement();
                stmt.accept(this, argu);
            }
        }
        symbolTable.exit();
        return argu;
    }
    //**************************************************//


    public String visit(MethodDeclaration n, String argu) {
        // Enter a new scope for the method declaration
        symbolTable.enter();
        // Get method name
        String methodName = n.f2.accept(this, "name");
        currentFunction = currentClass.getFunctions().get(methodName);
        // Insert formal parameters
        insertVariables(currentFunction.getParams());
        // and variables
        insertVariables(currentFunction.getVars());
        if (n.f8.present()){
            // iterate through all the statements
            Enumeration<Node> statements = n.f8.elements();
            while(statements.hasMoreElements()){
                Node stmt = statements.nextElement();
                stmt.accept(this, argu);
            }
        }
        String returnValueType = n.f10.accept(this, argu);
        ensureType(currentFunction.getType(), returnValueType);
        symbolTable.exit();
        return argu;
    }


    // Ensures that the variable with identifier == variableName is of the same type as the "foundType" one
    public void typeChecker(String variableName, String foundType) {
        // look up variable
        VariableContainer variable = symbolTable.lookup(variableName);
        if (variable == null)
            throw new RuntimeException("Cannot resolve symbol '" + variableName + "'");
        if (!variable.hasSameType(foundType)) {
            String error = "Incompatible types. Required '" + variable.getType() + "' and found: '" + foundType + "'";
            throw new RuntimeException(error);
        }
    }

    // Ensures that the foundType is of type "type"
    public static void ensureType(String type, String foundType) {
        if (!type.equals(foundType)) {
            String error = "Incompatible types. Required '" + type + "' and found: '" + foundType + "'";
            throw new RuntimeException(error);
        }
    }

    //**********************************************//
    // Level 1 Visitors (BFS - k = 1)
    //**********************************************//
    @Override
    public String visit(AssignmentStatement n, String argu) {
        String idType = n.f0.accept(this, argu);
        String exprType = n.f2.accept(this, argu);
        // If the identifier refers to a Class, check subtyping due to inheritance
        if (symbolTable.getClass(idType) != null && !idType.equals(exprType)) {
            List<String> supers = symbolTable.getSuperClasses(exprType);
            if (supers != null) {
                if (!supers.contains(idType)) {
                    String error = "Incompatible types. Required '" + idType + "' and found: '" + exprType + "'";
                    throw new RuntimeException(error);
                }
            }
            else {
                // Identifier isn't a subtype and types mismatch.
                String error = "Incompatible types. Required '" + idType + "' and found: '" + exprType + "'";
                throw new RuntimeException(error);
            }
        }
        else
            ensureType(idType, exprType);
        return exprType;
    }

    @Override
    public String visit(ArrayAssignmentStatement n, String argu) {
        // Identifier must be of type int[]
        // Expressions must be Integers
        String identifier = n.f0.accept(this, argu);
        ensureType("int[]", identifier);
        String idxExprType = n.f2.accept(this, argu);
        ensureType("int", idxExprType);
        String rvalueType = n.f5.accept(this, argu);
        ensureType("int", rvalueType);
        return rvalueType;
    }

    @Override
    public String visit(IfStatement n, String argu) {
        // Expression must be of type boolean
        String ifExprType = n.f2.accept(this, argu);
        ensureType("boolean", ifExprType);
        n.f4.accept(this, argu);
        n.f6.accept(this, argu);
        return "";
    }

    @Override
    public String visit(PrintStatement n, String argu) {
        // Expression can be either int or boolean
        String exprType = n.f2.accept(this, argu);
        if (!exprType.equals("boolean") && !exprType.equals("int")) {
            String error = "Incompatible types. Required 'int' or 'boolean' and found: '" + exprType + "'";
            throw new RuntimeException(error);
        }
        return "";
    }

    @Override
    public String visit(WhileStatement n, String argu) {
        // expr must be of type boolean
        String exprType = n.f2.accept(this, argu);
        ensureType("boolean", exprType);
        n.f4.accept(this, argu);
        return "";
    }

    public String visit(Block n, String argu) {
        if (n.f1.present()) {
            Enumeration<Node> statements = n.f1.elements();
            while (statements.hasMoreElements()) {
                Node stmt = statements.nextElement();
                stmt.accept(this, argu);
            }
        }
        return argu;
    }
    //**********************************************//
    // Level 2 Visitors (BFS - k = 2)
    //**********************************************//
    public String binaryOperators(String primAType, String primBType, String reqType, String retType) {
        ensureType(reqType, primAType);
        ensureType(reqType, primBType);
        return retType;
    }

    public String arithmeticBinaryOpers(String primAType, String primBType) {
        return binaryOperators(primAType, primBType, "int", "int");
    }

    @Override
    public String visit(Expression n, String argu) {
        return n.f0.accept(this, argu);
    }


    @Override
    public String visit(AndExpression n, String argu) {
        // Both clauses must be of type boolean
        String clause1Type = n.f0.accept(this, argu);
        String clause2Type = n.f2.accept(this, argu);
        return binaryOperators(clause1Type, clause2Type, "boolean", "boolean");
    }

    @Override
    public String visit(CompareExpression n, String argu) {
        // Both primary expressions must be integers
        String prim1Type = n.f0.accept(this, argu);
        String prim2Type = n.f2.accept(this, argu);
        return binaryOperators(prim1Type, prim2Type, "int", "boolean");
    }

    @Override
    public String visit(PlusExpression n, String argu) {
        // Both primary expressions must be integers
        String prim1Type = n.f0.accept(this, argu);
        String prim2Type = n.f2.accept(this, argu);
        return arithmeticBinaryOpers(prim1Type, prim2Type);
    }

    @Override
    public String visit(MinusExpression n, String argu) {
        // Both primary expressions must be integers
        String prim1Type = n.f0.accept(this, argu);
        String prim2Type = n.f2.accept(this, argu);
        return arithmeticBinaryOpers(prim1Type, prim2Type);
    }

    @Override
    public String visit(TimesExpression n, String argu) {
        // Both primary expressions must be integers
        String prim1Type = n.f0.accept(this, argu);
        String prim2Type = n.f2.accept(this, argu);
        return arithmeticBinaryOpers(prim1Type, prim2Type);
    }

    @Override
    public String visit(ArrayLength n, String argu) {
        String arrayType = n.f0.accept(this, argu);
        ensureType("int[]", arrayType);
        return "int";
    }

    @Override
    public String visit(ArrayLookup n, String argu) {
        String arrayType = n.f0.accept(this, argu);
        ensureType("int[]", arrayType);
        String idxType = n.f2.accept(this, argu);
        ensureType("int", idxType);
        return "int";
    }

    @Override
    public String visit(MessageSend n, String argu) {
        // PrimaryExpr must be of type class
        String className = n.f0.accept(this, argu);
        if (className.startsWith("class "))
            throw new RuntimeException("No static methods allowed");
        VariableContainer dummy = symbolTable.lookup(className);
        ClassContainer classContainer = symbolTable.getClass(dummy.getClassName());
        String methodName = n.f2.accept(this, "name");
        boolean superClassMethod = false;
        FunctionContainer functionContainer = null;
        if (!classContainer.getFunctions().containsKey(methodName)) {
            // look for the method in its superclasses
            List<String> supers = symbolTable.getSuperClasses(classContainer.getName());
            for (String superClass: supers) {
                ClassContainer superCl = symbolTable.getClass(superClass);
                if (superCl.getFunctions().containsKey(methodName)) {
                    superClassMethod = true;
                    functionContainer = superCl.getFunctions().get(methodName);
                    break;
                }
            }
            if (!superClassMethod) {
                String error = "Cannot find method '" + methodName + "' in '" + className + "'";
                throw new RuntimeException(error);
            }
        }
        StringBuilder argsB = new StringBuilder();
        if (n.f4.present()) {
            argsB.append(n.f4.accept(this, argu));
        }
        String[] args = argsB.toString().split(",");
        if (!superClassMethod)
            functionContainer = classContainer.getFunctions().get(methodName);
        functionContainer.checkCorrectParams(args, symbolTable.getSuperClassRelation());
        return functionContainer.getType();
    }


    // Will return all args, in this form type1, type2, type1
    @Override
    public String visit(ExpressionList n, String argu) {
        StringBuilder args = new StringBuilder();
        args.append(n.f0.accept(this, argu) + ", ");
        args.append(n.f1.accept(this, argu));
        args.delete(args.length() - 2, args.length() - 1);
        return args.toString().trim();
    }


    @Override
    public String visit(ExpressionTail n, String argu) {
        StringBuilder args = new StringBuilder();
        if (n.f0.present()) {
            Enumeration<Node> argsEnum = n.f0.elements();
            while(argsEnum.hasMoreElements()) {
                ExpressionTerm term = (ExpressionTerm) argsEnum.nextElement();
                String argType = term.f1.accept(this, argu);
                args.append(argType + ", ");
            }
        }
        return args.toString();
    }

    @Override
    public String visit(ExpressionTerm n, String argu) {
        return n.f1.accept(this, argu);
    }
    //**********************************************//
    // Level 3 Visitors (BFS - k = 3)
    //**********************************************//
    @Override
    public String visit(Clause n, String argu) {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(NotExpression n, String argu) {
        return n.f1.accept(this, argu);
    }

    @Override
    public String visit(PrimaryExpression n, String argu) {
        return n.f0.accept(this, argu);
    }


    @Override
    public String visit(FalseLiteral n, String argu) {
        return "boolean";
    }

    @Override
    public String visit(TrueLiteral n, String argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerLiteral n, String argu) {
        return "int";
    }

    @Override
    public String visit(ThisExpression n, String argu) {
        return currentClass.getName();
    }

    @Override
    public String visit(AllocationExpression n, String argu) {
        // the identifier must be a Class
        String className = n.f1.accept(this, "name");
        ClassContainer classContainer = symbolTable.getClass(className);
        if (classContainer == null)
            throw new RuntimeException("Cannot resolve symbol '" + className + "'");
        return className;
    }

    @Override
    public String visit(ArrayAllocationExpression n, String argu) {
        String idxType = n.f3.accept(this, argu);
        ensureType("int", idxType);
        return "int[]";
    }

    @Override
    public String visit(BracketExpression n, String argu) {
        return n.f1.accept(this, argu);
    }

    //**********************************************//
    // Returning the correct type
    //**********************************************//
    @Override
    public String visit(Type n, String argu) {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(ArrayType n, String argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, String argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, String argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, String argu) {
        if (argu.equals("name")) {
            return n.f0.withSpecials();
        }
        else {
            String identifier = n.f0.withSpecials();
            VariableContainer varID = symbolTable.lookup(identifier);
            if (varID == null)
                throw new RuntimeException("Cannot resolve symbol '" + identifier + "'");
            return varID.getType();
        }
    }
    //**********************************************//
}
