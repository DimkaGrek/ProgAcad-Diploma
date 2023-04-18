package dimploma.rynok.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dimploma.rynok.dto.PaymentDTO;
import dimploma.rynok.model.Calculation;
import dimploma.rynok.model.Counter;
import dimploma.rynok.repo.CalculationRepository;
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
    private final CalculationRepository calculationRepository;

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
        if (totalAmount != 0) { // если сумма оплаты не равна нулю, тогда записываем оплату
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
        }


        // если пользователь выбрал учесть переплату, нужно списать ее с начислений, у которых она есть
        if (request.has("overpayment")) {
            System.out.println("Overpayment: " + request.get("overpayment").asText());
            Double overpayment = Double.parseDouble(request.get("overpayment").asText());

            JsonNode idsOverpayNode = request.get("idsOverpayment"); // получаем ids квитанций, содержащих переплату
            System.out.println("idsOverpayNode: " + idsOverpayNode);
            List<Long> idsList = new ArrayList<>();
            if (idsOverpayNode.isArray()) {
                for (JsonNode idNode : idsOverpayNode) {
                    if (idNode.isNumber()) {
                        idsList.add(idNode.asLong());
                    } else {
                        System.out.println("idNode is NOT Number");
                    }
                }
            } else if (idsOverpayNode.isNumber()) {
                idsList.add(idsOverpayNode.asLong());
            } else if (idsOverpayNode.isTextual()) {
                try {
                    idsList.add(Long.parseLong(idsOverpayNode.asText()));
                } catch (NumberFormatException e) {
                    // Обработка исключения: строка не может быть преобразована в число
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not correct ids of Calculations");
                }
            }
            else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not correct ids of Calculations");

            for (Long id : idsList) {
                Optional<Calculation> optionalCalculation = calculationRepository.findById(id);
                if (optionalCalculation.isPresent()) {
                    Calculation calculation = optionalCalculation.get();

                    List<PaymentDTO> payments = paymentService.getPaymentsByCalculation(id); // получаем оплаты по калькуляции
                    Double totalPayAmount = 0.0;
                    for (PaymentDTO payment : payments) { // формируем сумму всех оплат.
                        totalPayAmount += payment.getAmount();
                    }
                    Double debtOverpay = totalPayAmount - calculation.getAmount();
                    if (debtOverpay > 0) { // если переплата есть, тогда списуем ее

                        if (debtOverpay > overpayment) {
                            paymentService.addPayment(id,counterId, 0L, -overpayment, LocalDateTime.now(), "Use Overpayment");
                            paymentService.addPayment(calcArray[calcArray.length - 1], counterId, 0L, overpayment, LocalDateTime.now(), "Use Overpayment");
                            overpayment = 0.0;
                        }
                        else {
                            paymentService.addPayment(id,counterId, 0L, -debtOverpay, LocalDateTime.now(), "Use Overpayment");
                            paymentService.addPayment(calcArray[calcArray.length - 1], counterId, 0L, debtOverpay, LocalDateTime.now(), "Use Overpayment");
                            overpayment -= debtOverpay;
                        }
                    }
                }
                if (overpayment == 0.0) break;
            }
        }

        System.out.println("totalAmount end: " + totalAmount);
        System.out.println("calculation saved!!!");
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
