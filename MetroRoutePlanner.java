import java.util.*;

public class MetroRoutePlanner {

    // Edge: a direct connection to a neighbouring station
    static class Edge {
        String to;
        int weight; // travel time in minutes
        Edge(String to, int weight) { this.to = to; this.weight = weight; }
    }

    // MetroGraph: adjacency list (station -> neighbours)
    static class MetroGraph {
        Map<String, List<Edge>> adj = new HashMap<>();

        void addStation(String s) { adj.putIfAbsent(s, new ArrayList<>()); }

        void addConnection(String a, String b, int w) {
            addStation(a);
            addStation(b);
            adj.get(a).add(new Edge(b, w)); // bidirectional
            adj.get(b).add(new Edge(a, w));
        }

        boolean hasStation(String s) { return adj.containsKey(s); }
    }

    // RouteService: Dijkstra's shortest path
    static class RouteService {
        MetroGraph graph;
        RouteService(MetroGraph graph) { this.graph = graph; }

        // Returns [route list, total weight] or null if no route
        Object[] findRoute(String source, String dest) {
            Map<String, Integer> dist = new HashMap<>();
            Map<String, String> prev = new HashMap<>();
            for (String s : graph.adj.keySet()) dist.put(s, Integer.MAX_VALUE);
            dist.put(source, 0);

            PriorityQueue<Object[]> pq =
                new PriorityQueue<>((x, y) -> Integer.compare((int) x[1], (int) y[1]));
            pq.add(new Object[]{source, 0});

            while (!pq.isEmpty()) {
                Object[] cur = pq.poll();
                String u = (String) cur[0];
                int d = (int) cur[1];
                if (d > dist.get(u)) continue;      // outdated entry
                if (u.equals(dest)) break;

                for (Edge e : graph.adj.get(u)) {   // relax edges
                    int nd = d + e.weight;
                    if (nd < dist.get(e.to)) {
                        dist.put(e.to, nd);
                        prev.put(e.to, u);
                        pq.add(new Object[]{e.to, nd});
                    }
                }
            }

            if (dist.get(dest) == Integer.MAX_VALUE) return null; // no path

            LinkedList<String> route = new LinkedList<>();
            for (String at = dest; at != null; at = prev.get(at)) route.addFirst(at);
            return new Object[]{route, dist.get(dest)};
        }
    }

    @SuppressWarnings("unchecked")
    static void plan(RouteService service, String src, String dst) {
        System.out.println("\n" + src + " -> " + dst);
        if (src == null || dst == null || src.isBlank() || dst.isBlank()
                || !service.graph.hasStation(src) || !service.graph.hasStation(dst)) {
            System.out.println("Please select valid stations.");
            return;
        }
        if (src.equals(dst)) {
            System.out.println("Route: " + src + "\nTotal: 0 minutes");
            return;
        }
        Object[] result = service.findRoute(src, dst);
        if (result == null) {
            System.out.println("No route available between the selected stations.");
        } else {
            System.out.println("Route: " + String.join(" -> ", (List<String>) result[0]));
            System.out.println("Total: " + result[1] + " minutes");
        }
    }

    public static void main(String[] args) {
        MetroGraph g = new MetroGraph();
        g.addConnection("Central", "Museum", 4);
        g.addConnection("Central", "Stadium", 6);
        g.addConnection("Museum", "City Park", 5);
        g.addConnection("Stadium", "City Park", 2);
        g.addConnection("City Park", "Airport", 7);
        g.addConnection("Museum", "Airport", 15);
        g.addStation("Isolated"); // for the "no route" test

        RouteService service = new RouteService(g);

        plan(service, "Central", "Airport");   // valid
        plan(service, "Central", "Central");   // same station
        plan(service, "Central", "Nowhere");   // unknown station
        plan(service, "Central", "Isolated");  // no path
    }
}
