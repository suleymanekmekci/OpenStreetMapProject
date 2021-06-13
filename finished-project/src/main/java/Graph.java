import java.util.*;

public class Graph {
    // Implement the graph data structure here
    // Use Edge and Vertex classes as you see fit
    /* Code here */
    private Map<Long, ArrayList<Edge>> edgeList = new HashMap<>();

    private Map<Long,Vertex> vertices = new HashMap<>();
    private Vertex lastVertex;

    public Vertex getLastVertex() {
        return lastVertex;
    }

    public void setLastVertex(Vertex lastVertex) {
        this.lastVertex = lastVertex;
    }

    public Map<Long, ArrayList<Edge>> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(Map<Long, ArrayList<Edge>> edgeList) {
        this.edgeList = edgeList;
    }



    public Map<Long, Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(Map<Long, Vertex> vertices) {
        this.vertices = vertices;
    }
}
