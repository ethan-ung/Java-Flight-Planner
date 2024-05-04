import java.io.*;
import java.util.*;

class City {
    private String name;
    private LinkedList<Flight> flights;

    public City(String name) {
        this.name = name;
        this.flights = new LinkedList<>();
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public LinkedList<Flight> getFlights() {
        return flights;
    }

    public String getName() {
        return name;
    }
}

class Flight {
    private String source;
    private String destination;
    private double cost;
    private int timeCost;

    public Flight(String source, String destination, double cost, int timeCost) {
        this.source = source;
        this.destination = destination;
        this.cost = cost;
        this.timeCost = timeCost;
    }

    public String getSource() {
        return this.source;
    }

    public String getDestination() {
        return this.destination;
    }

    public double getCost() {
        return this.cost;
    }

    public int getTimeCost() {
        return this.timeCost;
    }

    @Override
    public String toString() {
        return "";
    }
}

class FlightDatabase {
    private LinkedList<City> cities;

    public FlightDatabase() {
        this.cities = new LinkedList<>();
    }

    public void addFlight(String source, String destination, double cost, int duration) {
        City sourceCity = getOrCreateCity(source);
        sourceCity.addFlight(new Flight(source, destination, cost, duration));

        City destCity = getOrCreateCity(destination);
    }

    public LinkedList<Flight> getFlightsFromCity(String cityName) {
        LinkedList<Flight> flights = new LinkedList<>();
        for (City city : cities) {
            if (city.getName().equals(cityName)) {
                flights.addAll(city.getFlights());
                break;
            }
        }
        return flights;
    }

    private City getOrCreateCity(String cityName) {
        for (City city : cities) {
            if (city.getName().equals(cityName)) {
                return city;
            }
        }
        City newCity = new City(cityName);
        cities.add(newCity);
        return newCity;
    }

    public int getNumCities() {
        return cities.size();
    }

    public LinkedList<Flight> getFlightsFromCityIndex(int cityIndex) {
        if (cityIndex >= 0 && cityIndex < cities.size()) {
            return cities.get(cityIndex).getFlights();
        }
        return new LinkedList<>(); // Return empty list if index is out of bounds
    }

    public LinkedList<String> getCityNames() {
        LinkedList<String> cityNames = new LinkedList<>();
        for (City city : cities) {
            cityNames.add(city.getName());
        }
        return cityNames;
    }
}

class DFS {
    private static FlightDatabase flightDatabase;
    private static List<List<Flight>> allPaths;

    public static List<List<Flight>> findAllPaths(FlightDatabase db, String source, String destination) {
        flightDatabase = db;
        allPaths = new ArrayList<>();
        int sourceIndex = getCityIndex(source);
        int destIndex = getCityIndex(destination);

        if (sourceIndex != -1 && destIndex != -1) {
            boolean[] visited = new boolean[flightDatabase.getNumCities()];
            Stack<PathState> stack = new Stack<>();
            stack.push(new PathState(sourceIndex));

            while (!stack.isEmpty()) {
                PathState currentState = stack.pop();
                visited[currentState.cityIndex] = true;

                if (currentState.cityIndex == destIndex) {
                    allPaths.add(new ArrayList<>(currentState.path));
                    visited[currentState.cityIndex] = false; // Backtrack
                } else {
                    LinkedList<Flight> flights = flightDatabase.getFlightsFromCityIndex(currentState.cityIndex);

                    for (Flight flight : flights) {
                        int nextCityIndex = getCityIndex(flight.getDestination());

                        if (nextCityIndex != -1 && !visited[nextCityIndex]) {
                            LinkedList<Flight> newPath = new LinkedList<>(currentState.path);
                            newPath.add(flight);
                            stack.push(new PathState(nextCityIndex, newPath));
                        }
                    }
                }
            }
        }
        return allPaths;
    }

    private static int getCityIndex(String cityName) {
        LinkedList<String> cityNames = flightDatabase.getCityNames();
        for (int i = 0; i < cityNames.size(); i++) {
            if (cityNames.get(i).equals(cityName)) {
                return i;
            }
        }
        return -1; // City not found
    }

    // State class to keep track of current city and path
    private static class PathState {
        int cityIndex;
        LinkedList<Flight> path;

        PathState(int cityIndex) {
            this.cityIndex = cityIndex;
            this.path = new LinkedList<>();
        }

        PathState(int cityIndex, LinkedList<Flight> path) {
            this.cityIndex = cityIndex;
            this.path = path;
        }
    }
}

public class Flight_Planner {
    public static void main(String[] args) {
        FlightDatabase flightDatabase = new FlightDatabase();

        try {
            File file = new File("flight_data.dat");
            Scanner fileScanner = new Scanner(file);
            int numberOfFlights = Integer.parseInt(fileScanner.nextLine());

            // Reading flight data and populating the FlightDatabase
            for (int i = 0; i < numberOfFlights; i++) {
                String[] arrStrings = fileScanner.nextLine().split("\\|");
                String source = arrStrings[0];
                String destination = arrStrings[1];
                double cost = Double.parseDouble(arrStrings[2]);
                int timeCost = Integer.parseInt(arrStrings[3]);

                flightDatabase.addFlight(source, destination, cost, timeCost);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return; // Exit if file not found
        }

        try {
            File file = new File("flight_requests.dat");
            Scanner fileScanner = new Scanner(file);
            int numberOfRequests = Integer.parseInt(fileScanner.nextLine());

            // Reading flight data and populating the FlightDatabase
            for (int i = 0; i < numberOfRequests; i++) {
                String[] arrStrings = fileScanner.nextLine().split("\\|");
                // Now we have the flight database populated, let's plan some flights
                String sourceCity = arrStrings[0]; // Replace with actual source city name
                String destinationCity = arrStrings[1]; // Replace with actual destination city name
                char preference = arrStrings[2].charAt(0); // 'C' for cost, 'T' for time

                List<List<Flight>> paths = DFS.findAllPaths(flightDatabase, sourceCity, destinationCity);

                System.out.println("Paths from " + sourceCity + " to " + destinationCity + ":");
                int pathCount = 0;
                for (List<Flight> path : paths) {
                    if (pathCount >= 3) {
                        break; // Only show up to 3 paths
                    }

                    pathCount++;
                    System.out.println("Path " + pathCount + ":");
                    double totalCost = 0;
                    int totalTime = 0;

                    for (Flight flight : path) {
                        System.out.println(flight.getSource() + " -> " + flight.getDestination() + " (Cost: $" + flight.getCost() + ", Time: " + flight.getTimeCost() + " hrs)");
                        totalCost += flight.getCost();
                        totalTime += flight.getTimeCost();
                    }

                    System.out.println("Total Cost: $" + totalCost + ", Total Time: " + totalTime + " hrs");
                    System.out.println(); // Blank line for separation
                }

                if (paths.isEmpty()) {
                    System.out.println("No paths found from " + sourceCity + " to " + destinationCity);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return; // Exit if file not found
        }

    }
}
