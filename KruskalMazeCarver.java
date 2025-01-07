package mazes.logic.carvers;

import graphs.EdgeWithData;
import graphs.minspantrees.MinimumSpanningTree;
import graphs.minspantrees.MinimumSpanningTreeFinder;
import mazes.entities.Room;
import mazes.entities.Wall;
import mazes.logic.MazeGraph;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Carves out a maze based on Kruskal's algorithm.
 */
public class KruskalMazeCarver extends MazeCarver {
    MinimumSpanningTreeFinder<MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder;
    private final Random rand;

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random();
    }

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder,
                             long seed) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random(seed);
    }

    @Override
    protected Set<Wall> chooseWallsToRemove(Set<Wall> walls) {
        // Convert each wall into an edge with a random weight
        // Hint: you'll probably need to include something like the following:
        // this.minimumSpanningTreeFinder.findMinimumSpanningTree(new MazeGraph(edges))
        List<EdgeWithData<Room, Wall>> edges = walls.stream().map(wall -> new EdgeWithData<>(wall.getRoom1(),
                                                                    wall.getRoom2(), rand.nextDouble(),
                                                                    wall)).collect(Collectors.toList());
        // Find the minimum spanning tree of the maze graph
        MazeGraph graph = new MazeGraph(edges);
        MinimumSpanningTree<Room, EdgeWithData<Room, Wall>> mst =
                                                    this.minimumSpanningTreeFinder.findMinimumSpanningTree(graph);
        return mst.edges().stream().map(EdgeWithData::data).collect(Collectors.toSet());
    }
}
