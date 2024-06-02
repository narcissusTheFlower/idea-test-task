import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

class TicketParser {

    /**
     * JSON file path.
     */
    private final Path filePath;

    /**
     * Ticket only of VVO to TLV.
     */
    private Collection<OneTicketDTO> parsedTickets;

    /**
     * Text representation of the answer.
     */
    private Result result;

    /**
     * Private constructor that does not allow usage of the "new" keyword.
     * @param filePath
     */
    private TicketParser(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Protected factory
     * @param filePath
     * @return new instance of this class.
     */
    static TicketParser of(Path filePath) {
        if (!filePath.toFile().exists()){
            System.out.println("File does not exist!");
            System.exit(1);
        }
        return new TicketParser(filePath);
    }

    /**
     * Parses only TLV to VVO tickets from the file
     * @return this instance.
     */
    TicketParser parseJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TicketsDTO dto = objectMapper.readValue(filePath.toFile(), TicketsDTO.class);
            parsedTickets = dto.getTickets()
                .stream()
                .filter(ticket -> ticket.getOrigin()
                    .equals("VVO") && ticket.getDestination()
                    .equals("TLV"))
                .sorted()
                .toList();
        } catch (IOException e) {
            System.out.println("Failed to parse the ticket.json file!");
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Calculates minimal price and difference between average price and the median one
     * @return this instance.
     */
    TicketParser calculate() {
        double priceAverage = parsedTickets.stream()
            .mapToDouble(OneTicketDTO::getPrice)
            .average()
            .orElse(0);
        double priceMedian = calculateMedian(parsedTickets.stream()
            .map(OneTicketDTO::getPrice)
            .toList());
        //For readability only. Variable could be removed
        double priceDifference = priceAverage - priceMedian;

        result = Result.from(calculateMinimalTimeForEachCarrier(parsedTickets),
            priceAverage,
            priceMedian,
            priceDifference);
        return this;
    }

    void printResults() {
        System.out.println(result);
    }

    /**
     * Get the median of the prices
     * @param values
     * @return median of the ticket prices
     */
    private double calculateMedian(List<Double> values) {
        if (values.size() % 2 != 0) {
            return values.get(values.size() / 2);
        }
        return (values.get(values.size() / 2) + values.get(values.size() / 2 - 1)) / 2;
    }

    /**
     * Calculate time difference between two dates in minutes.
     * @param departureTime
     * @param arrivalTime
     * @return minutes between two dates.
     */
    private long getTimeDifference(String departureTime, String arrivalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
        try {
            Date departureDate = sdf.parse(departureTime);
            Date arrivalDate = sdf.parse(arrivalTime);
            return (Duration.ofMillis(arrivalDate.getTime() - departureDate.getTime())
                .toMinutes());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates and assembles representation of minimal flight time for each distinct carrier.
     * @param parsedTickets
     * @return minimal flight time for each distinct carrier
     */
    private Map<String, String> calculateMinimalTimeForEachCarrier(Collection<OneTicketDTO> parsedTickets) {
        HashSet<AbstractMap.SimpleEntry<String, Collection<Long>>> temp = new HashSet<>();

        parsedTickets.forEach(ticket -> temp.add(new AbstractMap.SimpleEntry<>(ticket.getCarrier(), new ArrayList<Long>())));

        temp.forEach(distinctCarrier -> {
            parsedTickets.stream()
                .filter(ticket -> ticket.getCarrier()
                    .equals(distinctCarrier.getKey()))
                .forEach(filteredTicket -> distinctCarrier.getValue()
                    .add(getTimeDifference(filteredTicket.getDepartureDate() + " " + filteredTicket.getDepartureTime(),
                        filteredTicket.getArrivalDate() + " " + filteredTicket.getArrivalTime())));
        });
        Map<String, String> result = new HashMap<>();
        temp.forEach(pair -> {
            result.put(pair.getKey(), timeAnswerBuilder(pair.getValue()
                .stream()
                .sorted()
                .findFirst()
                .get())
            );
        });
        return result;
    }

    /**
     * Convenience method for String building.
     * @param minutes
     * @return readable String answer parsed from minutes.
     */
    private String timeAnswerBuilder(Long minutes) {
        return new StringBuilder()
            .append("(hours:" + minutes / 60)
            .append(" minutes:" + minutes % 60 + ")")
            .toString();
    }

    /**
     * Answer representation.
     */
    private static class Result {

        private Map<String, String> times;
        private double priceAverage;
        private double priceMedian;
        private double priceDifference;

        private Result(Map<String, String> times, double priceAverage, double priceMedian, double priceDifference) {
            this.times = times;
            this.priceAverage = priceAverage;
            this.priceMedian = priceMedian;
            this.priceDifference = priceDifference;
        }

        public static Result from(Map<String, String> times, double priceAverage, double priceMedian, double priceDifference) {
            return new Result(times, priceAverage, priceMedian, priceDifference);
        }

        @Override
        public String toString() {
            return new StringBuilder().append("Минимальное время полета между городами:")
                .append(times)
                .append("\n")
                .append("\n")
                .append("Значения цен полета между городами Владивосток и Тель-Авив")
                .append("\n")
                .append("Среднее значение цены за билет: ")
                .append(priceAverage)
                .append("\n")
                .append("Медиана цены: ")
                .append(priceMedian)
                .append("\n")
                .append("Разница среднего значения и медианы: ")
                .append(priceDifference)
                .toString();
        }
    }

}
