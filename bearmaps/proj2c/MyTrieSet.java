package bearmaps.proj2c;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyTrieSet implements TrieSet61B {
    private Node root;

    // Node class
    private class Node {
        private boolean isKey;
        private Character k;
        private HashMap<Character, Node> children;

        Node(Character k, boolean isKey) {
            this.k = k;
            this.isKey = isKey;
            this.children = new HashMap<Character, Node>();
        }
    }

    // constructor
    MyTrieSet() {
        root = new Node(null, false);
    }

    @Override
    /** Clears all items out of Trie */
    public void clear() {
        root = new Node(null, false);
    }

    @Override
    /** Returns true if the Trie contains KEY, false otherwise */
    public boolean contains(String key) {
        if (key == null || key.length() < 1) {
            return false;
        }
        Node cur = root;
        for (int i = 0; i < key.length(); i++) {
            Character c = key.charAt(i);
            if (!cur.children.containsKey(c)) {
                return false;
            }
            cur = cur.children.get(c);
        }
        return cur.isKey;
    }

    @Override
    // Inserts string KEY into Trie
    public void add(String key) {
        if (key == null || key.length() < 1) {
            return;
        }
        Node cur = root;
        for (int i = 0; i < key.length(); i++) {

            Node n = new Node(key.charAt(i), false);

            if (!cur.children.containsKey(key.charAt(i))) {
                cur.children.put(key.charAt(i), n);
            }

            cur = cur.children.get(key.charAt(i));

        }
        cur.isKey = true;
    }

    @Override
    /** Returns a list of all words that start with PREFIX */
    public List<String> keysWithPrefix(String prefix) {  // prefix eg: "sa"
        if (prefix == null || prefix.length() < 1) {
            throw new IllegalArgumentException();
        }
        Node cur = root;
        for (int i = 0; i < prefix.length(); i++) {
            Character c = prefix.charAt(i);
            if (!cur.children.containsKey(c)) {
                throw new IllegalArgumentException("there is no word start with this prefix");
            }
            cur = cur.children.get(c);   // last char of prefix. ie, "a"
        }

        List returnList = new LinkedList<>();  // the return list

        for (HashMap.Entry<Character, Node> entry : cur.children.entrySet()) {
            String a = Character.toString(entry.getValue().k);
            helper(prefix +  a, returnList, entry.getValue());
        }
        return returnList;
    }

    private void helper(String s, List<String> returnList, Node n) {
        if (n.isKey) {
            returnList.add(s);
        }
        Node cur = n;

        for (HashMap.Entry<Character, Node> entry : cur.children.entrySet()) {
            String a = Character.toString(entry.getValue().k);
            helper(s +  a, returnList, entry.getValue());
        }
    }

    @Override
    /** Returns the longest prefix of KEY that exists in the Trie
     * Not required for Lab 9. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public String longestPrefixOf(String key) {
        throw new UnsupportedOperationException();
    }
}
