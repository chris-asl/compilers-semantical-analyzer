package me.aslanoglou.dit.compilers.semanticalanalyzer.containers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds information for classes.
 */
public class ClassContainer extends Container {
    // fieldName -> fieldType
    private Map<String, VariableContainer> fields;
    // functionName -> FunctionContainer
    private Map<String, FunctionContainer> functions;

    public ClassContainer(String name) {
        super(name, "class", name, 0, false);
        fields = new HashMap<>();
        functions = new HashMap<>();
    }


    public Map<String, FunctionContainer> getFunctions() {
        return functions;
    }


    public Map<String, VariableContainer> getFields() {
        return fields;
    }


    public void addField(String fieldName, String fieldType) {
        if (fields.containsKey(fieldName))
            throw new RuntimeException("Variable '" + fieldName + "' is already defined in this scope");
        VariableContainer var = new VariableContainer(fieldName, fieldType, this.name, this.fields.size() + 1);
        fields.put(fieldName, var);
    }


    @Override
    public String toString() {
        StringBuilder fieldsStr = new StringBuilder();
        if (fields.size() != 0) {
            Iterator it = fields.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry fieldEntry = (Map.Entry) it.next();
                VariableContainer field = (VariableContainer) fieldEntry.getValue();
                fieldsStr.append(field.getType() + " " + field.getName() + ",");
            }
            fieldsStr.delete(fieldsStr.length() - 1, fieldsStr.length());
        }
        return "ClassContainer {" +
                "name='" + name + "'" +
                ", \n\t\tfields=" + fieldsStr.toString() +
                ", \n\t\tfunctions=" + functions +
                '}';
    }
}
