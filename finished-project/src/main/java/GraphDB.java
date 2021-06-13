import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class GraphDB {


    public Graph graph = new Graph();
    public TST<Vertex> tst = new TST<>();
    public Set<Vertex> verticesWhichHaveWay = new HashSet<>();

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    static String normalizeString(String s) {
        // Should match all strings that are not alphabetical
        String regex = "[^a-zA-Z]"/* Replace *//* Code here */;
        return s.replaceAll(regex, "").toLowerCase(Locale.ENGLISH);
    }

    private void clean() {
        // Remove the vertices with no incoming and outgoing connections from your graph
        /* Code here */

        // store keys of vertices to be removed
        ArrayList<Long> valuesToRemove = new ArrayList<>();
        for (Map.Entry<Long,Vertex> entry: this.graph.getVertices().entrySet()) {
            if (!(verticesWhichHaveWay.contains(entry.getValue()))) {
                valuesToRemove.add(entry.getKey()); //
            }
        }
        // remove vertices with no way from the map.
        this.graph.getVertices().keySet().removeAll(valuesToRemove);

    }

    public double distance(Vertex v1, Vertex v2) {
        // Return the euclidean distance between two vertices
        /* Code here */
        double x1 = v1.getLat();
        double y1 = v1.getLng();

        double x2 = v2.getLat();
        double y2 = v2.getLng();

        return Math.sqrt(Math.pow(y2-y1,2) + Math.pow(x2-x1,2));
    }


    public long closest(double lon, double lat) {
        // Returns the closest vertex to the given latitude and longitude values
        /* Code here */

        Vertex givenVertex = new Vertex(lat,lon,0L);
        Vertex output = this.graph.getLastVertex();

        // iterate all vertices until the find closest vertex
        for (Vertex currentVertex: this.graph.getVertices().values()) {
            if (distance(currentVertex,givenVertex) < distance(givenVertex,output) ) {
                output = currentVertex;
            }
        }

        return output.getId();
    }

    double lon(long v) {
        // Returns the longitude of the given vertex, v is the vertex id
        /* Code here */
        return this.graph.getVertices().get(v).getLng();
    }


    double lat(long v) {
        // Returns the latitude of the given vertex, v is the vertex id
        /* Code here */
        return this.graph.getVertices().get(v).getLat();
    }
}
