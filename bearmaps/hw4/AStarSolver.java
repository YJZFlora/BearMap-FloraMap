package bearmaps.hw4;

import bearmaps.proj2ab.ArrayHeapMinPQ;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.*;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private SolverOutcome outcome;
    private double timeSpent = 0.0;
    private double solutionWeight = 0.0;
    private int numStatesExplored;  // The total number of priority queue dequeue operations.
    private List<Vertex> solution;
    private Map<Vertex, Vertex> edgeTo = new HashMap<>();
    private List<Vertex> realSolution = new LinkedList<>();
    private AStarGraph<Vertex> G;


    /* Constructor which finds the solution,
        computing everything necessary for all other methods to return their results in constant time.
        Note that timeout passed in is in seconds.
     */
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        G = input;
        Stopwatch sw = new Stopwatch();
        ArrayHeapMinPQ<Vertex> pq = new ArrayHeapMinPQ<>();
        Map<Vertex, Double> disTo = new HashMap<>();

        // input.estimatedDistanceToGoal(v, end) is h(v, end)
        pq.add(start, G.estimatedDistanceToGoal(start, end));
        disTo.put(start, 0.0);

        // Repeat until the PQ is empty, PQ.getSmallest() is the goal, or timeout is exceeded
        while (pq.size() != 0) {
            if (timeSpent > timeout) {
                outcome = SolverOutcome.TIMEOUT;
                return;
            }
            Vertex p = pq.removeSmallest();
            timeSpent = sw.elapsedTime();

            // Next vertex to be dequeued is our target, so we’re done!
            if (p.equals(end)) {
                outcome = SolverOutcome.SOLVED;
                solutionWeight = disTo.get(p);
                timeSpent = sw.elapsedTime();
                solution = solutionHelper(end, start);
                return;
            }
            numStatesExplored += 1;
            List<WeightedEdge<Vertex>> neighborEdges = G.neighbors(p);
            // relax all edges outgoing from p
            for (WeightedEdge<Vertex> e : neighborEdges) {
                Vertex to = e.to();
                Vertex from = e.from();
                double w = e.weight();
                // has not been visited yet
                if (disTo.get(to) == null) {
                    edgeTo.put(to, from);
                    disTo.put(to, disTo.get(from) + w);
                    pq.add(to, disTo.get(to) + G.estimatedDistanceToGoal(to, end));
                } else if(disTo.get(from) + w < disTo.get(to)) { // visited, but can be replaced
                    edgeTo.replace(to, from);
                    double newD = disTo.get(from) + w;
                    disTo.replace(to, newD);
                    double newP = disTo.get(to) + G.estimatedDistanceToGoal(to, end);
                    if(pq.contains(to)) {  // already in pq
                        pq.changePriority(to, newP);
                    } else {  // not in pq
                        pq.add(to, newP);
                    }
                }
            }
        }

        outcome = SolverOutcome.UNSOLVABLE;
        timeSpent = sw.elapsedTime();
    }

    /* Returns one of SolverOutcome.SOLVED, SolverOutcome.TIMEOUT, or SolverOutcome.UNSOLVABLE
     */
    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    /* A list of vertices corresponding to a solution.
       Should be empty if result was TIMEOUT or UNSOLVABLE.
     */
    @Override
    public List<Vertex> solution() {
        if (realSolution.size() == 0) {
            for(int i = solution.size() - 1; i >= 0  ; i--) {
                Vertex toAdd = solution.remove(i);
                realSolution.add(toAdd);
            }
            return realSolution;
        }
        return realSolution;

    }

    // give the reversed solution
    private List<Vertex> solutionHelper(Vertex end, Vertex start) {
        List<Vertex> l = new LinkedList<>();
        l.add(end);
        Vertex toAdd = edgeTo.get(end);
        while(!toAdd.equals(start)) {
            l.add(toAdd);
            toAdd = edgeTo.get(toAdd);
        }
        l.add(start);
        return l;
    }

    /* The total weight of the given solution, taking into account edge weights.
        Should be 0 if result was TIMEOUT or UNSOLVABLE.
     */
    @Override
    public double solutionWeight() {
        return solutionWeight;
    }

    // The total number of priority queue dequeue operations.
    @Override
    public int numStatesExplored() {
        return numStatesExplored;
    }

    // The total time spent in seconds by the constructor.
    public double explorationTime() {
        return timeSpent;
    }

}
