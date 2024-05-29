import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

class TicketParser {

    /**
     * Средняя цена для полета между владивостоком и тель-авивом =  (12400 + 13100 + 15300 + 11000 + 13400 + 12450 + 13600 + 14250 + 16700 + 17400) = 13960
     *
     * ("departure_date": "12.05.18" + "departure_time": "16:20") (epoch seconds) -  (epoch seconds) ("arrival_date": "12.05.18" +  "arrival_time": "22:10") = 8 часов 30 минут
     */
    private final Path filePath;

    private Collection<OneTicketDTO> parsedTickets;

    private Result result;

    private TicketParser(Path filePath) {
        this.filePath = filePath;
    }

    static TicketParser of(Path filePath) {
        return new TicketParser(filePath);
    }

    TicketParser parseJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TicketsDTO dto = objectMapper.readValue(filePath.toFile(), TicketsDTO.class);
            parsedTickets = dto.getTickets().stream()
                    .filter(ticket -> ticket.getOrigin().equals("VVO") && ticket.getDestination().equals("TLV"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            System.out.println("Failed to parse the ticket.json file!");
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Calculate minimal price and difference between average price and the median one
     * @return
     */
    TicketParser calculate() {
        double priceAverage = parsedTickets.stream().mapToDouble(OneTicketDTO::getPrice).average().orElse(0);
        double priceMedian = calculateMedian(parsedTickets.stream().map(OneTicketDTO::getPrice).toList());
        double priceDifference = Math.abs(priceAverage-priceMedian);
        Map<String,String> times= null; //COUNT TIMES
        result = Result.from(Collections.EMPTY_MAP,priceAverage,priceMedian,priceDifference);
        return this;
    }

    void printResults() {
        System.out.println(result);
    }

    //FINISH MEDIAN
    private <T extends Number> T calculateMedian(List<T> values){
        if (values.size() % 2 != 0){
            return (double) values.size() /2;
        }
//
//        Arrays.sort(numArray);
//        double median;
//        if (numArray.length % 2 == 0)
//            median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
//        else
//            median = (double) numArray[numArray.length/2];

        return (T) new Double(1);
    }

    private static class Result{

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

        public static Result from(Map<String, String> times, double priceAverage, double priceMedian, double priceDifference){
            return new Result(times,priceAverage,priceMedian,priceDifference);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                .append("Минимальное время полета между городами:")
                .append(times)
                .append("\n")
                .append("Среднее значение цены: ").append(priceAverage)
                .append("\n")
                .append("Медиана цены: ").append(priceMedian)
                .append("\n")
                .append("Разница среднего значения и медианы цены: ").append(priceDifference)
                .toString();
        }
    }


}
