package application;

/*
 * Huffman class store the value of huffman code for each byte which have cretin frequency*/

public class Huffman {
	private int intCount;
	private int bytee;
	private byte byteCount;
	private String Huffman;
	private char ascii;

	public Huffman() {
	}

	public Huffman(int intCount, String Huffman, byte byteCount) {
		this.intCount = intCount;
		this.byteCount = byteCount;
		this.Huffman = Huffman;
		if (byteCount < 0) {
			this.bytee = byteCount & 0xff;
		} else {
			this.bytee = byteCount;
		}

	}

	public Huffman(int intCount, String Huffman) {
		this.intCount = intCount;
		this.Huffman = Huffman;
	}

	public int getIntCount() {
		return intCount;
	}

	public byte getByteCount() {
		return byteCount;
	}

	public String getHuffman() {
		return Huffman;
	}

	public int getLength() {
		return getHuffman().length();
	}

	public char getAscii() {
		return (char) getByteCount();
	}

	public int getBytee() {
		return bytee;
	}

}
