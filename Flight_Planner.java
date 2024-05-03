import java.io.*;
import java.util.*;
import java.io.FileNotFoundException;

class Flight {
    private String source;
    private String destination;
    private int cost;
    private int timeCost;

    public Flight(String source, String destination, int cost, int timeCost) {
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

    @Override
    public String toString() {
        return "";
    }
}

class FlightPlan {
    private String source;
    private String destination;
    private String type;

    public FlightPlan(String source, String destination, String type) {
        this.source = source;
        this.destination = destination;
        this.type = type;
    }

    public String getSource() {
        return this.source;
    }

    public String getDestination() {
        return this.destination;
    }

    @Override
    public String toString() {
        return "";
    }
}

class FlightMap {
    private LinkedList<LinkedList<Flight>> flightMap;

    public FlightMap() {
        flightMap = new LinkedList<>();
    }

    public void updateMap(Flight flight) {
        String source = flight.getSource();
        boolean added = false;

        for (LinkedList<Flight> s : flightMap) {
            if(!s.isEmpty()) {
                String existingSource = s.getFirst().getSource();
                if(existingSource.equals(source)) {
                    s.add(flight);
                    added = true;
                    break;
                }
            }
        }

        if(!added) {
            LinkedList<Flight> newSource = new LinkedList<>();
            newSource.add(flight);
            flightMap.add(newSource);
            added = true;
        }
    }

    @Override
    public String toString() {
        return "";
    }
}

class FlightSolution {
    private String[] solutions;

    public FlightSolution() {
        solutions = new String[3];
    }

    public void findShortestPath(FlightMap flightMap, FlightPlan flightPlan) {
    }
}

public class Flight_Planner {
    public static void main (String[] args) {
        FlightMap flightMap = new FlightMap();

        try {
            File file = new File("flight_data.dat");
            Scanner fileScanner = new Scanner(file);
            int numberOfFlights = Integer.parseInt(fileScanner.nextLine());

            for(int i = 0; i < numberOfFlights; i++) {
                String[] arrStrings = fileScanner.nextLine().split("\\|");
                Flight flight = new Flight(arrStrings[0], 
                arrStrings[1], 
                Integer.parseInt(arrStrings[2]), 
                Integer.parseInt(arrStrings[3]));
                flightMap.updateMap(flight);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}