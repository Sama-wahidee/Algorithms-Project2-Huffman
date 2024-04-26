package application;

/*
 * Counter class contain huffman code
 * int count is the frequancy for each byte count 
 * isMain tells us if the byte occurs in the file(isMain = true) or not 
 * 
 */

public class Counter implements Comparable<Counter>{
	int intCount;
	byte byteCount;
	String Huffman;
	boolean isMain = true ; 
	
	public Counter() {
		
	}
	
	public Counter(int intCount, byte byteCount){
		this.intCount = intCount ; 
		this.byteCount =  byteCount;
		
	}
	public Counter(int intCount, boolean isMain){
		this.intCount = intCount ; 
		this.isMain = isMain; 
		
	}

	public int getIntCount() {
		return intCount;
	}

	public void setIntCount(int intCount) {
		this.intCount = intCount;
	}

	public byte getByteCount() {
		return  byteCount;
	}

	public void setByteCount(byte byteCount) {
		this.byteCount = byteCount;
	}

	@Override
	//compare between objects based on intCount for each.
	public int compareTo(Counter object) {
		if (this.intCount < object.getIntCount())
			return -1 ; 
		else if (this.getIntCount() > object.getIntCount() )
			return 1 ; 
		return 0 ;
		
		
	}

	
	
}
