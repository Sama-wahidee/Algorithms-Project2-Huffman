package application;

/*
 * Used to Create trees */

public class Tree<T extends Comparable<T>> {
	protected TreeNode<T> root ; 
	
	public boolean insert(Counter item1, Counter item2){
		return root.insert(item1, item2);
	}
	

	
	
	public String inOrderTraversal(){
		if (root != null )
			return root.inOrderTraversal();
		return null ; 
	}

	//print data in order traversal 
	public void traverseInOrder() {
		if (this.root != null)
			this.root.inOrderTraversal();
		System.out.println();
	}
	
	public int compareTo(T object){
		return this.compareTo(object);
	}
	

}
