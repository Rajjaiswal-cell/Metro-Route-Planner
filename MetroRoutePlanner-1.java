import java.util.*;

class Station {
    String id, name, line;

    Station(String id, String name, String line) {
        this.id = id;
        this.name = name;
        this.line = line;
    }

    public String toString() {
        return name + " (" + line + ")";
    }
}

class Edge {
    Station destination;
    int weight;

    Edge(Station destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }
}

class Node implements Comparable<Node> {
    Station station;
    int distance;

    Node(Station station, int distance) {
        this.station = station;
        this.distance = distance;
    }

    public int compareTo(Node other) {
        return Integer.compare(distance, other.distance);
    }
}

class MetroGraph {
    Map<String, Station> stations = new HashMap<>();
    Map<String, List<Edge>> graph = new HashMap<>();

    String normalize(String name) {
        return name.trim().toLowerCase();
    }

    void addStation(String id, String name, String line) {
        String key = normalize(name);

        if (stations.containsKey(key)) {
            return;
        }

        Station s = new Station(id, name, line);
        stations.put(key, s);
        graph.put(key, new ArrayList<>());
    }

    void addConnection(String from, String to, int weight) {
        String a = normalize(from);
        String b = normalize(to);

        if (!stations.containsKey(a) ||
            !stations.containsKey(b) || weight < 0) {
            System.out.println("Invalid connection!");
            return;
        }

        graph.get(a).add(new Edge(stations.get(b), weight));
        graph.get(b).add(new Edge(stations.get(a), weight));
    }

    void displayStations() {
        System.out.println("\n--- METRO STATIONS ---");

        for (Station s : stations.values()) {
            System.out.println(s.id + " - " + s);
        }
    }

    void displayConnections() {
        System.out.println("\n--- METRO CONNECTIONS ---");

        for (String key : graph.keySet()) {
            for (Edge e : graph.get(key)) {
                String from = stations.get(key).id;
                if (from.compareTo(e.destination.id) < 0) {
                    System.out.println(
                        stations.get(key).name + " <-> " +
                        e.destination.name + " : " +
                        e.weight + " minutes"
                    );
                }
            }
        }
    }

    void findShortestRoute(String source, String destination) {
        String src = normalize(source);
        String dest = normalize(destination);

        if (!stations.containsKey(src)) {
            System.out.println("Source station not found!");
            return;
        }

        if (!stations.containsKey(dest)) {
            System.out.println("Destination station not found!");
            return;
        }

        if (src.equals(dest)) {
            System.out.println("Route: " + stations.get(src).name);
            System.out.println("Total time: 0 minutes");
            return;
        }

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        PriorityQueue<Node> pq = new PriorityQueue<>();

        for (String key : stations.keySet()) {
            distance.put(key, Integer.MAX_VALUE);
        }

        distance.put(src, 0);
        pq.add(new Node(stations.get(src), 0));

        // Dijkstra's Algorithm
        while (!pq.isEmpty()) {
            Node current = pq.poll();

            String currentKey =
                normalize(current.station.name);

            if (current.distance != distance.get(currentKey)) {
                continue;
            }

            if (currentKey.equals(dest)) {
                break;
            }

            for (Edge edge : graph.get(currentKey)) {
                String next =
                    normalize(edge.destination.name);

                int newDistance =
                    current.distance + edge.weight;

                if (newDistance < distance.get(next)) {
                    distance.put(next, newDistance);
                    previous.put(next, currentKey);

                    pq.add(new Node(
                        edge.destination,
                        newDistance
                    ));
                }
            }
        }

        if (distance.get(dest) == Integer.MAX_VALUE) {
            System.out.println("No route available!");
            return;
        }

        List<Station> route = new ArrayList<>();

        String current = dest;

        while (current != null) {
            route.add(stations.get(current));
            current = previous.get(current);
        }

        Collections.reverse(route);

        System.out.println("\n===== SHORTEST ROUTE =====");

        for (int i = 0; i < route.size(); i++) {
            System.out.print(route.get(i).name);

            if (i < route.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println("\nTotal Travel Time: "
            + distance.get(dest) + " minutes");

        System.out.println("Total Stations: " + route.size());
    }
}

public class MetroRoutePlanner {

    static void loadMetroData(MetroGraph metro) {

        metro.addStation("M01", "Central", "Blue Line");
        metro.addStation("M02", "Museum", "Blue Line");
        metro.addStation("M03", "City Park", "Green Line");
        metro.addStation("M04", "University", "Green Line");
        metro.addStation("M05", "Airport", "Airport Line");
        metro.addStation("M06", "Old Town", "Blue Line");

        metro.addConnection("Central", "Museum", 4);
        metro.addConnection("Central", "City Park", 6);
        metro.addConnection("Museum", "University", 5);
        metro.addConnection("City Park", "University", 2);
        metro.addConnection("University", "Airport", 7);
        metro.addConnection("Museum", "Old Town", 3);
        metro.addConnection("Old Town", "Airport", 12);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        MetroGraph metro = new MetroGraph();

        loadMetroData(metro);

        while (true) {

            System.out.println("\n===== METRO ROUTE PLANNER =====");
            System.out.println("1. Find Shortest Route");
            System.out.println("2. Display All Stations");
            System.out.println("3. Display Connections");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");

            String input = sc.nextLine();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number!");
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.print("Enter Source Station: ");
                    String source = sc.nextLine();

                    System.out.print("Enter Destination Station: ");
                    String destination = sc.nextLine();

                    metro.findShortestRoute(source, destination);
                    break;

                case 2:
                    metro.displayStations();
                    break;

                case 3:
                    metro.displayConnections();
                    break;

                case 4:
                    System.out.println(
                        "Thank you for using Metro Route Planner!"
                    );
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}