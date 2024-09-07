# 🌟 Huffman Compression Tool 🌟

**Huffman Compression Tool** is a simple yet efficient application that allows users to compress and decompress files using the Huffman coding algorithm. This tool provides a graphical interface for easy file compression, and it also displays detailed statistics about the compression process.

## 📝 Project Overview

The Huffman Compression Tool implements the Huffman coding algorithm, which is a lossless data compression technique. The tool allows users to:

- Compress any file and generate a `.huf` compressed file.
- Decompress `.huf` files to retrieve the original data.

## 🔧 Huffman Coding Algorithm

Huffman coding assigns variable-length codes to characters based on their frequencies. Characters that occur more frequently are assigned shorter codes, while less frequent characters are assigned longer codes. This method ensures an efficient way to compress data while preserving all the information.

Key concepts used:

- **Priority Queue:** Used to build the Huffman tree by prioritizing characters with the lowest frequency.
- **Binary Tree:** The Huffman tree is constructed using nodes, where each leaf represents a character and its frequency.
- **Prefix Codes:** Huffman codes are prefix-free, meaning no code is a prefix of any other, ensuring a unique decoding process.

## 💻 Main Interface

The main interface provides users with two primary options:

- **Compress:** Select a file to compress using the Huffman algorithm.
- **Decompress:** Decompress an already compressed `.huf` file to restore the original data.

![1](https://github.com/user-attachments/assets/ec496169-d48f-4793-a7cd-73e8928550d2)

### Compression Success Dialog

Upon successful compression, the tool displays a dialog confirming the compression process.

![2](https://github.com/user-attachments/assets/b80d120a-f11f-4072-8cd9-1e62b6fff9ae)

## 📊 Huffman Table

During the compression process, the tool generates a Huffman table that displays detailed information for each byte in the file, including:

- **Byte:** The original byte value from the file.
- **Character:** The character represented by the byte (if applicable).
- **Huffman Code:** The variable-length Huffman code assigned to the byte.
- **Length:** The length of the Huffman code.
- **Frequency:** The frequency of occurrence of the byte in the file.

![3](https://github.com/user-attachments/assets/cf8d4426-d8b2-4178-95e0-bdbe930300ce)

## 📉 Compression Statistics

The tool also provides detailed statistics comparing the original and compressed file sizes:

- **Original File Size**
- **Compressed File Size**
- **Compression Ratio**

![4](https://github.com/user-attachments/assets/d8a8a95c-0e0d-473d-86bb-cb223a93c066)

## 📄 File Header Information

Each compressed file includes a header with metadata such as the original file name, file size, and the Huffman codes for each byte.
This header is critical for decoding the compressed file during decompression.

## ⚙️ Project Features

- **File Compression:** Users can select any file to compress, and the tool outputs a `.huf` compressed file.
- **File Decompression:** Users can decompress `.huf` files to restore the original file.
- **Huffman Table Display:** A detailed table shows the Huffman codes generated during the compression process.
- **Statistics Display:** The tool shows compression statistics, including the original and compressed file sizes and the compression ratio.


