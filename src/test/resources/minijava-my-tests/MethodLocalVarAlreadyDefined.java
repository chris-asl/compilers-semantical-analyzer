class MethodLocalVarAlreadyDefined {
    public static void main(String[] args) {
    }
}

class A {
    public int setValue(int x) {
        int x;
        x = 4;
        return x;
    }
}