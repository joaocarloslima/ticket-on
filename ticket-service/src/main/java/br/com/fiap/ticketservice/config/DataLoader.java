package br.com.fiap.ticketservice.config;

import br.com.fiap.ticketservice.ticket.Ticket;
import br.com.fiap.ticketservice.ticket.TicketRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;

@Configuration
public class DataLoader {

    private final TicketRepository ticketRepository;

    public DataLoader(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @PostConstruct
    public void init(){
        ticketRepository.save(new Ticket(
                null,
                "Show do Coldplay",
                10
        ));
    }

}
