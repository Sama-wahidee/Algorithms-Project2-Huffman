package application;

/**
 * 	Decompress File is used to deCompres .huf files into its original format 
 * read Header from .huf file then creates its heap and find Huffman code for it 
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Decompress {
	int numOfByte;
	String HuffFile; // input file is .hfm file , output file is the
						// uncompressed file
	Header header;
	Huffman[] HuffTable;
	ObjectInputStream ObjectReader;
	FileInputStream inputFileSteam; // is .hfm file
	BufferedOutputStream bufferedSteam;
	BufferedInputStream inputFile;
	private FileOutputStream outf;

	int headerSize;

	Decompress(String HuffFile) {

		this.HuffFile = HuffFile;
		try {
			File InputFile = new File(HuffFile);
			FileInputStream inputFileSteam = new FileInputStream(InputFile);
			ObjectInputStream ObjectReader = new ObjectInputStream(inputFileSteam);
			BufferedInputStream inputFile = new BufferedInputStream(inputFileSteam);
			header = (Header) (ObjectReader.readObject());// to read the header
															// from compress
															// file
			File outputFile = new File(header.getFileName());// give the
																// original name
			outf = new FileOutputStream(outputFile); // of file (
			// which save
			// into compress
			// file) to new
			// file
			numOfByte = header.getFileSize();
			ObjectReader.close();
			inputFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createHeap() {
		/**
		 * create Heap that contain all bytes in the file in order to use it to build
		 * the tree this method create an array of heaps , keep minimising it until it
		 * size = 1
		 */
		Counter[] counter = new Counter[header.getCount().length];
		for (int i = 0; i < counter.length; i++)
			counter[i] = new Counter(header.getCount()[i], header.getBytes()[i]);
		Heap arrayofHeap[] = new Heap[header.getCount().length];

		// create arrayofheap
		for (int i = 0; i < counter.length; i++)
			arrayofHeap[i] = new Heap(new Counter[] { counter[i] });

		while (arrayofHeap.length > 1) {
			/*
			 * tempA is an array of Counters which is from array of Heap tempB is an array
			 * of Counters which is from array of Heap each array contain a max int which
			 * represents the count needed , all other items are its sum
			 */
			int j = 0;
			Counter tempA[] = new Counter[arrayofHeap[j].size];
			for (int i = 0; i < tempA.length; i++)
				tempA[i] = new Counter();
			for (int i = 0; i < tempA.length; i++)
				tempA[i] = (Counter) arrayofHeap[j].deleteMin();

			Arrays.sort(tempA);
			Counter t1 = tempA[tempA.length - 1];
			j++;
			Counter tempB[] = new Counter[arrayofHeap[j].size];
			for (int i = 0; i < tempB.length; i++)
				tempB[i] = new Counter();
			for (int i = 0; i < tempB.length; i++)
				tempB[i] = (Counter) arrayofHeap[j].deleteMin();
			Arrays.sort(tempB);
			Counter t2 = tempB[tempB.length - 1];
			for (int i = 0; i < tempA.length; i++)
				arrayofHeap[j].insert(tempA[i]);
			for (int i = 0; i < tempB.length; i++)
				arrayofHeap[j].insert(tempB[i]);

			arrayofHeap[j].insert(new Counter(t2.intCount + t1.intCount, false));
			arrayofHeap = Arrays.copyOfRange(arrayofHeap, 1, arrayofHeap.length);
			arrayofHeap = sort(arrayofHeap);

		}

		// insert tempA and tempB into arrayofHeap then insert the sum of the
		// two max to the same heap
		Counter tempResult[] = new Counter[arrayofHeap[0].size];
		for (int i = 0; i < tempResult.length; i++)
			tempResult[i] = new Counter();
		for (int i = 0; i < tempResult.length; i++)
			tempResult[i] = (Counter) arrayofHeap[0].deleteMin();

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
			if (tempResult[i].byteCount == 0 && tempResult[i + 1].byteCount != 0
					&& tempResult[i].intCount == tempResult[i + 1].intCount) // for
																				// similar
																				// int
																				// count
																				// causes
																				// while
																				// computing
																				// the
																				// nodes
																				// in
																				// the
																				// tree
				tree.insert(tempResult[i + 1], tempResult[i]);
			else
				tree.insert(tempResult[i], tempResult[i + 1]);

		}
		HuffTable = createHuffTable(tree.inOrderTraversal());
	}

	private Huffman[] createHuffTable(String result) {
		/**
		 * create table of all characters and its freq, byte representation , ... etc
		 */
		String[] all = result.split(" ");
		Huffman huffTable[] = new Huffman[all.length];
		for (int i = 0; i < huffTable.length; i++)
			if (all[i].equals("") == false)
				// create record
				huffTable[i] = new Huffman(Integer.parseInt(all[i].substring(0, all[i].indexOf('('))),
						all[i].substring(all[i].indexOf('(') + 1, all[i].indexOf(')')),
						Byte.parseByte(all[i].substring(all[i].indexOf(')') + 1)));
		return huffTable;

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
		Arrays.sort(sorted);
		int index[] = new int[array.length];
		for (int i = 0; i < index.length; i++)
			index[i] = -1;
		for (int i = 0; i < index.length; i++) {
			// search if item is already in index[], if so we can't repeat it,
			// then the element we looking for is After the index we got
			int IndexfirstTime = search(sorted, original[i], 0, sorted.length);
			while (true) {
				if (contain(index, IndexfirstTime))
					IndexfirstTime = search(sorted, original[i], IndexfirstTime + 1, sorted.length);
				else {
					index[i] = IndexfirstTime;
					break;
				}
			}
		}
		Heap<Counter> arrayofHeap[] = new Heap[array.length];
		// fill array
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

	public void readHuffFile() throws IOException, ClassNotFoundException {
		this.createHeap();
		// get last Name for file
		if (header.getFileName().contains("\\")) {
			String FileNmae[] = header.getFileName().replace('\\', '*').split("\\*");
			bufferedSteam = new BufferedOutputStream(new FileOutputStream(FileNmae[FileNmae.length - 1]));
		} else
			bufferedSteam = new BufferedOutputStream(new FileOutputStream(header.getFileName()));

		FileInputStream in = new FileInputStream(HuffFile);
		inputFile = new BufferedInputStream(in);
		ObjectInputStream ob = new ObjectInputStream(in);
		// read the header
		Header h = (Header) ob.readObject();

		byte[] output = new byte[4];
		int outputCount = 0;
		String bits = ""; // contain decompressed bits
		int counter = 0;
		ArrayList<String> listBit = new ArrayList<String>(); // contain huffman
																// code
		ArrayList<Byte> listchar = new ArrayList<Byte>();

		// while counter < numofbytes mean that the file still have bytes to
		// read
		// inputFile have bytes ..stay in the loop
		while (counter < numOfByte && inputFile.available() > 0) {
			// read byte
			byte tempByte = (byte) inputFile.read();
			byte tempbit = 0;
			int bitCount = 0; // num of bits in byte

			while (bitCount < 8 && counter < numOfByte) {
				for (int k = 7; k >= 0 && counter < numOfByte; k--) {
					tempbit = getBit(tempByte, k);
					bits += tempbit;// append
					if (listBit.size() == 0) {// list are empty
						// Search for huffman that starts with bits
						for (int j = 0; j < HuffTable.length; j++)
							if (HuffTable[j] != null && HuffTable[j].getHuffman().startsWith(bits + "")) {
								listBit.add(HuffTable[j].getHuffman());
								listchar.add(HuffTable[j].getByteCount());
							}
					} else {// if list have values , search on these values .
						for (int i = 0; i < listBit.size(); i++)
							if (listBit.get(i).startsWith(bits + "") == false) {
								// remove items that doesn't match
								listBit.remove(i);
								listchar.remove(i);
								i--;
							}

					}
					bitCount++;

					if (listBit.size() == 1) { // element found
						// add element to output byte array
						output[outputCount] = listchar.get(0);
						outputCount++;
						counter++;
						listBit.remove(0);
						listchar.remove(0);
						bits = "";
						if (outputCount == 4) {
							// write byte array into file when it is filled
							bufferedSteam.write(output);
							outf.write(output);
							bufferedSteam.flush();
							output = new byte[4];
							outputCount = 0;
						}
					}
				}
			}
		}
		if (outputCount != 0) {
			// write all reminder bytes
			for (int i = 0; i < outputCount; i++) {
				bufferedSteam.write(output[i]);
				outf.write(output[i]);
			}
		}
		bufferedSteam.close();
		outf.close();
		ob.close();
		inputFile.close();
	}

	public static byte getBit(byte ID, int position) {
		// return cretin bit in selected byte
		return (byte) ((ID >> position) & 1);
	}

}
