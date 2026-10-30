package br.com.fiap.ticketservice.ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RestTemplate restTemplate;
    public record PaymentResponse(String status) {}

    public TicketService(TicketRepository ticketRepository, RestTemplate restTemplate) {
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void purchaseTicket(Long ticketId){
        var ticket = ticketRepository.findByIdForUpdate(ticketId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado")
        );

        if (ticket.getAvailableTickets() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingressos esgotados");
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}

        //processar o pagamento
        PaymentResponse response = restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                null,
                PaymentResponse.class
        );

        if (response != null){
            log.info("Pagamento processado com sucesso: ");
        }

        ticket.setAvailableTickets(ticket.getAvailableTickets() - 1);

        ticketRepository.save(ticket);
    }

}
