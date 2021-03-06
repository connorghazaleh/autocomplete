import java.util.*;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 * @author Jeff Forbes
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie
	 * rooted at myRoot, as well as add all nodes necessary to represent the
	 * words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws NullPointerException
	 *             if either argument is null
	 * @throws IllegalArgumentException
	 *             if terms and weights are different weight
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		//checks that inputs are valid
		if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}

		if (terms.length != weights.length) {
			throw new IllegalArgumentException("terms and weights are not the same length");
		}
		
		// Represent the root as a dummy/placeholder node
		myRoot = new Node('-', null, 0);
		
		//creates trie
		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any
	 * necessary intermediate nodes if they do not exist. Update the
	 * subtreeMaxWeight of all nodes in the path from root to the node
	 * representing word. Set the value of myWord, myWeight, isWord, and
	 * mySubtreeMaxWeight of the node corresponding to the added word to the
	 * correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 */
	private void add(String word, double weight) {
		//checks for valid inputs
		if(word==null) {
			throw new NullPointerException();
		}
		if(weight<0) {
			throw new IllegalArgumentException();
		}
		Node current = myRoot;
		// add String str to internal trie
		for(int k=0; k < word.length(); k++){
			char ch = word.charAt(k);
			//adds word character by character into the trie
			if (current.children.get(ch) == null) {
				current.children.put(ch,new Node(ch,current,weight));
				if (weight > current.mySubtreeMaxWeight) {
					current.mySubtreeMaxWeight = weight;
				}
				current = current.children.get(ch);
			
			}else {
				if (weight>current.mySubtreeMaxWeight) {
					current.mySubtreeMaxWeight = weight;
				}
				current = current.children.get(ch);
			}	
		}
		
		
		// current now points to a node representing String str
		current.myWord = word;
		current.myWeight = weight;
		current.isWord = true;
		current.mySubtreeMaxWeight=weight;


		

	}

	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in the trie with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An Iterable of the k words with the largest weights among all
	 *         words starting with prefix, in descending weight order. If less
	 *         than k such words exist, return all those words. If no such words
	 *         exist, return an empty Iterable
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public Iterable<String> topMatches(String prefix, int k) {
		//base case
		if (k == 0) {
			return new LinkedList<>();
		}
		//initialize variables
		Comparator<Node> compN = new Node.ReverseSubtreeMaxWeightComparator();
		Term.WeightOrder compW = new Term.WeightOrder();
		PriorityQueue<Node> npq = new PriorityQueue<>(compN);
		PriorityQueue<Term> tpq = new PriorityQueue<>(k,compW);
		LinkedList<String> last = new LinkedList<String>();
		
		
		
		Node root = myRoot;
		Node current = myRoot;
		
		//find node where prefix ends
		for (int a=0; a<prefix.length(); a++){
			if (current.getChild(prefix.charAt(a)) == null)
				return last;
			else
				current = current.getChild(prefix.charAt(a));
		}
		
		//add all stemming nodes  
     	npq.add(current);
     	while (!npq.isEmpty()) {
          	Node current2 = npq.remove();
          	if(!tpq.isEmpty()&&tpq.size()==k)
				if(current2.mySubtreeMaxWeight<tpq.peek().getWeight()){
					break;
				}
          	if (current2.isWord) {
          		Term g = new Term(current2.getWord(),current2.getWeight());
              	tpq.add(g);
          	}
          	for(Node n : current2.children.values()) {
              	npq.add(n);
          	}
     	}
     	
     	//add everything to the returned linked list
     	int numResults = Math.min(k, tpq.size());
		for (int i = 0; i < numResults; i++) {
			String temp = tpq.remove().getWord();
			last.addFirst(temp);
			
		}
		
	
		return last;
		
	}

	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from with the largest weight starting with prefix, or an
	 *         empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 */
	public String topMatch(String prefix) {
		//locates node at end of prefix
		Node current = myRoot;
		for(int k=0; k < prefix.length(); k++){
			if (current.children.get(prefix.charAt(k))==null) {
				return "";
			}
			current = current.children.get(prefix.charAt(k));
		}
		
		//cycles through trie to find word with highest weight by checking the isWord parameter and weight when isWord is true
		Node root = current;
		while (current.isWord == false) {
			
			Collection<Node> kids = current.children.values();
			for (Node k: kids) {
				if (k.mySubtreeMaxWeight == root.mySubtreeMaxWeight) {
					current = k;
				}
			}
		}


		return current.myWord;
	}

	/**
	 * Return the weight of a given term. If term is not in the dictionary,
	 * return 0.0
	 */
	public double weightOf(String term) {
		//returns weight of term
		//locate term
		Node current = myRoot;
		for(int k=0; k < term.length(); k++){
			if (current.children.get(term.charAt(k))==null) {
				return 0;
			}
			current = current.children.get(term.charAt(k));
		}
		//return its weight
		return current.getWeight();
		// term is not in dictionary return 0
		
	}
}
