package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node{
        private T item;
        private Node prev;
        private Node next;

        Node () {
            item = null;
            prev = next = null;
        }

        Node(T i, Node p, Node n){
            prev = p;
            next = n;
            item = i;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new Node();
        sentinel.prev = sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public boolean isEmpty(){
        return size == 0;
    }

    /*add node between sentinel and original first*/
    @Override
    public void addFirst(T item){
        Node firstNode =  new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = firstNode;
        sentinel.next = firstNode;
        size += 1;
    };


    /*be careful about the null pointers*/
    @Override
    public void addLast(T item){
        Node lastNode =  new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = lastNode;
        sentinel.prev = lastNode;
        size +=1;
    };

    @Override
    public int size(){
        return size;
    };

    @Override
    public void printDeque(){
        Node p = sentinel.next;
        while (p.next!=sentinel){
            System.out.println(p.item+",");
            p=p.next;
        }
        System.out.print(p.item);
        System.out.println();
    }

    @Override
    public T removeFirst(){
        if (size==0){
            return null;
        }
        Node tmp = sentinel.next;
        sentinel.next = tmp.next;
        tmp.next.prev = sentinel;
        size-=1;
        return tmp.item;
    };

    @Override
    public T removeLast(){
        if (size==0){
            return null;
        }
        Node tmp = sentinel.prev;
        sentinel.prev = tmp.prev;
        tmp.prev.next = sentinel;
        size-=1;
        return tmp.item;
    };

    @Override
    public T get(int index){
        if (index>=size){
            return null;
        }
        Node node = sentinel.next;
        while (index!=0) {
             node = node.next;
             index-=1;
        }
        return node.item;
    };

    public T getRecursive(int index){
        return getStartPointer(index, sentinel.next);
    }

    private T getStartPointer(int index, Node p){
        if (p == sentinel){
            return null;
        }
        if (index == 0){
            return p.item;
        }
        return getStartPointer(index-1, p.next);

    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node iterNode;

        LinkedListDequeIterator() {
            iterNode = sentinel.next;
        }


        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public T next() {
            T returnItem = iterNode.item;
            iterNode = iterNode.next;
            return returnItem;
        }

        @Override
        public boolean hasNext() {
            return iterNode != sentinel;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (this == null){
            return false;
        }
        if (!(o instanceof Deque)){
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (size()!= other.size()){
            return false;
        }
        for (int i=0; i< size(); i++){
            T item1 = get(i);
            T item2 = other.get(i);
            if (!item1.equals(item2)) {
                return false;
            }
        }
        return true;
    }

}
