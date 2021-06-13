import java.util.*;


public class QuadTree {
    public QTreeNode root;
    private String imageRoot;
    private List<List<QTreeNode>> searchResult = new ArrayList<>();

    public QuadTree(String imageRoot) {
        // Instantiate the root element of the tree with depth 0
        // Use the ROOT_ULLAT, ROOT_ULLON, ROOT_LRLAT, ROOT_LRLON static variables of MapServer class
        // Call the build method with depth 1
        // Save the imageRoot value to the instance variable
        /* Code here */
        this.imageRoot = imageRoot;
        this.root = new QTreeNode("root",MapServer.ROOT_ULLAT,MapServer.ROOT_ULLON,MapServer.ROOT_LRLAT,MapServer.ROOT_LRLON,0);
        build(this.root,1);
    }

    public void build(QTreeNode subTreeRoot, int depth) {
        // Recursive method to build the tree as instructed
        /* Code here */

        if (depth == 9) {
            return;
        }
        String name;
        if (depth - 1 == this.root.getDepth())
            name = "";
        else {
            name = subTreeRoot.getName();
        }

        double middleLongitude = (subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude()) / 2;
        double middleLatitude = ( subTreeRoot.getUpperLeftLatitude() + subTreeRoot.getLowerRightLatitude()) / 2;
        subTreeRoot.NW = new QTreeNode(name + "1",subTreeRoot.getUpperLeftLatitude(),subTreeRoot.getUpperLeftLongtitude(),middleLatitude,middleLongitude,depth);
        subTreeRoot.NE = new QTreeNode(name + "2", subTreeRoot.getUpperLeftLatitude(), middleLongitude,middleLatitude,subTreeRoot.getLowerRightLongtitude(), depth);
        subTreeRoot.SW = new QTreeNode(name + "3", middleLatitude, subTreeRoot.getUpperLeftLongtitude(), subTreeRoot.getLowerRightLatitude() ,middleLongitude, depth);
        subTreeRoot.SE = new QTreeNode(name + "4", middleLatitude, middleLongitude,subTreeRoot.getLowerRightLatitude(),subTreeRoot.getLowerRightLongtitude(),depth);

        build(subTreeRoot.NW,depth + 1);
        build(subTreeRoot.NE,depth + 1);
        build(subTreeRoot.SW,depth + 1);
        build(subTreeRoot.SE,depth + 1);
    }

    public Map<String, Object> search(Map<String, Double> params) {
        /*
         * Parameters are:
         * "ullat": Upper left latitude of the query box
         * "ullon": Upper left longitude of the query box
         * "lrlat": Lower right latitude of the query box
         * "lrlon": Lower right longitude of the query box
         * */

        // Instantiate a QTreeNode to represent the query box defined by the parameters
        // Calculate the lonDpp value of the query box
        // Call the search() method with the query box and the lonDpp value
        // Call and return the result of the getMap() method to return the acquired nodes in an appropriate way
        /* Code here */

        double ullat = params.get("ullat");
        double ullon = params.get("ullon");
        double lrlat = params.get("lrlat");
        double lrlon = params.get("lrlon");
        double w = params.get("w");

        QTreeNode queryBox = new QTreeNode("",ullat,ullon,lrlat,lrlon,0);
        double queryBoxLonDpp =  (lrlon - ullon) / w;
        ArrayList<QTreeNode> result = new ArrayList<>();
        search(queryBox,this.root,queryBoxLonDpp, result);
        return getMap(result);
    }

    private Map<String, Object> getMap(ArrayList<QTreeNode> list) {
        Map<String, Object> map = new HashMap<>();

        // Check if the root intersects with the given query box
        if (list.size() == 0) {
            map.put("query_success", false);
            return map;
        }

        // Use the get2D() method to get organized images in a 2D array
        map.put("render_grid", get2D(list));
        String[][] test = get2D(list);
        // Upper left latitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        double result = 0d;
        for(QTreeNode node : list) {

            if (node.getName().equals(test[0][0].replace("img/","").replace(".png",""))) {
                result = node.getUpperLeftLatitude();
            }
        }
        map.put("raster_ul_lat", searchResult.get(0).get(0).getUpperLeftLatitude());

        // Upper left longitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_ul_lon", searchResult.get(0).get(0).getUpperLeftLongtitude());

        // Upper lower right latitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_lr_lat", searchResult.get(searchResult.size() - 1).get( searchResult.get(searchResult.size() - 1).size() -1).getLowerRightLatitude() );

        // Upper lower right longitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_lr_lon", searchResult.get(searchResult.size() - 1).get( searchResult.get(searchResult.size() - 1).size() -1).getLowerRightLongtitude());

        // Depth of the grid (can be thought as the depth of a single image)
        map.put("depth",searchResult.get(0).get(0).getDepth());

        map.put("query_success", true);
        return map;
    }

    private String[][] get2D(ArrayList<QTreeNode> list) {
        String[][] images = new String[0][0];
        // After you retrieve the list of images using the recursive search mechanism described above, you
        // should order them as a grid. This grid is nothing more than a 2D array of file names. To order
        // the images, you should determine correct row and column for each image (node) in the retrieved
        // list. As a hint, you should consider the latitude values of images to place them in the row, and
        // the file names of the images to place them in a column.
        /* Code here */

        searchResult.clear();

        int ullatCounter = 0; // how many elements should be in every list inside of the list
        double ullat = list.get(0).getUpperLeftLatitude(); // take the first element. count the elements with the same ullat
        for (QTreeNode node: list ) {
            if (node.getUpperLeftLatitude() == ullat) {
                ullatCounter++;
            }
        }
        double ullong = list.get(0).getUpperLeftLongtitude(); // take the first element. count the elements with the same ullong
        int ullongCounter = 0; // how many elements should be in list
        for (QTreeNode node: list ) {
            if (node.getUpperLeftLongtitude() == ullong) {
                ullongCounter++;
            }
        }
        images = new String[ullongCounter][ullatCounter]; // re-initialize with the correct length



        Map<Double,List<QTreeNode>> ullats = new HashMap<>();

        for (QTreeNode node: list ) {
            if (ullats.containsKey(node.getUpperLeftLatitude())) {
                ullats.get(node.getUpperLeftLatitude()).add(node);
            }
            else {
                ArrayList<QTreeNode> tempList = new ArrayList<>();
                tempList.add(node);
                ullats.put(node.getUpperLeftLatitude(),tempList);
            }
        }
        ArrayList<Double> sortedKeys =new ArrayList<>(ullats.keySet());
        Collections.sort(sortedKeys); // sort elements by its latitudes
        Collections.reverse(sortedKeys); // reverse collection

        for (int i = 0; i < images.length; i++) {

            String[] temp = new String[ullatCounter];
            // sort every row by its name
            ullats.get(sortedKeys.get(i)).sort(new Comparator<QTreeNode>() {
                @Override
                public int compare(QTreeNode o1, QTreeNode o2) {
                    return Integer.compare(Integer.parseInt(o1.getName()), Integer.parseInt(o2.getName()));
                }
            });

            for (int j = 0; j < ullatCounter; j++) {
                temp[j] = "img/" + ullats.get(sortedKeys.get(i)).get(j).getName() + ".png"; // add img/ and .png to the each name.
            }
            images[i] = temp;
            searchResult.add(ullats.get(sortedKeys.get(i))); // store the result outside
        }

        return images;
    }

    public void search(QTreeNode queryBox, QTreeNode tile, double lonDpp, ArrayList<QTreeNode> list) {
        // The first part includes a recursive search in the tree. This process should consider both the
        // lonDPP property (discussed above) and if the images in the tree intersect with the query box.
        // (To check the intersection of two tiles, you should use the checkIntersection() method)
        // To achieve this, you should retrieve the first depth (zoom level) of the images which intersect
        // with the query box and have a lower lonDPP than the query box.
        // This method should fill the list given by the "ArrayList<QTreeNode> list" parameter
        /* Code here */

        if (checkIntersection(tile,queryBox)) {
            if (tile.getLonDPP() <= lonDpp) {
                list.add(tile);
            }
            else {
                search(queryBox,tile.NE,lonDpp,list);
                search(queryBox,tile.NW,lonDpp,list);
                search(queryBox,tile.SE,lonDpp,list);
                search(queryBox,tile.SW,lonDpp,list);
            }
        }



    }

    public boolean checkIntersection(QTreeNode tile, QTreeNode queryBox) {
        // Return true if two tiles are intersecting with each other
        /* Code here */

        double ax1 = tile.getUpperLeftLongtitude();
        double ay1 = tile.getUpperLeftLatitude();
        double ax2 = tile.getLowerRightLongtitude();
        double ay2 = tile.getLowerRightLatitude();

        double bx1 = queryBox.getUpperLeftLongtitude();
        double by1 = queryBox.getUpperLeftLatitude();
        double bx2 = queryBox.getLowerRightLongtitude();
        double by2 = queryBox.getLowerRightLatitude();


        return (ax1 <= bx2 && ax2 >= bx1 && ay1 >= by2 && ay2 <= by1);
    }
}