package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private final int INIT_CAPACITY = 8;
    private int nextFirst = 0;
    private int nextLast = 0;
    public int size = 0;
    

    /*constructor*/
    public ArrayDeque(){
       items =  (T[]) new Object[INIT_CAPACITY];
       size = 0;
       nextFirst = 0;
       nextLast = 1;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size ==0;
    }

//    public ArrayDeque(ArrayDeque o){
//        items =  (T[]) new Object[o.items.length];
//        size = o.size;
//        nextFirst = o.nextFirst;
//        nextLast = o.nextLast;
//    }

    /* get the last index */
    private int minusOne(int index) {
        return Math.floorMod(index-1, items.length);
    }

    /* get the next index */
    private int plusOne(int index) {
        return Math.floorMod(index+1, items.length);
    }

    private int plusOne(int index, int length) {
        return Math.floorMod(index+1, length);
    }

    public void addFirst(T item){
        resize();
        items[nextFirst] = item;
        size++;
        nextFirst = minusOne(nextFirst);
    }

    public T getFirst(){
        return items[plusOne(nextFirst)];
    }

    public void addLast(T item){
        resize();
        items[nextLast] = item;
        size++;
        nextLast = plusOne(nextLast);
    }


    public void resize() {
        if (size == items.length) {
            expand();
        }
        if (size < (items.length) * 0.25 && items.length > 8){
            reduce();
        }
    }

    public void expand() {
        resizeHelper(items.length*2);
    }

    public void  reduce() {
        resizeHelper(items.length/2);
    }


    private void resizeHelper(int capacity){
        T[] tempArr = items;
        int begin = plusOne(nextFirst);
        int end = minusOne(nextLast);
        items = (T[]) new Object[capacity];
        nextFirst = 0;
        nextLast = 1;
        for (int i = begin; i!=end; i=plusOne(i, tempArr.length)){
            items[nextLast] = tempArr[i];
            nextLast = plusOne(nextLast);
        }
        items[nextLast] =  tempArr[end];
        nextLast = plusOne(nextLast);
    }

    public void printDeque(){
        for (int i = plusOne(nextFirst); i!=nextLast; i = plusOne(i)){
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    public T getLast(){
      return items[minusOne(nextLast)];
    }

    public T removeFirst(){
      if (isEmpty()) {
          return null;
      }
      resize();
      T item = getFirst();
      nextFirst = plusOne(nextFirst);
      items[nextFirst] = null;
      size--;
      return item;
    }

    public T removeLast(){
        if (isEmpty()) {
            return null;
        }
        resize();
        T item = getLast();
        nextLast = minusOne(nextLast);
        items[nextLast] = null;
        size--;
        return item;
    }

    public T get(int index){
        if (index <0 || index>=size || isEmpty()){
            return null;
        }
        index = Math.floorMod(plusOne(nextFirst) + index, items.length);
        return items[index];
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;

        ArrayDequeIterator() {
            pos = plusOne(pos);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public T next() {
            T returnItem = items[pos];
            pos = plusOne(pos);
            return returnItem;
        }

        @Override
        public boolean hasNext() {
            return pos!=nextLast;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            T item1 = get(i);
            T item2 = other.get(i);
            if (!item1.equals(item2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }



}
