class IncompatibleClassAssignment {
    public static void main(String[] args) {
    }
}

class A {
    B b;
    public int setValue(A a) {
        b = a;
        return 2;
    }
}

class B {}