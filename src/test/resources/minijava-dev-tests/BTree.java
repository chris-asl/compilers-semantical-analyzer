class Main {
	public static void main(String[] args) {
		System.out.println(new BTree().constr());
	}
}


class BTree {
	int childs;
	BTree subT;

	int constr() {
		childs = 10;
		subT = new BTree();
		System.out.println((subT.getSub()).getChilds());
		return 100;
	}

	BTree getSub() {
		return subT;
	}

	int getChilds() {
		return childs;
	}

}
