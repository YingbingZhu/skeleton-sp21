package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private double loadFactor = 0.75;
    private int capacity = 16;   
    private int m;     // number of buckets
    private int n;     // number of elements in the map

    /** Constructors */
    /**
     * Initialize an empty symbol table   
     * */
    public MyHashMap() {
        m = capacity;
        buckets = createTable(capacity);
    }

    public MyHashMap(int initialSize) {
        m = initialSize;
        capacity = initialSize;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        loadFactor = maxLoad;
        capacity = initialSize;
        m = initialSize;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */

    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] hashtable = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++){
            hashtable[i] = createBucket();
        }
        return hashtable;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = createTable(capacity);
        n = 0;
        m = capacity;
    }

    public int hash(K key) {
        int hash = key.hashCode();
        hash = Math.floorMod(hash, m);
        return hash;
    }

    @Override
    public boolean containsKey(K key) {
        if (key==null) throw new IllegalArgumentException("argument to get() is null");
        return get(key)!=null;
    }

    @Override
    public V get(K key) {
        if (key==null) throw new IllegalArgumentException("argument to get() is null");
        int i = hash(key);
//        Collection<Node> targetBucket = buckets[i];
        Node node = find(buckets[i], key);
        if (node!=null) return node.value;
        else return null;
    }


    /*find node*/
    public Node find(Collection<Node> bucket, K key){
        for (Node node:bucket){
            if (node.key.equals(key)){
                return node;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public void put(K key, V value) {
        if (n >= loadFactor*m) resize(2*m);
        int hashcode = hash(key);
        Collection<Node> targetBucket = buckets[hashcode];
        Node targetNode = find(targetBucket, key);
        if (targetNode!=null){
            targetBucket.remove(targetNode);
        }
        else n+=1;
        Node node = createNode(key, value);
        targetBucket.add(node);
    }


    /*resize the hash table, rehash all the keys*/
    public void resize(int size){
        Collection<Node>[] temp = createTable(size);
        for (int i = 0; i < m; i++){
            for (Node node: buckets[i]){
               int newIndex = hash(node.key);
               temp[newIndex].add(node);
            }
        }
        m = size;
        buckets = temp;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (int i = 0; i < m; i++){
            Collection<Node> currBucket = buckets[i];
            if (currBucket.size()==0) continue;
            for (Node node:currBucket){
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        int i = hash(key);
        Collection<Node> currBucket = buckets[i];
        Node targetNode = find(currBucket, key);
        if (targetNode==null){
            return null;
        }
        currBucket.remove(targetNode);
        n-=1;
        return targetNode.value;
    }

    @Override
    public V remove(K key, V value) {
        int i = hash(key);
        Collection<Node> currBucket = buckets[i];
        Node targetNode = find(currBucket, key);
        if (targetNode == null || targetNode.value.equals(value)){
             return null;
        }
        currBucket.remove(targetNode);
        n-=1;
        return targetNode.value;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }


}
