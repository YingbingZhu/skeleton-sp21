package deque;

public class LinkedListDeque<T> implements Deque<T> {
    private class Node{
        private T item;
        private Node prev;
        private Node next;

        public Node(T i, Node p, Node n){
            this.prev = p;
            this.next = n;
            this.item = i;
        }

    }
    private Node sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    /*create a deep copy of linkedlistdeque */
    public LinkedListDeque(LinkedListDeque other){
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
        Node p = other.sentinel.next;
        for (int i = 0; i < other.size; i++){
            addLast(p.item);
            p = p.next;
        }
    }


    /*add node between sentinel and original first*/
    @Override
    public void addFirst(T item){
        Node firstNode =  new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = firstNode;
        sentinel.next = firstNode;
        size ++;
    };

    @Override
    public void addLast(T item){
        Node lastNode =  new Node(item, sentinel.prev, sentinel);
        sentinel.prev = lastNode;
        sentinel.prev.next = lastNode;
        size ++;
    };

    public int size(){
        return this.size;
    };
    public void printDeque(){
        for (Node i = sentinel.next; i!=sentinel; i = i.next){
            if (i.next==sentinel) {
                 System.out.println(i.item);
                 break;
            }
            System.out.println(i.item+",");
        }

    };
    
    public T removeFirst(){
        if (sentinel.next==sentinel){
            return null;
        }
        T removeItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return removeItem;
    };

    public T removeLast(){
        if (sentinel.next==sentinel){
            return null;
        }
        T removeItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return removeItem;

    };
    
    public T get(int index){
        if (index>this.size){
            return null;
        }
        Node node = sentinel.next;
        for (int i= 0; i<index; i++){
             node = node.next;
        }
        return node.item;
    };

    public T getRecursive(int index){
        return getStartPointer(index, sentinel.next);
    }

    private T getStartPointer(int index, Node p){
        if (index == 0){
            return p.item;
        }
        return getStartPointer(index-1, p.next);

    }

}
