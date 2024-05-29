import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class OneTicketDTO implements Comparable<OneTicketDTO>{

    private final String origin;

    private final String originName;

    private final String destination;

    private final String destinationName;

    private final String departureDate;

    private final String departureTime;

    private final String arrivalDate;

    private final String arrivalTime;

    private final String carrier;

    private final int stops;

    private final double price;

    @JsonCreator
    public OneTicketDTO( @JsonProperty("origin") String origin,
        @JsonProperty("origin_name") String originName,
        @JsonProperty("destination") String destination,
        @JsonProperty("destination_name") String destinationName,
        @JsonProperty("departure_date") String departureDate,
        @JsonProperty("departure_time") String departureTime,
        @JsonProperty("arrival_date") String arrivalDate,
        @JsonProperty("arrival_time") String arrivalTime,
        @JsonProperty("carrier") String carrier,
        @JsonProperty("stops")int stops,
        @JsonProperty("price") double price) {
        this.origin = origin;
        this.originName = originName;
        this.destination = destination;
        this.destinationName = destinationName;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.carrier = carrier;
        this.stops = stops;
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public String getOriginName() {
        return originName;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getCarrier() {
        return carrier;
    }

    public int getStops() {
        return stops;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "TicketDTO{" + "origin='" + origin + '\'' + ", originName='" + originName + '\'' + ", destination='" + destination + '\'' +
            ", destinationName='" + destinationName + '\'' + ", departureDate='" + departureDate + '\'' + ", departureTime='" + departureTime + '\'' +
            ", arrivalDate='" + arrivalDate + '\'' + ", arrivalTime='" + arrivalTime + '\'' + ", carrier='" + carrier + '\'' + ", stops=" + stops + ", price=" +
            price + '}';
    }


    @Override
    public int compareTo(OneTicketDTO oneTicketDTO) {
        return Double.compare(oneTicketDTO.getPrice(), this.getPrice());
    }
}
