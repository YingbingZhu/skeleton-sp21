package bstmap;


import java.util.*;
import java.util.function.Consumer;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K,V>{

    private Node root;   // root of BST
    private int size = 0;

    private class Node {
        private K key;
        private V value;
        private Node left, right;
        private int size;

        public Node(K key, V value, int size){
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    public BSTMap() {

    }

    /** Removes all of the mappings from this map. */
    public void clear(){
        root = null;
        size = 0;
    };

    public Node find(Node node, K key) {
        if (node==null||node.key==null){
            return null;
        }
        if (key==null){
            throw new IllegalArgumentException("calls get() with a null key");
        }
        int cmp = key.compareTo(node.key);
        if (cmp>0){
            return find(node.right, key);
        } else if (cmp<0){
            return find(node.left, key);
        } else {
            return node;
        }
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        if (root==null||root.key==null) return false;
        Node node = find(root, key);
        return node!=null;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key){
        return get(root, key);
    };

    public V get(Node node, K key) {
        if (node==null){
            return null;
        }
        if (key==null){
            throw new IllegalArgumentException("calls get() with a null key");
        }
        int cmp = key.compareTo(node.key);
        if (cmp>0){
            return get(node.right, key);
        } else if (cmp<0){
            return get(node.left, key);
        } else {
            return node.value;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return size(root);
    }

    public int size(Node node){
        if (node==null){
            return 0;
        } else {
        return node.size;
        }
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        if (key == null) throw new IllegalArgumentException("calls put() with a null key");
        root = put(root, key, value);
    };

    private Node put(Node node, K key, V value){
        if (node==null) {
            return new Node(key, value, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp>0){
            node.right = put(node.right, key, value);
        } else if (cmp<0){
            node.left = put(node.left, key, value);
        } else {
            node.value = value;
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    };

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        inOrderTraverse(root, keySet);
        return keySet;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key){
        if (key==null) throw new IllegalArgumentException();
        V deleteValue = get(key);
        root = remove(root, key);
        return deleteValue;
    };

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value){
        if (key==null) throw new IllegalArgumentException();
        V deleteValue = get(key);
        root = remove(root, key);
        return deleteValue;
    }

    public Node remove(Node node, K key){
        if (node==null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp<0) node.left = remove(node.left, key);
        else if (cmp>0) node.right = remove(node.right, key);
        else {
            /*node with 0/1 child*/
            if (node.left==null) return node.right;
            if (node.right==null) return node.left;
            /* node with 2 children*/
            Node successor = min(node.right);
            remove(node, successor.key);
            successor.right = node.right;
            successor.left = node.left;
            node = successor;
        }
        node.size = size(node.left) + size(node.right) +1;
        return node;
    }

    /* find min key*/
    public Node min(Node node) {
        if (node.left == null) return node;
        else {
            return min(node.left);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void inOrderTraverse(Node node, Set<K> set) {
        if (node == null) return;
        inOrderTraverse(node.left, set);
        set.add(node.key);
        inOrderTraverse(node.right, set);
    }


}
