package Huffman;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Assignment 1
 * Submitted by:
 * Student 1. 	ID# 206109621 Eyal Delarea
 * Student 2. 	ID# 203000005 Yohai Mizrahi
 * Student 3.   ID# 311272173 Gabriel Noghryan
 */

/**
 * This class will preform hoffman codding and decoding in 3 steps:
 * 1.Preform analysis of the data of the file and keep amount of each data junk.
 * 2.Create a mini-heap,and insert all of the data into the heap.
 * 3.Build a hoffman-tree from the mini heap.
 * LAST STEP:
 * 4.Output the compressed file and the keymap of the values.
 */
public class HuffmanEncoderDecoder {

    static final int NOT_FOUND = -1;
    static int bitSetIndex = 0;
    static String currentFileType = null;

    public void Compress(BitSet LZCompressedBitSet, String outputPath) {

        byte[] fileContent = LZCompressedBitSet.toByteArray();


        ArrayList<ArrayListObject> inputVariables = new ArrayList<ArrayListObject>();

        /**
         * First step - mapping the chars of the files.
         * building array list of different values of ascii chars,
         * and keep counting values for each char.
         *
         */
        //read from file and remove char
        Byte temp1 = 0;
        //loop until file ends == NOT_FOUND

        for (int i = 0; i < fileContent.length; i++) {
            temp1 = fileContent[i];
            int ans = find(inputVariables, temp1);
            if (ans == NOT_FOUND) {
                inputVariables.add(new ArrayListObject(temp1));

            } else {
                inputVariables.get(ans).incAmount();
            }
        }

        MinHeap heap = new MinHeap(inputVariables.size());
        //insert all elements
        for (ArrayListObject inputVariable : inputVariables) {
            heap.insert(inputVariable);
        }

        //Generate Huffman map
        HashMap<Byte, mapObject> map = heap.generateHoffmanMap();

        //Crate BitSet object

        BitSet bitset = new BitSet();
        BitSet tempByte ;

        //write Coding to Bitset to compressed file

        for (int i = 0; i < fileContent.length; i++) {

            mapObject tempObject = map.get(fileContent[i]);
            int length = tempObject.getLength();
            tempByte = tempObject.getHoffmanCode();

            for (int j = 0; j < length; j++) {
                bitset.set(bitSetIndex++, tempByte.get(j));
            }

        }


        try {
            File compressedFile = new File(outputPath);
            if (compressedFile.createNewFile()) {
                System.out.println("File created: " + compressedFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream fileOut =
                    new FileOutputStream(outputPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(bitset);

            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + outputPath);
        } catch (IOException i) {
            i.printStackTrace();
        }


    }


    public ArrayList<Byte> decompress(String gzipCompressedFile, String keyPath) {

        BitSet set = null;
        ArrayListObject key = null;

        try {
            FileInputStream fileIn = new FileInputStream(gzipCompressedFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            set = (BitSet) in.readObject();
            fileIn = new FileInputStream(keyPath);
            in = new ObjectInputStream(fileIn);
            key = (ArrayListObject) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
        assert set != null;
        assert key != null;

        //wrtire to char array from bitset

        ArrayList<Byte> deCompressedArray = decompressCharArray(key, set);


        //clear after,delete key
        try {
            File f = new File(keyPath);           //file to be delete
            if (f.delete())                      //returns Boolean value
            {
                System.out.println(f.getName() + " deleted");   //getting and printing the file name
            } else {
                System.out.println("failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deCompressedArray;
    }


    public ArrayList<Byte> decompressCharArray(ArrayListObject tree, BitSet compressedBits) {

        ArrayList<Byte> result = new ArrayList<>();
        boolean temp;
        ArrayListObject current = tree;

            for (int i = 0; i < compressedBits.length(); i++) {
            while (current.isFather()) {
                temp = compressedBits.get(i); // bool value
                if (temp) {
                    current = current.getRight();
                } else {
                    current = current.getLeft();
                }
                if (current.isFather()) {
                    i++;
                }
            }
            result.add(current.getValue());
            current = tree;
        }  //end for loop i++
        return result;
    }


    /**
     * Check if a char exits in the array list.
     *
     * @param arrayList   given arrayList
     * @param currentChar given char
     * @return index value;
     */
    public static int find(ArrayList<ArrayListObject> arrayList, Byte currentChar) {

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getValue() == currentChar) {
                return i;
            }
        }
        return NOT_FOUND;
    }


    /**
     * read file to char array
     *
     * @param filePath file path
     * @return char array
     * @throws IOException e
     */
    public static char[] ReadFileToCharArray(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        return fileData.toString().toCharArray();
    }

    public String getFileSuffix(String path) {
        char temp;
        for (int i = path.length() - 1; i > 0; i--) {
            temp = path.charAt(i);
            if (temp == '.') {
                return path.substring(i, path.length());
            }
        }
        return null;
    }
}



