import java.util.*;

/**
 * 
 * Using a sorted array of Term objects, this implementation uses binary search
 * to find the top term(s).
 * 
 * @author Austin Lu, adapted from Kevin Wayne
 * @author Jeff Forbes
 */
public class BinarySearchAutocomplete implements Autocompletor {

	Term[] myTerms;

	/**
	 * Given arrays of words and weights, initialize myTerms to a corresponding
	 * array of Terms sorted lexicographically.
	 * 
	 * This constructor is written for you, but you may make modifications to
	 * it.
	 * 
	 * @param terms
	 *            - A list of words to form terms from
	 * @param weights
	 *            - A corresponding list of weights, such that terms[i] has
	 *            weight[i].
	 * @return a BinarySearchAutocomplete whose myTerms object has myTerms[i] =
	 *         a Term with word terms[i] and weight weights[i].
	 * @throws a
	 *             NullPointerException if either argument passed in is null
	 */
	public BinarySearchAutocomplete(String[] terms, double[] weights) {
		//checks to see if inputs are valid
		if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}
		
		//checks to see if weights are valid
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] < 0){
				throw new IllegalArgumentException("One or more arguments negative");
			}
		}
		
		//sets instance variables and creates terms
		myTerms = new Term[terms.length];
		
		for (int i = 0; i < terms.length; i++) {
			myTerms[i] = new Term(terms[i], weights[i]);
		}
		
		Arrays.sort(myTerms);
	}

	/**
	 * Uses binary search to find the index of the first Term in the passed in
	 * array which is considered equivalent by a comparator to the given key.
	 * This method should not call comparator.compare() more than 1+log n times,
	 * where n is the size of a.
	 * 
	 * @param a
	 *            - The array of Terms being searched
	 * @param key
	 *            - The key being searched for.
	 * @param comparator
	 *            - A comparator, used to determine equivalency between the
	 *            values in a and the key.
	 * @return The first index i for which comparator considers a[i] and key as
	 *         being equal. If no such index exists, return -1 instead.
	 */
	public static int firstIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		
	
		//sets low and high boundaries
		int low = 0;
		int high = a.length-1;

		//while loop iterates through array indices changing boundaries
	    while (low <= high) {
	        int mid = (low + high)/2;
	        Term midval = a[mid];
	        int cmp = comparator.compare(midval,key);
	       
	       //checks relationship between key and middle value of range and adjusts boundaries accordingly for next iteration 
	        if (cmp < 0)
	            low = mid + 1;
	        else if (cmp > 0)
	            high = mid - 1;
	        else {
	        	//return correct index if in correct place, otherwise reiterates through loop
	        		if ((mid>0)&&(comparator.compare(a[mid-1],a[mid]))==0) {
	        			high = mid - 1;
	        		}else
	        			return mid; //key found
	        }
	        
	            
	     }
		
	    
	    return -1;  // key not found


		

	}

	/**
	 * The same as firstIndexOf, but instead finding the index of the last Term.
	 * 
	 * @param a
	 *            - The array of Terms being searched
	 * @param key
	 *            - The key being searched for.
	 * @param comparator
	 *            - A comparator, used to determine equivalency between the
	 *            values in a and the key.
	 * @return The last index i for which comparator considers a[i] and key as
	 *         being equal. If no such index exists, return -1 instead.
	 */
	public static int lastIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		
		//sets low and high boundaries
		int low = 0;
		int high = a.length-1;

		//while loop iterates through array indices changing boundaries
	    while (low <= high) {
	        int mid = (low + high)/2;
	        Term midval = a[mid];
	        int cmp = comparator.compare(midval,key);
	       
	      //checks relationship between key and middle value of range and adjusts boundaries accordingly for next iteration 
	        if (cmp < 0)
	            low = mid + 1;
	        else if (cmp > 0)
	            high = mid - 1;
	        else {
	        	//return correct index if in correct place, otherwise reiterates through loop
		        	if ((mid<a.length-1)&&(comparator.compare(a[mid+1],a[mid]))==0) {
	        			low = mid + 1;
	        		}else
	        			return mid; //key found
	        			
	        }
	        
	            
	     }
	     return -1;  // key not found
	     
	     
	}

	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in myTerms with the largest weight which match the given prefix,
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
	 * @return An array of the k words with the largest weights among all words
	 *         starting with prefix, in descending weight order. If less than k
	 *         such words exist, return an array containing all those words If
	 *         no such words exist, reutrn an empty array
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public Iterable<String> topMatches(String prefix, int k) {
		//checks that inputs are valid
		if (k < 0) {
			throw new IllegalArgumentException("Illegal value of k:"+k);
		}
		if (k==0) {
			return new LinkedList<>();
		}
		
		// maintain pq of size k
		PriorityQueue<Term> pq = new PriorityQueue<Term>(k, new Term.WeightOrder());
		//looks through all of terms and adds word to pq when its weight is the appropriate size and stops when it reaches a point where 
		for (Term t : myTerms) {
			if (!t.getWord().startsWith(prefix))
				continue;
			if (pq.size() < k) {
				pq.add(t);
			} else if (pq.peek().getWeight() < t.getWeight()) {
				pq.remove();
				pq.add(t);
			}
		}
		
		//puts all terms from pq into a linked list to be returned
		int numResults = Math.min(k, pq.size());
		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < numResults; i++) {
			ret.addFirst(pq.remove().getWord());
		}
		
		return ret;
	}

	/**
	 * Given a prefix, returns the largest-weight word in myTerms starting with
	 * that prefix. e.g. for {air:3, bat:2, bell:4, boy:1}, topMatch("b") would
	 * return "bell". If no such word exists, return an empty String.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from myTerms with the largest weight starting with
	 *         prefix, or an empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 * 
	 */
	public String topMatch(String prefix) {
		//returns match with highest weight
		String maxTerm = "";
		double maxWeight = -1;
		for (Term t : myTerms) {
			if (t.getWeight() > maxWeight && t.getWord().startsWith(prefix)) {
				maxWeight = t.getWeight();
				maxTerm = t.getWord();
			}
		}
		return maxTerm;
	}

	/**
	 * Return the weight of a given term. If term is not in the dictionary,
	 * return 0.0
	 */
	public double weightOf(String term) {
		//returns weight of term
		for (Term t : myTerms) {
			if (t.getWord().equalsIgnoreCase(term))
				return t.getWeight();
		}
		// term is not in dictionary return 0
		return 0;
	}
}
