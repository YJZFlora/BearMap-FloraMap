package bearmaps.proj2ab;


import bearmaps.proj2ab.ExtrinsicMinPQ;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    /* Adds an item with the given priority value. Throws an
     * IllegalArgumentExceptionb if item is already present.
     * You may assume that item is never null. */
    private class Node implements Comparable<Node>{
        private T item;
        private double priority;
        private int position;

        // constructor
        Node(T item, double priority, int position) {
            this.item = item;
            this.priority = priority;
            this.position = position;
        }

        T getItem() {
            return item;
        }

        int getPosition() {
            return position;
        }

        double getPriority() {
            return priority;
        }

        void setPriority(double newP) {
            priority = newP;
        }

        @Override
        public int compareTo(Node other) {
            if (other == null) {
                return -1;
            }
            return Double.compare(this.getPriority(), other.getPriority());
        }

        @Override
        public boolean equals(Object o) {
            if (this.getClass() != o.getClass()) {
                return false;
            } else {
                return ((Node) o).getItem().equals(this.getItem());
            }
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }
    }

    private Node[] pq;       // store Nodes
    private int n;          // number of items
    private HashMap<T, Node> hm = new HashMap<>();

    // constructor
    public ArrayHeapMinPQ(int initCapicity) {
        pq = new ArrayHeapMinPQ.Node[initCapicity + 1];
        n  = 0;
    }

    public ArrayHeapMinPQ() {
        pq = new ArrayHeapMinPQ.Node[10];
        n = 0;
    }

    // If the item already exists, throw an IllegalArgumentException.
    // You may assume that item is never null.
    @Override
    public void add(T item, double priority) {

        // check if exists o(log N)
        if (contains(item)) {
            throw new IllegalArgumentException("this item already exists.");
        }

        Node toAdd = new Node(item, priority, n + 1);
        hm.put(item, toAdd);

        // double size of array if necessary
        if (n == pq.length - 1) {
            resize(pq.length * 2);
        }

        // put to last bucket of pq
        n ++;
        pq[n] = toAdd;

        // swim up until in right place
        if (n != 1){
            swimUp(n);
        }
    }

    private void swimUp(int curPosition) {
        // get parent, and compare to parent
        int parentPosition = curPosition / 2;
        // if smaller than parent, swap
        if (pq[curPosition].compareTo(pq[parentPosition]) < 0) {
            swap(parentPosition, curPosition);

            if (parentPosition != 1) {
                swimUp(parentPosition);
            }
        }
    }

    private void swap(int po1, int po2) {
        Node temp = pq[po1];
        pq[po1] = pq[po2];
        pq[po2] = temp;
        pq[po1].position = po1;
        pq[po2].position = po2;
    }


    private void resize(int capicity) {
        Node[] temp = new ArrayHeapMinPQ.Node[capicity];
        for (int i = 1; i < n + 1; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    @Override
    /* Returns true if the PQ contains the given item. */
    public boolean contains(T item) {
        return hm.containsKey(item);
    }

    @Override
    /* Returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T getSmallest() {
        if (n == 0) {
            throw new NoSuchElementException();
        }
        return pq[1].item;
    }


    @Override
    /* Removes and returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T removeSmallest() {
        if (n == 0) {
            throw new NoSuchElementException();
        }
        T returnItem = pq[1].item;
        swap(1, n);
        pq[n] = null;
        n--;
        sink(1);
        hm.remove(returnItem);

        // resize if 3/4 empty
        if ( (double) n / (double)pq.length < 0.25) {
            resize(pq.length / 2);
        }

        return returnItem;
    }
    /* Returns the number of items in the PQ. */

    private void sink(int curPosition) {
        int child = curPosition * 2;
        // sink to the side with smaller child
        if (child < n && pq[child].compareTo(pq[child + 1]) > 0) {
            child ++;
        }
        if (child <= n && pq[child].compareTo(pq[curPosition]) < 0) {
            swap(child, curPosition);
            sink(child);
        }
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    /* Changes the priority of the given item. Throws NoSuchElementException if the item
     * doesn't exist. */
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        int nodePosition = hm.get(item).getPosition();
        double oldPriority = pq[nodePosition].getPriority();
        pq[nodePosition].setPriority(priority);
        if (oldPriority == priority) {
            return;
        }
        if (priority > oldPriority) {
            sink(nodePosition);
        } else {
            swimUp(nodePosition);
        }
    }
}
