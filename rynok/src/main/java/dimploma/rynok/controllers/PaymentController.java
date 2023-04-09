package dimploma.rynok.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dimploma.rynok.dto.PaymentDTO;
import dimploma.rynok.model.Counter;
import dimploma.rynok.repo.CounterRepository;
import dimploma.rynok.repo.PaymentRepository;
import dimploma.rynok.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final CounterRepository counterRepository;

    @GetMapping("/api/payments/{calcId}")
    public List<PaymentDTO> getPaymentsByCalculation(@PathVariable Long calcId) {
        return paymentService.getPaymentsByCalculation(calcId);
    }

    @GetMapping("/api/payments/sum/{counterId}") // получить сумму всех оплат по счетчику
    public Double getSumPaymentsByCounter(@PathVariable Long counterId) {
        Optional<Counter> counterOptional = counterRepository.findById(counterId);
        if(counterOptional.isPresent()) {
            Counter counter = counterOptional.get();
            return paymentRepository.getSumPaymentsByCounter(counter);
        }
        return null;
    }

    @PostMapping("/api/payments/{calcIds}")
    public ResponseEntity<String> addPayment(@PathVariable List<Long> calcIds, @RequestBody JsonNode request) {
        Long counterId = Long.parseLong(request.get("counterId").asText());
        Long receiptId = Long.parseLong(request.get("receiptId").asText());
        Double totalAmount = Double.parseDouble(request.get("amount").asText());
        LocalDateTime date = LocalDateTime.parse(request.get("date").asText());
        String notes = request.get("notes").asText();

        JsonNode amountsNode = request.get("amountsPay");
        System.out.println("amountsNode: " + amountsNode);
        List<Double> amountsList = new ArrayList<>();

        if (amountsNode.isArray()) { // если приняли массив сумм квитанций
            for (JsonNode amountNode : amountsNode) {
                if (amountNode.isNumber()) {
                    amountsList.add(amountNode.asDouble());
                } else {
                    System.out.println("amountNode is NOT Number");
                }
            }
        } else if (amountsNode.isNumber()) { // если приняли только одну квитанцию
            amountsList.add(amountsNode.asDouble());
        } else if (amountsNode.isTextual()) {
            try {
                amountsList.add(Double.parseDouble(amountsNode.asText()));
            } catch (NumberFormatException e) {
                // Обработка исключения: строка не может быть преобразована в число
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not correct amount of Calculation");
            }
        }
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not correct amount of Calculation");

        System.out.println("amountsList: " + amountsList);
        System.out.println(calcIds);
        Long[] calcArray = calcIds.toArray(new Long[0]);
        Double[] amountsArray = amountsList.toArray(new Double[0]);
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        System.out.println("totalAmount start: " + totalAmount);
        for (int i=0; i < calcArray.length; i++) {
            Double amount = 0.0;
            if (i == calcArray.length - 1 && totalAmount > amountsArray[i]) {
                amount = totalAmount;
            } else {
                amount = totalAmount > amountsArray[i] ? amountsArray[i] : totalAmount;
            }

            if ( !paymentService.addPayment(calcArray[i], counterId, receiptId, amount, date, notes)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            totalAmount -= amountsArray[i];
        }
        System.out.println("totalAmount end: " + totalAmount);
        System.out.println("calculation saved!!!");
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
