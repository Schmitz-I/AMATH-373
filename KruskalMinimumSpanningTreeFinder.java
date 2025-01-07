package graphs.minspantrees;

import disjointsets.DisjointSets;
import disjointsets.QuickFindDisjointSets;
import graphs.BaseEdge;
import graphs.KruskalGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Computes minimum spanning trees using Kruskal's algorithm.
 * @see MinimumSpanningTreeFinder for more documentation.
 */
public class KruskalMinimumSpanningTreeFinder<G extends KruskalGraph<V, E>, V, E extends BaseEdge<V, E>>
    implements MinimumSpanningTreeFinder<G, V, E> {

    protected DisjointSets<V> createDisjointSets() {
        return new QuickFindDisjointSets<>();
        /*
        Disable the line above and enable the one below after you've finished implementing
        your `UnionBySizeCompressingDisjointSets`.
         */
        // return new UnionBySizeCompressingDisjointSets<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    public MinimumSpanningTree<V, E> findMinimumSpanningTree(G graph) {
        // Here's some code to get you started; feel free to change or rearrange it if you'd like.

        // sort edges in the graph in ascending weight order
        List<E> edges = new ArrayList<>(graph.allEdges());
        List<V> vertices = new ArrayList<>(graph.allVertices());
        Set<E> mstEdges = new HashSet<>();
        DisjointSets<V> disjointSets = createDisjointSets();

        if (vertices.size() <= 1) {
            return new MinimumSpanningTree.Success<>(mstEdges);
        }
        // Initialize disjoint sets for all vertices
        vertices.forEach(disjointSets::makeSet);
        // Sort edges by weight
        edges.sort(Comparator.comparingDouble(E::weight));

        // Process edges in ascending weight order
        for (E edge : edges) {
            V source = edge.from();
            V destination = edge.to();
            if (disjointSets.union(source, destination)) {
                mstEdges.add(edge);
                if (mstEdges.size() == vertices.size() - 1) {
                    return new MinimumSpanningTree.Success<>(mstEdges);
                }
            }
        }
        return new MinimumSpanningTree.Failure<>();
    }
}
