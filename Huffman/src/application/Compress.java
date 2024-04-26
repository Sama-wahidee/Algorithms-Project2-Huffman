package application;

/**
 * Compress Class: compressing files is the main purpose for it 
 * compress Class have:
 * an array of Counter objects that have information about each byte and its freq
 * array of Huffman Code called huffTable.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.Arrays;

public class Compress {
	private int numOfByte = 0;
	private int HuffnumOfByte = 0;
	Counter[] counter = new Counter[256];
	String File; // file name
	double ratio;
	private Huffman[] HuffTable;
	private Header header;
	java.io.File outputFilee;
	double outtt;

	public Compress() {
	}

	public Compress(String File) {
		this.File = File;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	// read bytes from file
	public void readFile() throws Exception {
		// open file and read bytes from it
		RandomAccessFile aFile = new RandomAccessFile(File, "r");
		FileChannel inChannel = aFile.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(8);

		// initialize Counter
		for (int i = 0; i < counter.length; i++)
			counter[i] = new Counter();

	
		byte k = -128;
		for (int i = 0; i < counter.length; i++, k++)
			counter[i].setByteCount(k);

		while (inChannel.read(buffer) > 0) {
			buffer.flip();
			for (int i = 0; i < buffer.limit(); i++) {
				byte tempByte = buffer.get();
				counter[tempByte + 128].setIntCount(counter[tempByte + 128].getIntCount() +1);// unsigned
				numOfByte++;
			}
			buffer.clear();

		}
		inChannel.close();
		aFile.close();
		// sort counter array , then remove all zero freq bytes
		Arrays.sort(counter, 0, counter.length);
		int index = 255;
		for (int i = 0; i < counter.length; i++)
			if (counter[i].intCount != 0) {
				index = i; // index of first byte have freq > 0
				break;
			}
		// remove all zeros
		counter = Arrays.copyOfRange(counter, index, counter.length);

	}

	public void createHeap() throws IOException {
		/**
		 * create Heap that contain all bytes in the file in order to use it to build
		 * the tree this method create an array of heaps , keep minimising it until it
		 * size = 1
		 */
		Heap<Counter> arrayofHeap[] = new Heap[counter.length];

		// create arrayofheap
		for (int i = 0; i < counter.length; i++)
			arrayofHeap[i] = new Heap<Counter>(new Counter[] { counter[i] });

		while (arrayofHeap.length > 1) {
			int j = 0;
			/*
			 * tempA is an array of Counters which is from array of Heap tempB is an array
			 * of Counters which is from array of Heap each array contain a max int which
			 * represents the count needed , all other items are its sum
			 */
			Counter tempA[] = new Counter[arrayofHeap[j].size];
			for (int i = 0; i < tempA.length; i++)
				tempA[i] = new Counter();
			for (int i = 0; i < tempA.length; i++)
				tempA[i] = arrayofHeap[j].deleteMin();

			Arrays.sort(tempA);
			Counter t1 = tempA[tempA.length - 1];
			j++;
			Counter tempB[] = new Counter[arrayofHeap[j].size];
			for (int i = 0; i < tempB.length; i++)
				tempB[i] = new Counter();
			for (int i = 0; i < tempB.length; i++)
				tempB[i] = arrayofHeap[j].deleteMin();
			Arrays.sort(tempB);
			Counter t2 = tempB[tempB.length - 1];

			// insert tempA and tempB into arrayofHeap then insert the sum of the two max to
			// the same heap
			for (int i = 0; i < tempA.length; i++)
				arrayofHeap[j].insert(tempA[i]);
			for (int i = 0; i < tempB.length; i++)
				arrayofHeap[j].insert(tempB[i]);

			arrayofHeap[j].insert(new Counter(t2.intCount + t1.intCount, false));
			// we took two elements of arraofHeap[] but we insert only one
			// copy the items into the array without the empty index
			arrayofHeap = Arrays.copyOfRange(arrayofHeap, 1, arrayofHeap.length);
			arrayofHeap = sort(arrayofHeap);

		} // while end

		// tempResult contain all items with all summation
		Counter tempResult[] = new Counter[arrayofHeap[0].size];
		for (int i = 0; i < tempResult.length; i++)
			tempResult[i] = new Counter();
		for (int i = 0; i < tempResult.length; i++)
			tempResult[i] = arrayofHeap[0].deleteMin();

		// reverse array
		for (int i = 0, j = tempResult.length - 1; i < tempResult.length / 2; i++, j--) {
			Counter tempValue = tempResult[i];
			tempResult[i] = tempResult[j];
			tempResult[j] = tempValue;
		}

		// build tree
		Tree<Counter> tree = new Tree<Counter>();
		tree.root = new TreeNode<Counter>(tempResult[0].intCount, "", tempResult[0].byteCount, false);
		for (int i = 1; i < tempResult.length; i += 2) {
			// next if statement handle the case where there is two same values one of them
			// is main the other is a result of summation
			// so the main node have to be a leaf , other wise we will never be able to get
			// huffman code for that node
			if (tempResult[i].byteCount == 0 && tempResult[i + 1].byteCount != 0
					&& tempResult[i].intCount == tempResult[i + 1].intCount) // for similar int count causes while
																				// computing the nodes in the tree
				tree.insert(tempResult[i + 1], tempResult[i]);
			else
				tree.insert(tempResult[i], tempResult[i + 1]);

		}
		// create huffman table
		HuffTable = createHuffTable(tree.inOrderTraversal());
	}

	/*
	 * fileNmae method returns the original name of the file is used to avoid the
	 * case of occurring more than one . in the file name
	 */
	public String fileName(String fileName) {
		int indexOfDot = -1;
		for (int i = fileName.length() - 1; i >= 0; i--)
			if ((fileName.charAt(i) + "").equals(".")) {
				// index of last dot is found, break the loop
				indexOfDot = i;
				break;
			}
		return fileName.substring(0, indexOfDot);

	}

	/*
	 * write huffman code on the .huf file
	 */
	public void writeHuffmanCode() throws IOException {
		/*
		 * bufferedSteam the output buffer steam output[] an array will contain 4 bytes
		 * of huffman code, when its full will be written on output file outputByte:
		 * string contain huffman code, it used to make spliting bits easier
		 */

		/*
		 * create Header then write it to .hfm file
		 */
		header = new Header(File, numOfByte);
		int counterTemp[] = new int[counter.length];
		byte tempbytes[] = new byte[counter.length];
		for (int i = 0; i < counter.length; i++)
			counterTemp[i] = counter[i].intCount;
		for (int i = 0; i < counter.length; i++)
			tempbytes[i] = counter[i].byteCount;
		header.setCount(counterTemp);
		header.setBytes(tempbytes);

		// write header on huf file
		outputFilee = new java.io.File(fileName(File) + ".hfm");
		FileOutputStream outputFile = new FileOutputStream(outputFilee);
		ObjectOutputStream outF = new ObjectOutputStream(outputFile);
		outF.writeObject(header);

		// bufferedSteam is used to write data into .huf file
		BufferedOutputStream bufferedSteam = new BufferedOutputStream(outputFile);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(File));
		byte[] output = new byte[4];
		int count = 0;
		byte temp;
		int outputCount = 0; // index for output array
		String outputByte = "";

		// while loop which breaks when all bytes are read
		while (count < numOfByte) {
			/*
			 * read each byte, search for its huffman code write each four bytes to the
			 * output file
			 */
			count++;
			// read byte , then search if huffman code exists
			byte tempByte = (byte) in.read();
			String huffmanCode = search(HuffTable, tempByte);
			outputByte += huffmanCode;

			// if output byte is less than 8 bits , continue to append more bits to the
			// output String
			if (outputByte.length() < 8)
				continue;

			temp = toByte(outputByte.substring(0, 8));
			output[outputCount] = temp;
			outputCount++;
			HuffnumOfByte++;
			if (outputByte.length() > 8)
				// if huffmanCode contain more than 1 byte, we want to keep all bits more than
				// needed byte
				outputByte = outputByte.substring(8);
			else
				outputByte = "";
			// write 4 bytes into .hfm file when outputbyte [] is filled
			if (outputCount == 4) {

				bufferedSteam.write(output);
				outputCount = 0;
				output = new byte[4];
			}

		} // end while
			// if last bytes didn't make complete array to be send, we want to send them
		if (outputByte.length() != 0) {
			for (int i = 0; i < 8; i++)
				outputByte += "0";
			temp = toByte(outputByte.substring(0, 8));
			output[outputCount] = temp;
			outputCount++;
			HuffnumOfByte++;
		}
		if (outputCount != 0)
			bufferedSteam.write(output);

		bufferedSteam.close();
		in.close();
		outF.close();

	}

	/*
	 * convert String huffman code into byte usually used to write .huff bytes
	 */
	public static byte toByte(String huffmanCode) {
		/**
		 * bit wise OR, if the 8 bits are tested return result byte
		 */
		int bitCount = 0;
		byte temp = 0, iIndex = 0;
		for (iIndex = 0; iIndex < huffmanCode.length(); bitCount++, iIndex++) {
			if ((huffmanCode.charAt(iIndex) + "").equals("1"))
				// bit wise or
				// temp=(byte) (temp| (1<<(7-bitCount%8)));
				temp |= (1 << (7 - bitCount % 8));
			if (bitCount == 7)
				return temp;
		}
		return temp; // return revived value
	}

	private Huffman[] createHuffTable(String result) {
		/*
		 * create table of all characters and its freq, byte representation , ... etc
		 */
		String[] all = result.split(" ");
		Huffman huffTable[] = new Huffman[all.length];
		for (int i = 0; i < huffTable.length; i++) {
			if (all[i].equals("") == false)
				// create record // count of this before paranthesis,
				huffTable[i] = new Huffman(Integer.parseInt(all[i].substring(0, all[i].indexOf('('))),
						/* remainig huff string */all[i].substring(all[i].indexOf('(') + 1, all[i].indexOf(')')),
						/* huf bytes */Byte.parseByte(all[i].substring(all[i].indexOf(')') + 1)));
		}
		return huffTable;
	}

	private String search(Huffman[] huffTable, byte tempByte) {
		/**
		 * search for huffman code in haffman table
		 */
		for (int i = 0; i < huffTable.length; i++) {
			if (huffTable[i] != null && huffTable[i].getByteCount() == tempByte)
				return huffTable[i].getHuffman();
		}
		return ""; // return empty string if huffman code not found
	}

	public Heap<Counter>[] sort(Heap<Counter>[] array) {
		/*
		 * sort heap array we have two arrays of counters (original: have same order of
		 * Counters which occur in received heap[] and (sorted ) which have the item in
		 * original but sorted we sort sorted and then fill index[] index[] is an array
		 * that will contain the right index for each element in original, in order to
		 * have a sorted heap[] each item in original will be moved to its right index
		 * in newHeap[] using index[]
		 */
		Counter[] original = new Counter[array.length];
		Counter[] sorted = new Counter[array.length];
		for (int i = 0; i < array.length; i++) {
			// read data
			Counter tempA[] = new Counter[array[i].size];
			for (int j = 0; j < tempA.length; j++)
				tempA[j] = new Counter();
			for (int j = 0; j < tempA.length; j++)
				tempA[j] = (Counter) array[i].deleteMin();
			Arrays.sort(tempA);
			// take last element because all of the previous are sum of it
			original[i] = tempA[tempA.length - 1];
			sorted[i] = tempA[tempA.length - 1];
			array[i] = new Heap<Counter>(tempA);

		}
		// sort array
		Arrays.sort(sorted);
		int index[] = new int[array.length];
		for (int i = 0; i < index.length; i++)
			index[i] = -1;
		for (int i = 0; i < index.length; i++) {
			int IndexfirstTime = search(sorted, original[i], 0, sorted.length);
			while (true) {
				// search if item is already in index[], if so we can't repeat it, then the
				// element we looking for is After the index we got
				if (contain(index, IndexfirstTime))
					IndexfirstTime = search(sorted, original[i], IndexfirstTime + 1, sorted.length);
				else {
					index[i] = IndexfirstTime;
					break;
				}
			}
		}
		Heap<Counter> arrayofHeap[] = new Heap[array.length];

		// fill arrayOfHeap
		for (int i = 0; i < array.length; i++) {
			Counter tempA[] = new Counter[array[i].size];
			for (int j = 0; j < tempA.length; j++)
				tempA[j] = new Counter();
			for (int j = 0; j < tempA.length; j++)
				tempA[j] = array[i].deleteMin();

			arrayofHeap[index[i]] = new Heap<Counter>(tempA);

		}
		return arrayofHeap;
	}

	private boolean contain(int[] index, int indexfirstTime) {
		// search if that index already existed in index array
		for (int i = 0; i < index.length; i++)
			if (index[i] == indexfirstTime)
				return true;
		return false;
	}

	public int search(Counter[] sortedArray, Counter item, int from, int to) {
		/**
		 * search for an item in a sorted array and return its index , or -1 if not
		 * found
		 */
		for (int i = from; i < to; i++) {
			if (sortedArray[i].compareTo(item) == 0 && sortedArray[i].byteCount == item.byteCount)
				return i;
		}
		return 0;
	}

	public int getNumOfByte() {
		return numOfByte;
	}

	public void setNumOfByte(int numOfByte) {
		this.numOfByte = numOfByte;
	}

	public int getHuffnumOfByte() {
		return HuffnumOfByte;
	}

	public void setHuffnumOfByte(int huffnumOfByte) {
		HuffnumOfByte = huffnumOfByte;
	}

	public Counter[] getCounter() {
		return counter;
	}

	public void setCounter(Counter[] counter) {
		this.counter = counter;
	}

	public String getFile() {
		return File;
	}

	public void setFile(String file) {
		File = file;
	}

	public String getRatio() {
		ratio= (1 - ((double) getCompressedFileSize() / (double) getNumOfByte())) * 100;
		return String.format("%.2f", ratio);		
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public Huffman[] getHuffTable() {
		return HuffTable;
	}

	public void setHuffTable(Huffman[] huffTable) {
		HuffTable = huffTable;
	}

	public long getCompressedFileSize() {
		return outputFilee.length();
	}

}
