import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketsDTO {

    private final Set<OneTicketDTO> tickets;

    public TicketsDTO(@JsonProperty("tickets") Set<OneTicketDTO> tickets) {
        this.tickets = tickets;
    }

    public Set<OneTicketDTO> getTickets() {
        return tickets;
    }


}
