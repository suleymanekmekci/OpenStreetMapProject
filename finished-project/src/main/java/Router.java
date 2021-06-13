import java.lang.reflect.Array;
import java.util.*;


public class Router {

    private static List<Vertex> stops = new ArrayList<>();
    private static Vertex start, end;

    private static Map<Long,Double> distances;
    private static Map<Long, LinkedList<Vertex>> path;
    private static PriorityQueue<Vertex> queue;


    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        // Return the shortest path between start and end points
        // Use g.closest() to get start and end vertices
        // Return ids of vertices as a linked list
        /* Code here */

        distances = new HashMap<>();
        path = new HashMap<>();

        Map<Long,Vertex> vertices = g.graph.getVertices();
        Map<Long,ArrayList<Edge>> edgeList = g.graph.getEdgeList();

        queue = new PriorityQueue<Vertex>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return Double.compare(distances.get(o1.getId()), distances.get(o2.getId()));
            }
        });


        Long startId = g.closest(stlon,stlat);

        Long endId = g.closest(destlon,destlat);

        if (start == null && end == null) {
            start = new Vertex(stlat,stlon,startId);
            end = new Vertex(destlat,destlon,endId);
        }




        // add source vertex to the path
        queue.add(vertices.get(startId));




        // set distances for each vertex as infinity. set source distance 0.
        for (Long vertexId: g.graph.getVertices().keySet()) {
            if (vertexId.equals(startId)) {
                distances.put(vertexId,0d);
            }
            else {
                distances.put(vertexId,Double.MAX_VALUE);
            }
        }


        while(!queue.isEmpty()){
            // get the maximum
            Vertex u = queue.poll();

            // check if vertex has any adjacency list
            ArrayList<Edge> loop = new ArrayList<>();
            if (edgeList.containsKey(u.getId())) {
                loop = edgeList.get(u.getId());
            }

            for(Edge neighbour: loop){


                // get weight of the current edge
                double weight = neighbour.getWeight();

                // add current weight to the source weight. we will compare it later between the neighbours
                Double newDist = distances.get(u.getId()) + weight;


                if(distances.get(neighbour.getDestination().getId())>newDist){
                    // delete the node from the queue

                    queue.remove(neighbour.getDestination());
                    // update the distance value
                    distances.put(neighbour.getDestination().getId(),newDist);

                    LinkedList<Vertex> adding = new LinkedList<Vertex>();
                    if (path.containsKey(u.getId())) {
                        // if there is already a path to the current node, initialize an array with the path.
                        adding = new LinkedList<>(path.get(u.getId()));
                    }
                    // initialize
                    path.put(neighbour.getDestination().getId(),adding);
                    // add the current path.
                    path.get(neighbour.getDestination().getId()).add(u);

                    // add same node with the different weight
                    queue.add(neighbour.getDestination());
                }
            }
        }



        LinkedList<Vertex> result = path.get(endId);
        LinkedList<Long> output = new LinkedList<>();

//        if (result == null) {
//            return output;
//        }
        for (Vertex aa : result) {
            output.add(aa.getId());
        }
        output.add(endId);

        return output;
    }

    public static LinkedList<Long> addStop(GraphDB g, double lat, double lon) {
        // Find the closest vertex to the stop coordinates using g.closest()
        // Add the stop to the stop list
        // Recalculate your route when a stop is added and return the new route
        /* Code here */

        Long stopId = g.closest(lon,lat);
        Vertex stopVertex = g.graph.getVertices().get(stopId);

        stops.add(stopVertex);

        Vertex temp = start;


        LinkedList<Long> output = new LinkedList<>();

        for (int i = 0; i < stops.size();i++) {
            Vertex stopPoint = stops.get(i);

            LinkedList<Long> routesToStop = Router.shortestPath(g, temp.getLng(), temp.getLat(), stopPoint.getLng(), stopPoint.getLat());
            output.addAll(routesToStop);
            temp = stopPoint;
        }

        LinkedList<Long> lastRoutes = Router.shortestPath(g, temp.getLng(), temp.getLat(), end.getLng(), end.getLat());
        output.addAll(lastRoutes);

        return output;
    }

    public static void clearRoute() {
        start = null;
        end = null;
        stops = new ArrayList<>();
    }
}
