class MultipleClassesAndInheritance {
    public static void main(String[] array){
    	int x;
    	x = 4;
    }
}

class Container {
	int[] x;
	public int bar(int y) {
		Atest a;
		Btest b;
		y = this.get(b);
		return y;
	}

	public int get(Atest a) {
		return 4;
	}
}

class Atest {
	int x;
	public int add(int x, boolean y) {
		int res;
		res = x;
		return res;
	}
}

class Btest extends Atest {
	int x;
	public int add(int x, boolean y) {
		int res;
		res = x + 2;
		return res;
	}
}

