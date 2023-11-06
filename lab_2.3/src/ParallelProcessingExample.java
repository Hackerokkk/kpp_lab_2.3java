import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Container {
    private List<Route> routes;

    public Container(List<Route> routes) {
        this.routes = routes;
    }

    public synchronized List<Route> sortBySeats() {
        List<Route> sortedRoutes = new ArrayList<>(routes);
        Collections.sort(sortedRoutes, (r1, r2) -> r2.getAvailableSeats() - r1.getAvailableSeats());
        return sortedRoutes;
    }

    public synchronized List<Route> sortByDayOfWeek() {
        List<Route> sortedRoutes = new ArrayList<>(routes);
        Collections.sort(sortedRoutes, (r1, r2) -> r1.getDayOfWeek().compareTo(r2.getDayOfWeek()));
        return sortedRoutes;
    }

    public synchronized List<Route> sortByFlightNumber() {
        List<Route> sortedRoutes = new ArrayList<>(routes);
        Collections.sort(sortedRoutes, (r1, r2) -> r1.getFlightNumber() - r2.getFlightNumber());
        return sortedRoutes;
    }
}

class Route {
    private String stationName;
    private int availableSeats;
    private String dayOfWeek;
    private int flightNumber;

    public Route(String stationName, int availableSeats, String dayOfWeek, int flightNumber) {
        this.stationName = stationName;
        this.availableSeats = availableSeats;
        this.dayOfWeek = dayOfWeek;
        this.flightNumber = flightNumber;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public String getStationName() {
        return stationName;
    }
}


public class ParallelProcessingExample {
    public static void main(String[] args) {
        List<Route> routes = new ArrayList<>();
        // Додайте ваші дані про маршрути сюди
        routes.add(new Route("StationA", 100, "Monday", 101));
        routes.add(new Route("StationB", 80, "Tuesday", 102));
        routes.add(new Route("StationC", 120, "Wednesday", 103));
        routes.add(new Route("StationD", 90, "Thursday", 104));
        routes.add(new Route("StationE", 110, "Friday", 105));

        Container container = new Container(routes);

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        List<Route> resultBySeats = new ArrayList<>();
        List<Route> resultByDayOfWeek = new ArrayList<>();
        List<Route> resultByFlightNumber = new ArrayList();

        executorService.execute(() -> {
            resultBySeats.addAll(container.sortBySeats());
        });

        executorService.execute(() -> {
            resultByDayOfWeek.addAll(container.sortByDayOfWeek());
        });

        executorService.execute(() -> {
            resultByFlightNumber.addAll(container.sortByFlightNumber());
        });

        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Порівнюємо час паралельної обробки і послідовної обробки
        long startTime = System.nanoTime();
        List<Route> sequentialResultBySeats = container.sortBySeats();
        List<Route> sequentialResultByDayOfWeek = container.sortByDayOfWeek();
        List<Route> sequentialResultByFlightNumber = container.sortByFlightNumber();
        long endTime = System.nanoTime();
        long sequentialTime = endTime - startTime;

        System.out.println("Час послідовної обробки: " + sequentialTime + " наносекунд");

        // Обробка і виведення результатів паралельної обробки
        System.out.println("Результати сортування за кількістю місць (паралельно):");
        for (Route route : resultBySeats) {
            System.out.println(route.getStationName() + " - " + route.getAvailableSeats());
        }

        System.out.println("Результати сортування за днем тижня (паралельно):");
        for (Route route : resultByDayOfWeek) {
            System.out.println(route.getStationName() + " - " + route.getDayOfWeek());
        }

        System.out.println("Результати сортування за номером рейсу (паралельно):");
        for (Route route : resultByFlightNumber) {
            System.out.println(route.getStationName() + " - " + route.getFlightNumber());
        }
    }
}