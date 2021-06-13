import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TST<Value> {
    public Node<Value> root;

    public static class Node<Value> {
        public char c;
        public Node<Value> left, mid, right;
        public Value val;
    }

    // Inserts the key value pair into ternary search tree
    public void put(String key, Value val) {
        /* Code here */

        // check if parameter is null
        if (key == null || key.length() == 0) return;


        root = putHelper(root, key, val, 0);
    }

    private Node<Value> putHelper(Node<Value> currentNode, String key, Value val, int index) {
        char currentCharacter = key.charAt(index); // get each character of key
        if (currentNode == null) {
            // if current root is null, initialize the new node and set it as the root of the tree
            currentNode = new Node<Value>();
            currentNode.c = currentCharacter;
        }
        if (currentCharacter > currentNode.c) {
            // if current character is after the current node
            currentNode.right = putHelper(currentNode.right, key, val, index);
        } else if (currentCharacter < currentNode.c) {
            // if current character is before the current node
            currentNode.left = putHelper(currentNode.left, key, val, index);
            // 5 8 - 1
        } else if (  index + 1 < key.length()) {
            // increment the d and set the current character as the middle of the current node
            currentNode.mid = putHelper(currentNode.mid, key, val, index + 1);
        } else {
            currentNode.val = val;
        }

        return currentNode;
    }


    // Returns a list of values using the given prefix
    public List<Value> valuesWithPrefix(String prefix) {
        /* Code here */
        ArrayList<Value> output = new ArrayList<>();

        if (this.root == null) {
            return output;
        }
        if (prefix == null) {
            return output;
        }


        // get subtree first then get all the children of subtree
        Node<Value> rootOfSubTree = getSubTree(root,prefix,0);
        if (rootOfSubTree == null)
            return output;
        if (rootOfSubTree.val != null)
            output.add(rootOfSubTree.val);


        getAllChildrenOfSubTree(rootOfSubTree.mid,output,prefix);
        return output;
    }

    private Node<Value> getSubTree(Node<Value> currentNode,String word, int d) {
        if (currentNode == null) {
            return null;
        }

        char currentCharacter = word.charAt(d);
        // make search while finding the node with the value of word
        if (currentCharacter < currentNode.c)  {
            return getSubTree(currentNode.left,  word, d);
        }
        else if (currentCharacter > currentNode.c) {
            return getSubTree(currentNode.right, word, d);
        }

        else if (d + 1 < word.length()) {
            return getSubTree(currentNode.mid,   word, d+1);
        }

        else { // if we are at the end of the word, return the current node
            return currentNode;
        }
    }

    private void getAllChildrenOfSubTree(Node<Value> currentNode, List<Value> output, String prefix) {
        if (currentNode == null) {
            return;
        }

        getAllChildrenOfSubTree(currentNode.left,  output, prefix);
        // if current value is not null, it means that there is a vertex in this node. add this to the output
        if (currentNode.val != null) {
            output.add(currentNode.val);
        }

        getAllChildrenOfSubTree(currentNode.mid,   output, prefix + currentNode.c);
        // backtracking so remove the last character
        if (prefix.length() > 0) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        getAllChildrenOfSubTree(currentNode.right, output, prefix);
    }


//    private void valuesWithPrefixHelper(Node<Value> currentNode, ArrayList<Value> output,String prefix, int index) {
//        if (currentNode != null) {
//            valuesWithPrefixHelper(currentNode.left,output,prefix,index);
//
//            if (currentNode.val != null && currentNode.val.toString().toLowerCase(Locale.ENGLISH).startsWith(prefix) ) {
//
//                output.add(currentNode.val);
//            }
//
//            valuesWithPrefixHelper(currentNode.mid,output,prefix,index+1);
//
//            valuesWithPrefixHelper(currentNode.right,output,prefix,index);
//        }
//    }
}