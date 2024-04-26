package application;

/*
 * TreeNode class creates nodes that are used to build trees 
 * data is the counter of bytes / characters 
 * char byte is the byte read from the file 
 * huffmanCode represents huffman code for that byte 
 * is Main , to check if the node is a really read from the file 
 * and its not a result of adding two other nodes */

public class TreeNode<T extends Comparable<?>> {
	int data; // counter
	byte charByte; // char
	String huffmanCode = "";
	boolean isMain = true;
	protected TreeNode<T> leftChild;
	protected TreeNode<T> rightChild;

	public TreeNode() {
	}

	public TreeNode(int data, String hString, byte charByte, boolean ismain) {
		this.data = data;
		this.huffmanCode = hString;
		this.charByte = charByte;
		this.isMain = ismain;
	}

	public TreeNode(int data, String hString) {
		this.data = data;
		this.huffmanCode = hString;

	}

	// print datainOrder traversal
	public String inOrderTraversal() {
		String result = "";
		if (this.leftChild != null)
			result += this.leftChild.inOrderTraversal();
		if (this.isLeaf() || this.isMain == true)
			// if the node is leaf and its a main node --> add it to result
			result += this.data + "(" + this.huffmanCode + ")" + charByte + " ";
		if (this.rightChild != null)
			result += this.rightChild.inOrderTraversal();
		return result;
	}

	/*
	 * insert method : insets nodes into tree
	 */
	public boolean insert(Counter item1, Counter item2) {

		int data1 = item1.intCount;
		int data2 = item2.intCount;
		// target node is rightChild node , insert it.
		if (this.rightChild != null && this.rightChild.isMain == false && this.rightChild.data == (data1 + data2))
			if (this.rightChild.insert(item1, item2))
				return true;

		// target node is leftChild node , insert it.
		if (this.leftChild != null && this.leftChild.isMain == false && this.leftChild.data == (data1 + data2))
			return this.leftChild.insert(item1, item2);

		// to get to the inner level of the tree
		// check if the nodes can be add to the right , else check left

		if (this.rightChild != null && this.leftChild != null && this.rightChild.isMain == false)
			if (this.rightChild.insert(item1, item2) == true)
				return true;
			else
				return this.leftChild.insert(item1, item2);
		else if (this.rightChild != null && this.leftChild != null && this.leftChild.isMain == false)
			if (this.leftChild.insert(item1, item2) == true)
				return true;
			else
				return false;
		// if node don't have left or right child and sum of data = its data , then add.
		else if (this.rightChild == null && this.leftChild == null && this.data == (data1 + data2)
				&& this.isMain == false) {
			if (this.rightChild == null)
				this.rightChild = new TreeNode<T>(data1, this.huffmanCode + "1", item1.byteCount, item1.isMain);
			if (this.leftChild == null)
				this.leftChild = new TreeNode<T>(data2, this.huffmanCode + "0", item2.byteCount, item2.isMain);
			return true;
		}
		return false;

	}

	public int compareTo(T object) {
		return this.compareTo(object);
	}

	public String toString() {
		return data + "   ";
	}

	public boolean isLeaf() {
		return this.leftChild == null && this.rightChild == null;
	}

	public TreeNode<T> getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(TreeNode<T> leftChild) {
		this.leftChild = leftChild;
	}

	public TreeNode<T> getRightChild() {
		return rightChild;
	}

	public void setRightChild(TreeNode<T> rightChild) {
		this.rightChild = rightChild;
	}

}
