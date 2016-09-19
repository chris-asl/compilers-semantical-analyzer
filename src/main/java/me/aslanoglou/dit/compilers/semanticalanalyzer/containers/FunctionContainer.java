package me.aslanoglou.dit.compilers.semanticalanalyzer.containers;

import me.aslanoglou.dit.compilers.semanticalanalyzer.TypeCheckingVisitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FunctionContainer extends Container{
    // ParamName -> ParamType
    private Map<String, VariableContainer> params;
    // VarName -> VarType
    private Map<String, VariableContainer> vars;

    public FunctionContainer(String name, String type, String className, int index) {
        super(name, type, className, index, true);
        params = new HashMap<>();
        vars = new HashMap<>();
    }

    public Map<String, VariableContainer> getParams() {
        return params;
    }

    public Map<String, VariableContainer> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        StringBuilder paramsStr = new StringBuilder();
        if (params.size() != 0) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry paramEntry = (Map.Entry) it.next();
                VariableContainer param = (VariableContainer) paramEntry.getValue();
                paramsStr.append(param.getType() + " " + param.getName() + ", ");
            }
            paramsStr.delete(paramsStr.length() - 2, paramsStr.length());
        }
        StringBuilder varsStr = new StringBuilder();
        if (vars.size() != 0) {
            Iterator it = vars.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry varEntry = (Map.Entry) it.next();
                VariableContainer var = (VariableContainer) varEntry.getValue();
                varsStr.append("\t" + var.getType() + " " + var.getName() + ";\n");
            }
            varsStr.delete(varsStr.length() - 1, varsStr.length());
        }
        return "\n" + type + " "  + name + "(" + paramsStr.toString() + ") {\n" + varsStr.toString() + "\n}\n";
    }

    public boolean isIdentical(FunctionContainer function) {
        // Check if return type is the same
        if (!this.type.equals(function.type))
            return false;
        else {
            // Check formalParameters, but firstly check their number
            if (this.params.size() != function.params.size())
                return false;
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry paramEntry = (Map.Entry) it.next();
                // Extract param from current and given
                VariableContainer param = (VariableContainer) paramEntry.getValue();
                // Check if it exists
                if (!function.params.containsKey(param.name))
                    return false;
                VariableContainer paramOther = (VariableContainer) function.params.get(param.name);
                if (!param.isIdenticalOrdered(paramOther))
                    return false;
            }
            return true;
        }
    }


    public void addParam(String paramName, String paramType) {
        if (params.containsKey(paramName))
            throw new RuntimeException("Variable '" + paramName + "' is already defined in this scope");
        VariableContainer param = new VariableContainer(paramName, paramType, this.className, this.params.size() + 1);
        params.put(paramName, param);
    }

    public void addVar(String varName, String varType) {
        if (vars.containsKey(varName))
            throw new RuntimeException("Variable '" + varName + "' is already defined in this scope");
        // Variable must not already be defined as a parameter.
        if (params.containsKey(varName))
            throw new RuntimeException("Variable '" + varName + "' is already defined in this scope");
        VariableContainer var = new VariableContainer(varName, varType, this.className, this.vars.size() + 1);
        vars.put(varName, var);
    }

    // responsible for returning the VariableContainer that represents the i-th parameter
    public VariableContainer getParam(int i) {
        for(String varName: params.keySet()) {
            VariableContainer variable = params.get(varName);
            if (variable.getOrder() == i)
                return variable;
        }
        return null;
    }

    public int getParamsSize() {
        return params.size();
    }

    public void checkCorrectParams(String[] args, Map<String, List<String>> classInfo) {
        // check equal number
        if (args[0].equals(""))
            return;
        if (args.length != params.size()){
            String error = "ParametersNumber - Expected: " + params.size() + " Found: " + args.length;
            throw new RuntimeException(error);
        }
        // Build array with the types of the parameters
        String[] paramTypes = new String[args.length];
        for(String paramName: params.keySet()) {
            VariableContainer tmp = params.get(paramName);
            paramTypes[tmp.getOrder() - 1] = tmp.getType();
        }
        for (int i = 0; i < args.length; i++) {
            // check for subType
            List<String> supers = classInfo.get(args[i]);
            if (supers != null) {
                if (!supers.contains(paramTypes[i])) {
                    String error = "Incompatible types. Required '" + paramTypes[i] + "' and found: '" + args[i] + "'";
                    throw new RuntimeException(error);
                }
            }
            else
                TypeCheckingVisitor.ensureType(paramTypes[i], args[i].trim());
        }
    }
}
