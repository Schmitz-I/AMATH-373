package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        Map<V, E> spt = new HashMap<>();
        Map<V, Double> vertexToDistance = new HashMap<>();
        vertexToDistance.put(start, 0.0);
        ExtrinsicMinPQ<V> verticesPriorityQueue = createMinPQ();
        verticesPriorityQueue.add(start, 0.0);

        while (!verticesPriorityQueue.isEmpty()) {
            V currentVertex = verticesPriorityQueue.removeMin();
            if (currentVertex.equals(end)) {
                break;
            }
            for (E edge : graph.outgoingEdgesFrom(currentVertex)) {
                V targetVertex = edge.to();
                double newDistance = vertexToDistance.getOrDefault(currentVertex,
                                                                    Double.POSITIVE_INFINITY) + edge.weight();
                if (newDistance < vertexToDistance.getOrDefault(targetVertex, Double.POSITIVE_INFINITY)) {
                    vertexToDistance.put(targetVertex, newDistance);
                    spt.put(targetVertex, edge);

                    if (verticesPriorityQueue.contains(targetVertex)) {
                        verticesPriorityQueue.changePriority(targetVertex, newDistance);
                    } else {
                        verticesPriorityQueue.add(targetVertex, newDistance);
                    }
                }
            }
        }
        return spt;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }
        List<E> pathEdges = new LinkedList<>();
        for (E edge = spt.get(end); edge != null; edge = spt.get(edge.from())) {
            pathEdges.add(edge);
        }
        if (pathEdges.isEmpty()) {
            return new ShortestPath.Failure<>();
        }
        Collections.reverse(pathEdges);
        return new ShortestPath.Success<>(pathEdges);
    }
}
