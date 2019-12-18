package bearmaps.proj2ab;

import org.junit.Test;

import java.util.List;

/* KDTree class should be immutable
*  only has to work for the 2-dimensional case, i.e. when our points have only x and y coordinates.*/

public class KDTree implements PointSet{

    private class Node {
        private Point p;
        private Node left; // also "down"
        private Node right; // also "up"
        boolean cmpX;

        Node(Point p, Node left, Node right, Boolean cmpX) {
            this.p = p;
            this.left = left;
            this.right = right;
            this.cmpX = cmpX;
        }
    }
    private Node root;

    // You can assume points has at least size 1.
    public KDTree(List<Point> points) {
        for (Point point : points) {
            put(point);
        }
    }

    private void put(Point p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        root = put(root, p, true);
    }

    private Node put(Node parent, Point toputPoint, boolean cmpX) {
        if (parent == null) {
            return new Node(toputPoint, null, null, cmpX);
        }

        double cmpResult = compare(toputPoint, parent);

        if (cmpResult < 0.0) {  // topuPoint < parent
            parent.left = put(parent.left, toputPoint, !parent.cmpX);

        } else if (cmpResult >= 0.0) {
            parent.right = put(parent.right, toputPoint, !parent.cmpX);
        } else {
            parent.p = toputPoint;
        }
        return parent;
    }

    private double compare(Point cmp, Node parent) {
        if (parent.cmpX == true) {
            return cmp.getX() - parent.p.getX();
        } else {
            return cmp.getY() - parent.p.getY();
        }
    }

    @Override
    // Returns the closest point to the inputted coordinates.
    // This should take O(logN) time on average, where N is the number of points.
    public Point nearest(double x, double y) {
        Point goal = new Point(x, y);
        Node bestNode = nearest(root, goal, root);
        return bestNode.p;
    }

    private Node nearest(Node n, Point goal, Node best) {
        if (n == null) {
            return best;
        }
        if (Point.distance(n.p, goal) < Point.distance(best.p, goal)) {
            best = n;
        }
        Node goodSide;
        Node badSide;
        if (compare(goal, n) < 0) { // according to n's comparator
            goodSide = n.left;
            badSide = n.right;
        } else {
            goodSide = n.right;
            badSide = n.left;
        }

        best = nearest(goodSide, goal, best);
        if (badSideUseful(n, goal, best)) {
            best = nearest(badSide, goal, best);
        }
        return best;
    }

    private boolean badSideUseful(Node n, Point goal, Node best) {
        double bestDistance = Point.distance(goal, best.p);
        double cmpD = Math.pow(compare(goal, n), 2);
        if (bestDistance - cmpD >= 0) {
            return true;
        } else {
            return false;
        }
    }


    public static void main(String[] args) {
        Point A = new Point(2, 3); // constructs a Point with x = 1.1, y = 2.2
        Point B = new Point(4, 2);
        Point C = new Point(4, 5);
        Point D = new Point(3, 3);
        Point E = new Point(1, 5);
        Point F = new Point(4, 4);

        KDTree kd = new KDTree(List.of(A, B, C, D, E, F));
        Point M = new Point(14, 14);

        Point ret = kd.nearest(0, 7); // returns E (1, 5)
        System.out.println(ret.getX()); // evaluates to 1
        System.out.println(ret.getY()); // evaluates to 5


    }

}
