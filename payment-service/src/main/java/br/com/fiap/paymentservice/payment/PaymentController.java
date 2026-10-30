package br.com.fiap.paymentservice.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public record PaymentResponse(String status) {}

    @PostMapping
    public PaymentResponse processPayment() {
        String payment = paymentService.processPayment();
        return new PaymentResponse(payment);
    }

}
