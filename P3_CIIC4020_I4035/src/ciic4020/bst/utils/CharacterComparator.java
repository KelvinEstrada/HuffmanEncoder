package ciic4020.bst.utils;

public class CharacterComparator implements Comparator<Character> {

	@Override
	public int compare(Character v1, Character v2) {
		return v2.compareTo(v1);
	}

}
