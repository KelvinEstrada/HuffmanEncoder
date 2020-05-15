package ciic4020.main;
import java.io.FileReader;

import ciic4020.bst.BTNode;
import ciic4020.bst.utils.BinaryTreePrinter;
import ciic4020.list.List;
import ciic4020.map.Map;
import ciic4020.project3.sortedlist.SortedList;

public class Launch {
	
	
	private static final String INPUT1 = "samples/input1.txt";
	private static final String INPUT2 = "samples/input2.txt";
	private static final String INPUT3 = "inputData/stringData.txt";
	
	public static void main(String[] args) throws Exception {
		
		/*
		 * Read input text file using Java's File Reader.
		 */
		FileReader file = new FileReader(INPUT1);
	/*--------------------------------------------------------------------------*/
		HuffmanCoding h = new HuffmanCoding();
		String inputText = h.loadData(file);
		Map<String, Integer> frequencyMap = h.computeFD(inputText);
		BTNode<Integer, String> rootNode = h.huffmanTree(frequencyMap);
		Map<String, String> huffmanMap = h.huffmanCode(rootNode);
		SortedList<BTNode<Integer, String>> SL = h.sortedCode(frequencyMap);
		String output = h.encode(huffmanMap, inputText);
		h.processResults(SL, huffmanMap, inputText, output);
		
	}
}
