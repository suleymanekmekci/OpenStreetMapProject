# OpenStreetMapProject
In this assignment, we practiced implementing several data structures and algorithms (quadtrees, ternary search trees, Dijkstra’s shortest path algorithm for routing) on a real-world problem. We built a web application for mapping the real-world map data (tile images and map
feature data) which is freely available from the OpenStreetMap project. 

## [Detailed Explanation Here](https://github.com/suleymanekmekci/OpenStreetMapProject/blob/main/assignment.pdf)

# Running the Project

+ Mark the src directory as the sources root
+ Mark static directory as the resources root and build the project
+ Run the MapServer.main()
+ Check the server by opening localhost:4567 in your browser

# Features
## 1- Auto-Complete Feature using Ternary Search Trees
Application is able to serve location suggestions to the
user when given a minimum of two-letter prefix. When a location is selected, you should be able
to see a marker correctly placed above the selected location on the map

## 2- Dijkstra’s Shortest Path Algorithm for Routing
You can test this feature by double-clicking on the map
to pick a source location and double-clicking again to pick a destination location. You should be
able to add a stop location to the current route by placing the cursor over the location you want
to add and then pressing “s” on the keyboard.
