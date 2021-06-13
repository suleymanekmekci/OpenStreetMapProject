import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;


public class GraphBuildingHandler extends DefaultHandler {


    public static class Way {
        private String id;
        private String name;
        private String speed;
        private boolean isOneWay;

        private final ArrayList<Long> listOfNodes = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public ArrayList<Long> getListOfNodes() {
            return listOfNodes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setOneWay(boolean oneway) {
            this.isOneWay = oneway;
        }

        public boolean isOneWay() {
            return this.isOneWay;
        }
    }


    private static final Set<String> ALLOWED_HIGHWAY_TYPES = new HashSet<>(Arrays.asList
            ("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                    "residential", "living_street", "motorway_link", "trunk_link", "primary_link",
                    "secondary_link", "tertiary_link"));

    private String activeState = "";
    private final GraphDB g;
    private Way currentWay = new Way();
    private boolean flag = false;


    public GraphBuildingHandler(GraphDB g) {
        this.g = g;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equals("node")) {

            activeState = "node";

            // For the case of new nodes, you should add a new vertex to your graph and save other necessary information
            // to be used depending on your implementation (i.e. saving the last saved vertex).
            /* Code here */

            Long id = Long.parseLong(attributes.getValue(0));
            double lat = Double.parseDouble(attributes.getValue(1));
            double lon = Double.parseDouble(attributes.getValue(2));
            Vertex temp = new Vertex(lat,lon,id);

            this.g.graph.getVertices().put(id,temp);
            this.g.graph.setLastVertex(temp);

        } else if (qName.equals("way")) {

            activeState = "way";

            // The first one is encountering a new "way" start tag. In this scenario, you should instantiate
            // a new Way, while saving its id.
            /* Code here */

            currentWay = new Way();
            String id = attributes.getValue(0);
            currentWay.setId(id);


        } else if (activeState.equals("way") && qName.equals("nd")) {

            // The second scenario is encountering a new "nd" tag while the active state is "way". This
            // tag references a node in the current way by its node id. When this tag is encountered, you
            // should add the id to the listOfNodes in that Way.
            /* Code here */
            Long vertexId = Long.parseLong(attributes.getValue(0));

            currentWay.getListOfNodes().add(vertexId);


        } else if (activeState.equals("way") && qName.equals("tag")) {

            String k = attributes.getValue("k");
            String v = attributes.getValue("v");

            if (k.equals("maxspeed")) {

                // Set the speed for the current way
                /* Code here */
                currentWay.setSpeed(v);
            } else if (k.equals("highway")) {

                // At this point, you should check if the parsed highway type is specified to be allowed in the
                // list ALLOWED_HIGHWAY_TYPES. If the type is allowed, you should set a flag as an
                // instance variable to specify that this way should be used to connect vertices in the graph.
                /* Code here */

                if (ALLOWED_HIGHWAY_TYPES.contains(v)) {
                    this.flag = true;
                }

            } else if (k.equals("name")) {

                // Set the name for the current way
                /* Code here */
                currentWay.setName(v);
            } else if (k.equals("oneway")) {
                // Set the oneway property for the current way
                /* Code here */

                if (v.equals("yes")) {
                    currentWay.setOneWay(true);
                }
                else {
                    currentWay.setOneWay(false);
                }

            }



        } else if (activeState.equals("node") && qName.equals("tag") && attributes.getValue("k").equals("name")) {
            // For the case of encountering a "tag" tag with the attribute "name" while the active state is
            // "node", you should set the name of the current vertex, as well as insert the location name to
            // your ternary search tree. Do not forget to normalize the text by supplying the regular expression
            // given in the coding template (GraphDB.cleanString())
            // Insert the normalized text as a key along with an list which contains the Vertex if the key is not in GraphDB.locations
            // If the key is in GraphDB.locations, retrieve the list and add the Vertex to the list
            /* Code here */
            String name = attributes.getValue("v");

            this.g.graph.getLastVertex().setName(name);
            this.g.tst.put(GraphDB.normalizeString(name),this.g.graph.getLastVertex());

        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way")) {
            // In endElement callback, you should handle ending of a way tag. If the way is marked as a valid
            // one in the third scenario described above, you should connect the vertices specified by the list
            // of references in the listOfNodes. In this step, you should be careful if the way is one-directional
            // or not. If the way you were parsing is not one-directional, you should add two edges to your
            // directed graph (in both directions).
            /* Code here */
            if (flag) {
                for (int i = 0; i < currentWay.getListOfNodes().size() - 1; i++) {
                    // get the current and the next way from the list of nodes from currentWay

                    Long leftId = currentWay.getListOfNodes().get(i);
                    Long rightId = currentWay.getListOfNodes().get(i+1);
                    Vertex left = this.g.graph.getVertices().get(leftId);
                    Vertex right = this.g.graph.getVertices().get(rightId);

                    // create a new edge. add this edge to the edge list
                    Edge edge1 = new Edge();
                    edge1.setSource(left);
                    edge1.setDestination(right);
                    edge1.setSpeed(currentWay.speed);
                    edge1.setName(currentWay.name);
                    edge1.setWeight(g.distance(left,right));


                    if (!this.g.graph.getEdgeList().containsKey(leftId)) {
                        this.g.graph.getEdgeList().put(leftId, new ArrayList<>());
                    }
                    this.g.graph.getEdgeList().get(leftId).add(edge1);


                    // if oneway is false, add one more edge
                    if (!(currentWay.isOneWay())) {

                        Edge edge2 = new Edge();
                        edge2.setSource(right);
                        edge2.setDestination(left);
                        edge2.setSpeed(currentWay.speed);
                        edge2.setName(currentWay.name);
                        edge2.setWeight(g.distance(right,left));

                        if (!this.g.graph.getEdgeList().containsKey(rightId)) {
                            this.g.graph.getEdgeList().put(rightId, new ArrayList<>());
                        }
                        this.g.graph.getEdgeList().get(rightId).add(edge2);

                    }



                    // add vertices which have way to anywhere so that we can clean the vertices with no way.
                    this.g.verticesWhichHaveWay.add(left);
                    this.g.verticesWhichHaveWay.add(right);


                }
            }
            flag = false;


        }
    }
}