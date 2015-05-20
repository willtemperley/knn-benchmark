package voidious.utils;

import java.util.ArrayList;
import java.util.Stack;

/*
 * This is a Kd-tree, as in http://en.wikipedia.org/wiki/Kd-tree, 
 * useful for efficiently finding nearest neighbors in a k-dimensional
 * space.
 * 
 * Thanks for the idea, Corbos.
 * 
 * Code is open source, released under the RoboWiki Public Code License:
 * http://robowiki.net/?RWPCL
 */

public class KdTree {
    double[] _location;
    int _k;
    int _dimension;
    KdTree _parent, _left, _right;
    int _leftChildren;
    int _rightChildren;
//    private static String lastFindLeaf = "";
//    private static String lastNnLog = "";
    
    public static void main(String[] args) {
        KdTree.runDiagnostics2();
    }
    public KdTree() {
        this(null, null, 0);
    }
    
    public KdTree(int d) {
        this(null, null, d);
    }
    
    public KdTree(KdTree parent, int d) {
        this(parent, null, d);
    }
    
    public KdTree(KdTree parent, double[] l, int d) {
        _parent = parent;
        _location = l;
        if (l != null) { 
            _k = l.length;
            _dimension = d % _k;
        }
        _leftChildren = 0;
        _rightChildren = 0;
    }
    
    public double[] getLocation() {
        return _location;
    }
    
    public int getK() {
        return _k;
    }
    
    public int getDimension() {
        return _dimension;
    }
    
    public boolean isParent() {
        return (_parent == null);
    }
    
    public boolean isLeaf() {
        return (_left == null && _right == null);
    }

    public KdTree getParent() {
        return _parent;
    }
    
    public KdTree getOtherChild(KdTree child) {
        if (child == _left) {
            return _right;
        }
        return _left;
    }
    
    public double getSplitValue() {
        return _location[_dimension];
    }
    
    public void insert(double[] point) {
/*
        if (isParent()) { 
            System.out.print("Inserting point: " );
            String value = "";
            for (int x = 0; x < point.length; x++) {
                if (x != 0) { value += ", "; }
                value += point[x];
            }
            System.out.println(value);
        }
*/
        if (_location == null) {
            _location = point;
            _k = point.length;
            _dimension = 0;
//            System.out.println("To root: " + this.toString());
        } else {
            if (point[_dimension] < _location[_dimension]) {
//                System.out.println("  Left of " + this.toString());
                if (_left == null) {
                    _left = new KdTree(this, point, _dimension + 1);
                } else {
                    _left.insert(point);
                }
                _leftChildren++;
            } else {
//                System.out.println("  Right of " + this.toString());
                if (_right == null) {
                    _right = new KdTree(this, point, _dimension + 1);
                } else {
                    _right.insert(point);
                }
                _rightChildren++;
            }
        }
    }
    
    public KdTree findLeaf(double[] point) {
//        if (isParent()) {
//            lastFindLeaf = "";
//        }
//        lastFindLeaf += "At " + pointAsString(_location) + "\n";

        if (point[_dimension] < _location[_dimension]) {
            if (_left == null) {
                if (_right == null) {
//                  lastFindLeaf += "  Leaf node, done.\n";
                    return this;
                }
//                lastFindLeaf += "  No left subtree, right on dim=" + 
//                    _dimension +".\n";
                return _right.findLeaf(point);
            }
//                lastFindLeaf += "  Left on dim=" + _dimension + ".\n";
            return _left.findLeaf(point);
        } else {
            if (_right == null) {
                if (_left == null) {
//                  lastFindLeaf += "  Leaf node, done.\n";
                    return this;
                }
//                lastFindLeaf += "  No right subtree, left on dim=" + 
//                    _dimension +".\n";
                return _left.findLeaf(point);
            }
//                lastFindLeaf += "  Right on dim=" + _dimension + ".\n";
            return _right.findLeaf(point);
        }
    }
    
    public static double[] nearestNeighbor(KdTree tree, 
        double[] searchPoint) {
        
//        lastNnLog = "Finding nearest neighbor of: " + 
//            RTree.pointAsString(searchPoint) + "\n";
        
        Stack<KdTree> crossed = new Stack<KdTree>();
        crossed.push(new KdTree());

        KdTree node = tree.findLeaf(searchPoint);
        KdTree nearest = node;
        KdTree lastChild;
        double closestDistanceSq = 
            KdTree.distanceSq(searchPoint, node.getLocation());
        
//        lastNnLog += "Starting at node (" + node.getDimension() + "): " + 
//            node.toString() + ", distanceSq: " + closestDistanceSq + "\n";
        while (!node.isParent()) {
            lastChild = node;
            node = node.getParent();
            
            if (crossed.peek() == node) {
                crossed.pop();
            } else {
                double parentDistanceSq = 
                    KdTree.distanceSq(node.getLocation(), searchPoint);
//                lastNnLog += "  Parent (" + node.getDimension() + "): " + 
//                    node.toString() + ", distanceSq: " + parentDistanceSq + 
//                    "\n";
                if (parentDistanceSq < closestDistanceSq) {
//                    lastNnLog += "    Parent now closest.\n";
                    closestDistanceSq = parentDistanceSq;
                    nearest = node;
                }
            
                double z = 
                    node.getSplitValue() - searchPoint[node.getDimension()];
                double testDistanceSq = z * z;
                if (testDistanceSq < closestDistanceSq) {
                    KdTree otherSide = node.getOtherChild(lastChild);
                    if (otherSide != null) {
//                        lastNnLog += "  Closest possible distanceSq across " +
//                            "split dimension " + node.getDimension() + ": " + 
//                            testDistanceSq + "\n";
                        KdTree testNode = otherSide.findLeaf(searchPoint);
                        testDistanceSq = KdTree.distanceSq(
                            testNode.getLocation(), searchPoint);
//                        lastNnLog += "    Moving to leaf node (" + 
//                            testNode.getDimension() + "): " + 
//                            testNode.toString() + ", distanceSq: " + 
//                            testDistanceSq + "\n";
                        if (testDistanceSq < closestDistanceSq) {
//                            lastNnLog += "      New leaf node now nearest.\n";
                            closestDistanceSq = testDistanceSq;
                            nearest = testNode;
                        }
                        crossed.push(node);
                        node = testNode;
                    }
                }
            }
        }
        
        return nearest.getLocation();
    }
    
    public static double[][] nearestNeighbors(KdTree tree, 
        double[] searchPoint, int numNeighbors) {
        
        if (numNeighbors == 1) { 
            return new double[][]{nearestNeighbor(tree, searchPoint)};
        }
        
        if (numNeighbors <= 0) {
            return null;
        }
            
//        lastNnLog = "Finding " + numNeighbors + " nearest neighbors of: " + 
//            RTree.pointAsString(searchPoint) + "\n";
        
        Stack<KdTree> crossed = new Stack<KdTree>();
        crossed.push(new KdTree());
        double[][] nearest = new double[numNeighbors][searchPoint.length];
        double[] nearestDistancesSq = new double[numNeighbors];
        
        KdTree node = tree.findLeaf(searchPoint);
        KdTree lastChild;
        double distanceSqThreshold = Double.POSITIVE_INFINITY;
        nearestDistancesSq[0] = node.distanceSq(searchPoint); 
        nearest[0] = node.getLocation();
        for (int x = 1; x < nearestDistancesSq.length; x++) {
            nearestDistancesSq[x] = Double.POSITIVE_INFINITY;
        }
        
//        lastNnLog += "Starting at node (" + node.getDimension() + "): " + 
//            node.toString() + ", distanceSq: " + 
//            RTree.distanceSq(node.getLocation(), searchPoint) + "\n";
        while (!node.isParent()) {
            lastChild = node;
            node = node.getParent();
            
            if (crossed.peek() == node) {
                crossed.pop();
            } else {
                double parentDistanceSq = node.distanceSq(searchPoint);
//                lastNnLog += "  Parent (" + node.getDimension() + "): " + 
//                    node.toString() + ", distanceSq: " + parentDistanceSq + 
//                    "\n";
                if (parentDistanceSq < distanceSqThreshold) {
                    distanceSqThreshold = findAndReplaceLongestDistanceSqSorted(
                        nearest, nearestDistancesSq, node.getLocation(), 
                        parentDistanceSq);
//                    lastNnLog += "    Parent among now nearest.\n";
                }

                double z = 
                    node.getSplitValue() - searchPoint[node.getDimension()];
                double testDistanceSq = z * z;
                if (testDistanceSq < distanceSqThreshold) {
                    KdTree otherSide = node.getOtherChild(lastChild);
                    if (otherSide != null) {
//                        lastNnLog += "  Closest possible distanceSq across " +
//                            "split dimension " + node.getDimension() + ": " + 
//                            testDistanceSq + "\n";
                        KdTree testNode = otherSide.findLeaf(searchPoint);
                        testDistanceSq = testNode.distanceSq(searchPoint);
//                        lastNnLog += "    Moving to leaf node (" + 
//                            testNode.getDimension() + "): " + 
//                            testNode.toString() + ", distanceSq: " + 
//                            testDistanceSq + "\n";
                        if (testDistanceSq < distanceSqThreshold) {
                            distanceSqThreshold = 
                                findAndReplaceLongestDistanceSqSorted(
                                    nearest, nearestDistancesSq, 
                                    testNode.getLocation(), testDistanceSq);
//                            lastNnLog += 
//                                "      New leaf node now among nearest.\n";
                        }
                        crossed.push(node);
                        node = testNode;
                    }
                }
            }
        }
        
        return nearest;
    }
    
    public double distanceSq(double[] p1) {
        double sum = 0;
        for (int x = 0; x < p1.length; x++) {
            double z = _location[x] - p1[x];
            sum += z * z;
        }
        
        return sum;        
    }
    
    public static double distanceSq(double[] p1, double[] p2) {
        double sum = 0;
        for (int x = 0; x < p1.length; x++) {
            double z = p1[x] - p2[x];
            sum += z * z;
        }
        
        return sum;
    }
    
    public static double distance(double[] p1, double[] p2) {
        return Math.sqrt(distanceSq(p1, p2));
    }

    public static double findLongestDistanceSq(double[][] points,
        double[] testPoint) {
            
        double longestDistanceSq = 0;
        for (int x = 0; x < points.length; x++) {
            double distanceSq = KdTree.distanceSq(points[x], testPoint);
            if (distanceSq > longestDistanceSq) {
                longestDistanceSq = distanceSq;
            }
        }
        
        return longestDistanceSq;
    }

    public static double findLongestDistanceSq(double[] nearestDistancesSq) {
                
        double longestDistanceSq = Double.NEGATIVE_INFINITY;
        for (int x = 0; x < nearestDistancesSq.length; x++) {
            double distanceSq = nearestDistancesSq[x];
            if (distanceSq > longestDistanceSq) {
                longestDistanceSq = distanceSq;
            }
        }
        
        return longestDistanceSq;
    }

    public static double findAndReplaceLongestDistanceSq(double[][] points,
        double[] nearestDistances, double[] newPoint, 
        double newPointDistanceSq) {
                    
        double longestDistanceSq = 0;
        double newLongestDistanceSq = 0;
        int longestIndex = 0;
        for (int x = 0; x < points.length; x++) {
            double distanceSq = nearestDistances[x];
            if (distanceSq > longestDistanceSq) {
                newLongestDistanceSq = longestDistanceSq;
                longestDistanceSq = distanceSq;
                longestIndex = x;
            } else if (distanceSq > newLongestDistanceSq) {
                newLongestDistanceSq = distanceSq;
            }
        }
        points[longestIndex] = newPoint;
        nearestDistances[longestIndex] = newPointDistanceSq;
        
        return Math.max(newLongestDistanceSq, newPointDistanceSq);
    }

    public static double findAndReplaceLongestDistanceSqSorted(
        double[][] points, double[] nearestDistances, double[] newPoint, 
        double newPointDistanceSq) {
             
        int x;
        for (x = points.length - 2; x >= 0; x--) {
            double distanceSq = nearestDistances[x];
            if (newPointDistanceSq > distanceSq) {
                nearestDistances[x + 1] = newPointDistanceSq;
                points[x + 1] = newPoint;
                return nearestDistances[points.length - 1];
            } else {
                nearestDistances[x + 1] = nearestDistances[x];
                points[x + 1] = points[x];
            }
        }

        if (x < 0) {
            nearestDistances[0] = newPointDistanceSq;
            points[0] = newPoint;
        }
        
        return nearestDistances[points.length - 1];
    }

    public static double[] copyPoint(double[] p) {
        double[] copyLocation = new double[p.length];
        for (int x = 0; x < p.length; x++) {
            copyLocation[x] = p[x];
        }
        
        return copyLocation;
    }
    
    public static void runDiagnostics() {
        KdTree testTree = new KdTree();
        ArrayList<double[]> testArray = new ArrayList<double[]>();
        long treeTime = 0;
        long bruteTime = 0;

        for (int x = 0; x < 25000; x++) {
            double[] testPoint = new double[]{
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1),
                    DiaUtils.round(Math.random() * 100, 1),
                    DiaUtils.round(Math.random() * 100, 1)
                    };
            testTree.insert(testPoint);
            testArray.add(testPoint);
        }

        for (int x = 0; x < 1000; x++) {
            double[] searchPoint = new double[]{
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1),
                    DiaUtils.round(Math.random() * 100, 1),
                    DiaUtils.round(Math.random() * 100, 1)
                    };
            treeTime -= System.currentTimeMillis();
            double[] nearestNeighbor = 
                KdTree.nearestNeighbor(testTree, searchPoint);
            treeTime += System.currentTimeMillis();
            double nearestDistanceSq = 
                KdTree.distanceSq(searchPoint, nearestNeighbor);
            bruteTime -= System.currentTimeMillis();
            double[] bruteClosestPoint = testArray.get(0);
            double bruteClosestDistanceSq = 
                KdTree.distanceSq(searchPoint, bruteClosestPoint);
            for (int y = 1; y < testArray.size(); y++) {
                double[] point = testArray.get(y);
                double thisDistanceSq = KdTree.distanceSq(searchPoint, point);
                if (thisDistanceSq < bruteClosestDistanceSq) {
                    bruteClosestDistanceSq = thisDistanceSq;
                    bruteClosestPoint = point;
                }
            }
            bruteTime += System.currentTimeMillis();
            if (bruteClosestDistanceSq < nearestDistanceSq) {
                System.out.println("Found a problem: ");
                System.out.println("  Search point: " + 
                    pointAsString(searchPoint));
                System.out.println("  NN from tree: " + 
                    pointAsString(nearestNeighbor));
                System.out.println("    DistanceSq: " + 
                    nearestDistanceSq);
                System.out.println("  Brute force found: " + 
                    pointAsString(bruteClosestPoint));
                System.out.println("    DistanceSq: " + bruteClosestDistanceSq);
//                System.out.println();
//                System.out.println(lastNnLog);
//                System.out.println();
//                System.out.println(lastFindLeaf);
                System.out.println("----");
            }
        }
        System.out.println("Time using kd-tree:     " + treeTime);
        System.out.println("Time using brute force: " + bruteTime);
    }
    
    public static void runDiagnostics2() {
        int numNeighbors = 25;
        KdTree testTree = new KdTree();
        ArrayList<double[]> testArray = new ArrayList<double[]>();
        long treeTime = 0;
        long bruteTime = 0;

        for (int x = 0; x < 15000; x++) {
            double[] testPoint = new double[]{
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1)
                    };
            testTree.insert(testPoint);
            testArray.add(testPoint);
        }

        for (int x = 0; x < 1000; x++) {
            double[] searchPoint = new double[]{
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1),
                    DiaUtils.round(Math.random() * 100, 1),
                    DiaUtils.round(Math.random() * 100, 1), 
                    DiaUtils.round(Math.random() * 100, 1)
                    };
            treeTime -= System.nanoTime();
            double[][] nearestNeighbors = KdTree.nearestNeighbors(testTree, 
                searchPoint, numNeighbors);
            treeTime += System.nanoTime();
            double nearestDistanceSqThreshold = 
                findLongestDistanceSq(nearestNeighbors, searchPoint);
            bruteTime -= System.nanoTime();
            double[][] bruteClosestPoints = 
                new double[numNeighbors][searchPoint.length];
            double[] nearestDistancesSq = new double[numNeighbors];
            for (int y = 0; y < numNeighbors; y++) {
                bruteClosestPoints[y] = testArray.get(y);
                nearestDistancesSq[y] = 
                    KdTree.distanceSq(bruteClosestPoints[y], searchPoint);
            }
            double bruteClosestDistanceSqThreshold = 
                findLongestDistanceSq(bruteClosestPoints, searchPoint);
            for (int y = numNeighbors; y < testArray.size(); y++) {
                double[] point = testArray.get(y);
                double thisDistanceSq = KdTree.distanceSq(searchPoint, point);
                if (thisDistanceSq < bruteClosestDistanceSqThreshold) {
                    bruteClosestDistanceSqThreshold = 
                        findAndReplaceLongestDistanceSq(bruteClosestPoints, 
                            nearestDistancesSq, point, thisDistanceSq);
                }
            }
            bruteTime += System.nanoTime();
            if (bruteClosestDistanceSqThreshold < nearestDistanceSqThreshold) {
                System.out.println("Found a problem: ");
                System.out.println("  Search point: " + 
                    pointAsString(searchPoint));
                System.out.println("  NN threshold: " + 
                    nearestDistanceSqThreshold);
                System.out.println("  Brute force:  " + 
                    bruteClosestDistanceSqThreshold);
                System.out.println();
                
                for (int z = 0; z < numNeighbors; z++) {
                    System.out.println(pointAsString(nearestNeighbors[z]) + 
                        "  \t" + pointAsString(bruteClosestPoints[z]));
                }
                System.out.println();

//              System.out.println(lastNnLog);
                System.out.println("----");
            } else if (nearestDistanceSqThreshold < 
                           bruteClosestDistanceSqThreshold) {
                System.out.println("Found a weirder problem: ");
                System.out.println("  Search point: " + 
                    pointAsString(searchPoint));
                System.out.println("  NN threshold: " + 
                    nearestDistanceSqThreshold);
                System.out.println("  Brute force:  " + 
                    bruteClosestDistanceSqThreshold);
                System.out.println("----");                
            }
        }
        System.out.println("Time using kd-tree:     " + treeTime);
        System.out.println("Time using brute force: " + bruteTime);
    }

    public static String pointAsString(double[] point) {
        String str = "";
        for (int x = 0; x < point.length; x++) {
            if (x != 0) { str += ", "; }
            str += point[x];
        }
        
        return str;
    }

    public String toString() {
        return pointAsString(_location);
    }
}
