import java.io.*;
import java.util.*;
import java.io.FileNotFoundException;

class City {
    private String name;
    private List<Flight> flights;

    public City(String name) {
        this.name = name;
        this.flights = new ArrayList<>();
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public List<Flight> getFlights() {
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

class FlightPlan {
    private String source;
    private String destination;
    private char preference; // 'T' for time, 'C' for cost

    public FlightPlan(String source, String destination, char preference) {
        this.source = source;
        this.destination = destination;
        this.preference = preference;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public char getPreference() {
        return preference;
    }
}

class FlightDatabase {
    private List<City> cities;

    public FlightDatabase() {
        this.cities = new ArrayList<>();
    }

    public void addFlight(String source, String destination, double cost, int duration) {
        City sourceCity = getOrCreateCity(source);
        sourceCity.addFlight(new Flight(source, destination, cost, duration));

        City destCity = getOrCreateCity(destination);
    }

    public List<Flight> getFlightsFromCity(String cityName) {
        List<Flight> flights = new ArrayList<>();
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

    public List<Flight> getFlightsFromCityIndex(int cityIndex) {
        if (cityIndex >= 0 && cityIndex < cities.size()) {
            return cities.get(cityIndex).getFlights();
        }
        return new ArrayList<>(); // Return empty list if index is out of bounds
    }

    public List<String> getCityNames() {
        List<String> cityNames = new ArrayList<>();
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
            List<Flight> currentPath = new ArrayList<>();
            dfs(sourceIndex, destIndex, visited, currentPath);
        }
        return allPaths;
    }

    private static void dfs(int currentCityIndex, int destIndex, boolean[] visited, List<Flight> currentPath) {
        visited[currentCityIndex] = true;
        if (currentCityIndex == destIndex) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            List<Flight> flights = flightDatabase.getFlightsFromCityIndex(currentCityIndex);
            for (Flight flight : flights) {
                int nextCityIndex = getCityIndex(flight.getDestination());
                if (nextCityIndex != -1 && !visited[nextCityIndex]) {
                    currentPath.add(flight);
                    dfs(nextCityIndex, destIndex, visited, currentPath);
                    currentPath.remove(currentPath.size() - 1);
                }
            }
        }
        visited[currentCityIndex] = false;
    }

    private static int getCityIndex(String cityName) {
        List<String> cityNames = flightDatabase.getCityNames();
        for (int i = 0; i < cityNames.size(); i++) {
            if (cityNames.get(i).equals(cityName)) {
                return i;
            }
        }
        return -1; // City not found
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

        // Now we have the flight database populated, let's plan some flights
        String sourceCity = "Dallas"; // Replace with actual source city name
        String destinationCity = "Houston"; // Replace with actual destination city name
        char preference = 'C'; // 'C' for cost, 'T' for time

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
}