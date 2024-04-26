package application;

/*
 *  header class contain attributes should be saved in the compressed file as a header 
 * like each byte and its frequency 
 */
import java.io.Serializable;

public class Header implements Serializable {
	private String FileName;
	private int headerSize;
	private int count[];
	private byte bytes[];

	public Header() {

	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Header(String FileName, int FileSize) {
		if (FileName.contains("\\")) {
			String FileNmae[] = FileName.replace('\\', '*').split("\\*");
			// last name in the Path
			this.FileName = FileNmae[FileNmae.length - 1];
		}
		this.headerSize = FileSize;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public int getFileSize() {
		return headerSize;
	}

	public void setFileSize(int fileSize) {
		headerSize = fileSize;
	}

	public int[] getCount() {
		return count;
	}

	public void setCount(int[] count) {
		this.count = count;
	}

}
