package br.com.fiap.ticketservice.ticket;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public record PurchaseResponse(String status) {}

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/purchase/{ticketId}")
    public ResponseEntity<PurchaseResponse> purchaseTicket(@PathVariable Long ticketId) {
        try{
            ticketService.purchaseTicket(ticketId);
            return ResponseEntity.ok(new PurchaseResponse("Compra realizada com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new PurchaseResponse("Erro ao realizar a compra: " + e.getMessage())
            );
        }

    }

}
