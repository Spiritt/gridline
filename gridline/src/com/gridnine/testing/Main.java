package com.gridnine.testing;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

public class Main {

    public static void main(String[] args){
        Output.choice();
    }

}

class Output {
    public static void choice() {
        final Scanner in = new Scanner(System.in);
        final List<Flight> flights = FlightBuilder.createFlights();

        System.out.print("\n*** Input 0 to EXIT *** \nInput rule number (1-3): ");
        try {
            switch (in.nextInt()){
                case 1:
                    outputFlights(new Rule1().filter(flights));
                    break;
                case 2:
                    outputFlights(new Rule2().filter(flights));
                    break;
                case 3:
                    outputFlights(new Rule3().filter(flights));
                    break;
                case 0:
                    System.exit(0);
                    break;
                default :
                    throw new IllegalStateException("There is no such rule number!");
            }
        }catch (InputMismatchException e){
            System.out.println("You must input a number!");
            choice();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            choice();
        }
    }

    private static void outputFlights(List<Flight> flights){
        for (Flight flight : flights){
            System.out.println(flight);
        }
        choice();
    }
}


interface FlightFilter {
    List<Flight> filter(List<Flight> flights);
}

/**
 * Вылет до текущего момента времени.
 */
class Rule1 implements FlightFilter {
    @Override
    public List<Flight> filter(List<Flight> flights) {
        List<Flight> listFiltered = new ArrayList<>();
        for (Flight flight : flights){
            listFiltered.add(flight);
            for (Segment segment : flight.getSegments()){
                if (segment.getDepartureDate().isBefore(LocalDateTime.now())) {
                    listFiltered.remove(flight);
                }
            }
        }
        return listFiltered;
    }
}

/**
 * Имеются сегменты с датой прилёта раньше даты вылета.
 */
class Rule2 implements FlightFilter {
    @Override
    public List<Flight> filter(List<Flight> flights) {
        List<Flight> listFiltered = new ArrayList<>();
        for (Flight flight : flights){
            listFiltered.add(flight);
            for (Segment segment : flight.getSegments()){
                if (segment.getArrivalDate().isBefore(segment.getDepartureDate())) {
                    listFiltered.remove(flight);
                }
            }
        }
        return listFiltered;
    }
}

/**
 * Общее время, проведённое на земле превышает два часа (время на земле — это интервал между прилётом одного сегмента и вылетом следующего за ним).
 */
class Rule3 implements FlightFilter {
    @Override
    public List<Flight> filter(List<Flight> flights) {
        List<Flight> listFiltered = new ArrayList<>();
        for (Flight flight : flights) {
            listFiltered.add(flight);
            LocalDateTime lastArrival = null;
            long groundTime = 0;
            for (Segment segment : flight.getSegments()) {
                if (lastArrival != null) {
                    groundTime += Duration.between(lastArrival, segment.getDepartureDate()).toHours();
                    if (groundTime > 2) {
                        listFiltered.remove(flight);
                    }
                }
                lastArrival = segment.getArrivalDate();
            }
        }
        return listFiltered;
    }
}
