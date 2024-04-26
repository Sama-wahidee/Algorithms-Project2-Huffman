package application;

/**
 * heap class create heap using array 
 * min capacity 
 */
import java.util.*;

@SuppressWarnings("unchecked")
public class Heap<AnyType extends Comparable<AnyType>> implements Comparable<Heap<AnyType>> {
	private static final int CAPACITY = 2;
	protected int size; // Number of elements in heap
	protected AnyType[] heap; // The heap array

	public Heap() {
		size = 0;
		heap = (AnyType[]) new Comparable[CAPACITY];
	}

	/*
	 * Construct the binary heap given an array of items.
	 */
	public Heap(AnyType[] array) {
		size = array.length;
		heap = (AnyType[]) new Comparable[array.length + 1];

		System.arraycopy(array, 0, heap, 1, array.length);// we do not use 0 index

		buildHeap();
	}

	private void buildHeap() {
		for (int k = size / 2; k > 0; k--)
			sink(k);
	}

	private void sink(int k) {
		AnyType tmp = heap[k];
		int child;

		for (; 2 * k <= size; k = child) {
			child = 2 * k;

			if (child != size && heap[child].compareTo(heap[child + 1]) > 0)
				child++;

			if (tmp.compareTo(heap[child]) > 0)
				heap[k] = heap[child];
			else
				break;
		}
		heap[k] = tmp;
	}

	/**
	 * Deletes the top item
	 */
	public AnyType deleteMin() throws RuntimeException {
		if (size == 0)
			throw new RuntimeException();
		AnyType min = heap[1];
		heap[1] = heap[size--];
		sink(1);
		return min;
	}

	public AnyType getHead() {
		return this.heap[0];
	}

	/**
	 * Inserts a new item
	 */
	public void insert(AnyType x) {

		if (size == heap.length - 1)
			doubleSize();

		// Insert a new item to the end of the array
		int pos = ++size;

		// Percolate up
		for (; pos > 1 && x.compareTo(heap[pos / 2]) < 0; pos = pos / 2)
			heap[pos] = heap[pos / 2];

		heap[pos] = x;
	}

	public void insert(Heap<AnyType> x) {
		if (size == heap.length - 1)
			doubleSize();

		// Insert a new item to the end of the array
		int pos = ++size;

		// Percolate up
		for (; pos > 1 && x.getHead().compareTo(heap[pos / 2]) < 0; pos = pos / 2)
			heap[pos] = heap[pos / 2];

		heap[pos] = x.getHead();
	}

	private void doubleSize() {
		AnyType[] old = heap;
		heap = (AnyType[]) new Comparable[heap.length * 2];
		System.arraycopy(old, 1, heap, 1, size);
	}

	@Override
	public int compareTo(Heap<AnyType> o) {
		return 0;

	}
}
