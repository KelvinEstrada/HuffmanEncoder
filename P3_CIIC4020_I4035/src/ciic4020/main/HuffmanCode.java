package ciic4020.main;
import java.io.BufferedReader;
import java.io.FileReader;
import ciic4020.bst.BTNode;
import ciic4020.hashtable.HashTableOA;
import ciic4020.hashtable.HashTableSC;
import ciic4020.hashtable.SimpleHashFunction;
import ciic4020.list.List;
import ciic4020.map.Map;
import ciic4020.project2.sortedlist.SortedArrayList;
import ciic4020.project2.sortedlist.SortedList;


/**
 * This class contains the code to create a Huffman Code for a particular input file with a string in it. 
 * The input file should have UTF-8 encoding. The class calculates the frequency distribution of a given string in the input file,
 * creates a Huffman Tree based on the frequency distribution, determines the Huffman Code for the string and proceeds to encode
 * the string with its Huffman Code. Finally, it prints to the console a frequency distribution table, the Huffman Code of each character,
 * the original text/string, the new encoded string, the amount of bytes needed to store the encoded text and the savings percentage after encoding
 * the string. 
 * @author KELVIN O. ESTRADA SOTO
 *
 */
public class HuffmanCode {

	/**
	 * Reads input file with UTF-8 encoding format only. 
	 * @param File	Input file.
	 * @return Input file represented as a string.
	 * @throws Exception 
	 */
	public String loadData(FileReader inputFile) throws Exception {
		BufferedReader reader = new BufferedReader(inputFile);
		String text = "";
		String line = reader.readLine();
		while(line != null) {
			text += line;
			line = reader.readLine();
		}
		return text;
	}


	/** Method computeFD computes the frequency distribution of parameter String an stores it in a Map. 
	 *  @param String	Input text string. 
	 *  @return Map containing the frequency distribution of the parameter string. 
	 */
	public Map<String, Integer> computeFD(String inputText){
		Character[] chars = new Character[inputText.length()];
		for(int i = 0; i < inputText.length(); i++) {
			chars[i] = inputText.charAt(i);
		}
		/*Count frequency distribution of each character.*/
		Map<String, Integer> fd = new HashTableSC<String, Integer>(11, new SimpleHashFunction<String>());
		for(Character c: chars) {
			Integer count = fd.get(c.toString());
			if(count != null) {
				fd.put(c.toString(), count+1);
			}
			else {
				fd.put(c.toString(), 1);
			}
		}
		return fd;
	}

	/**
	 * Sorts a frequency distribution map in non-descending order by their key. Used to retrieve least frequent element in other methods. 
	 * @param fd Input Map of frequency distribution.
	 * @return SortedList A new sorted list containing the frequency distribution sorted in non-descending order. 
	 */
	private SortedList<BTNode<Integer, String>> toSortedList(Map<String, Integer> fd){
		SortedList<BTNode<Integer, String>> sortedlist = new SortedArrayList<BTNode<Integer, String>>(fd.size());
		for(String s: fd.getKeys()) {
			BTNode<Integer, String> node = new BTNode<Integer, String>(fd.get(s), s);
			sortedlist.add(node);
		}
		return sortedlist;
	}

	/**
	 * Method to construct the Huffman Tree given a frequency distribution map with each character of the string to be 
	 * encoded. 
	 * @param fd Frequency distribution map of the characters of the string. 
	 * @return BTNode Root node of the Huffman Tree to be constructed. 
	 */
	public BTNode<Integer, String> huffmanTree(Map<String, Integer> fd){
		int size = fd.size();
		SortedList<BTNode<Integer, String>> SL = toSortedList(fd);
		for(int i = 1; i < size; i++) {
			BTNode<Integer, String> newNode = new BTNode<Integer, String>();
			BTNode<Integer, String> x = minFreqRemove(SL);
			BTNode<Integer, String> y = minFreqRemove(SL);
			newNode.setLeftChild(y);
			newNode.setRightChild(x);
			newNode.setKey(x.getKey() + y.getKey()); // Key will be right frequency + left frequency
			newNode.setValue(y.getValue() + x.getValue());
			SL.add(newNode);
		}
		return minFreqRemove(SL);
	}


	/**
	 * Private method to retrieve least frequent element in the sorted list of frequency elements.
	 * If there are two element with the least same frequency number, we proceed to compare which element's symbol is smaller and
	 * return that element as the smallest frequent element. 
	 * @param sl Sorted list containing frequency the nodes from that Binary Tree.
	 * @return BTNode Node containing the least frequent element and its symbol. 
	 */
	private BTNode<Integer, String> minFreqRemove(SortedList<BTNode<Integer, String>> sl){
		//Create node to return.
		BTNode<Integer, String> result = new BTNode<Integer, String>();
		//Make sure if the are two least frequent elements in the list.
		if(sl.size() >= 3) {
		//Resolve the tie ONLY for the last two elements in the list.
			String p1 = sl.get(0).getValue();
			String p2 = sl.get(1).getValue();
			if(p1.compareTo(p2) < 0) {
				String s1 = sl.get(0).getValue();
				String s2 = sl.get(1).getValue();
				if(s1.compareTo(s2) > 0) {
					result = sl.get(0);
					sl.removeIndex(0);
					return result;
				}
				else {
					result = sl.get(1);
					sl.removeIndex(1);
					return result;
				}
			}
		}
		//If not, return the first element in the list since the least sorts the elements in non-ascending order. 
		result = sl.get(0);
		sl.removeIndex(0);
		return result;
	}

	/**
	 * Method that maps each symbol in the tree to its particular Huffman code. We are considering that elements that are left child
	 * will have a 1 assigned in their Huffman code. Elements that are right child will have a 0 assigned to their Huffman code. 
	 * @param root The root of the Binary Tree.
	 * @return Map A map containing each symbol and its particular Huffman code. 
	 */
	public Map<String, String> huffmanCode(BTNode<Integer, String> root){
		Map<String, String> map = new HashTableSC<String, String>(11, new SimpleHashFunction<String>());
		//Delegate work to huffman code auxiliary method.
		huffmanCodeHelper(root, "", map);
		return map;
	}
	
	/**
	 * Huffman code method helper that recursively assigns a 0 to left child and 1 to right child when traversing the Binary Tree.
	 * @param node String, Map Root of the tree, an empty string to assign 0 or 1 and a Map to map each huffman code to the symbol. 
	 * @param s	String representing a 0 if traversing left or 1 if traversing right down the Huffman Tree.
	 * @param map Map containing every symbol with its huffman code. 
	 */
	private void huffmanCodeHelper(BTNode<Integer, String> node, String s, Map<String, String> map){
		if(node.getLeftChild() != null) {
			//Assign 0 to left child.
			huffmanCodeHelper(node.getLeftChild(), s + "0", map);
		}
		if(node.getRightChild() != null) {
			//Assign 1 to right child.
			huffmanCodeHelper(node.getRightChild(), s + "1", map);
		}
		else {
			map.put(node.getValue(), s);
		}
	}

	/**
	 * Auxiliar method to facilitate output display of the frequency elements in non-ascending order. Method returns the list in non=-descending order but we 
	 * will iterate in reverse to print frequency and elements in non-ascending order. 
	 * @param map Frequency distribution Map.
	 * @return SortedList Sorted list containing the frequency distribution in non-descending order. 
	 */
	public SortedList<BTNode<Integer, String>> sortedCode(Map<String, Integer> map){
		SortedList<BTNode<Integer, String>> sortedCode = new SortedArrayList<BTNode<Integer, String>>(map.size());
		for(String s: map.getKeys()) {
			BTNode<Integer, String> node = new BTNode<Integer, String>(map.get(s), s);
			sortedCode.add(node);
		}
		return sortedCode;
	}
	/**
	 * Method to encode each character inside the input string to its corresponding Huffman code. 
	 * @param codeMap Map containing each symbol and its huffman code.
	 * @param inputString String to be encoded using Huffman Code. 
	 * @return String Returns encoded string result. 
	 */
	public String encode(Map<String, String> codeMap, String inputString) {
		String encodedString = "";
		for(int i = 0; i < inputString.length(); i++) {
			for(String j: codeMap.getKeys()) {
				if(j.equals(inputString.substring(i, i+1))) {
					encodedString += codeMap.get(j);
					break;
				}
			}
		}
		return encodedString;
	}

	/**
	 * Method to process the results after computing the frequency distribution of the characters in the string, constructing the Huffman Tree and
	 * encoding the resulting string. 
	 * @param SL	Sorted list containing the frequency distribution. 
	 * @param huffmanMap	Huffman map containing the mapping of each symbol to its huffman code. 
	 * @param inputString	Input text to be encoded. 
	 * @param outputString	Resulting output string encoded. 
	 */
	public void processResults(SortedList<BTNode<Integer, String>> SL, Map<String, String> huffmanMap, String inputString, String outputString) {
		System.out.print("Symbol\t");
		System.out.print("Frequency\t");
		System.out.println("Code");
		System.out.print("------\t");
		System.out.print("---------\t");
		System.out.println("------");
		List<String> fq = huffmanMap.getKeys();
		for(int i = SL.size()-1; i >= 0; i--) {
			System.out.print(SL.get(i).getValue() + "\t");
			System.out.print(SL.get(i).getKey() + "\t\t");
			for(int j = 0; j < fq.size(); j++) {
				if(fq.get(j).equals(SL.get(i).getValue())) {
					System.out.print(huffmanMap.getValues().get(j));
				}
			}
			System.out.println();
		}

				System.out.println();
				System.out.println("Original string:");
				System.out.println(inputString);
				System.out.println("Encoded string:");
				System.out.println(outputString);
				System.out.println();
				int bytesSize = inputString.getBytes().length;
				int encodedBytes =  0;
				for(int i = 0; i < outputString.length(); i++) {
					if(i % 8 == 0) {
						encodedBytes++;
					}
				}
				System.out.println("The original string requires " + bytesSize + " bytes.");
				System.out.println("The encoded string requires " + encodedBytes + " bytes.");
				double numerator = Math.abs(bytesSize - encodedBytes);
				double differencePercent = ((numerator)/(bytesSize)) * 100;
				System.out.printf("Difference in space required is %.2f", differencePercent);
				System.out.println("%");

	}

}
