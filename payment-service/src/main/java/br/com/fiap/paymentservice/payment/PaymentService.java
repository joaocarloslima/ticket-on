package br.com.fiap.paymentservice.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class PaymentService {

    private Random random = new Random();

    @Retryable(
            includes = RuntimeException.class,
            maxRetries = 5,
            delay = 500,
            jitter = 50,
            multiplier = 2,
            maxDelay = 8_000
    )
    public String processPayment() {
        var randomValue = random.nextInt(100);

        if(randomValue < 70){
            log.info("🔴 Pagamento falhou");
            throw new RuntimeException("Pagamento falhou");
        }

        log.info("🟢 Pagamento realizado com sucesso");
        return "Pagamento realizado com sucesso";

    }

}
