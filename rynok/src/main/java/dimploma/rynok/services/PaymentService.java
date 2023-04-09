package dimploma.rynok.services;

import dimploma.rynok.dto.PaymentDTO;
import dimploma.rynok.model.Calculation;
import dimploma.rynok.model.Counter;
import dimploma.rynok.model.CustomUser;
import dimploma.rynok.model.Payment;
import dimploma.rynok.repo.CalculationRepository;
import dimploma.rynok.repo.CounterRepository;
import dimploma.rynok.repo.PaymentRepository;
import dimploma.rynok.repo.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final CalculationRepository calculationRepository;

    private final CounterRepository counterRepository;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByCalculation(Long calcId) {
        Optional<Calculation> optionalCalculation = calculationRepository.findById(calcId);
        if(optionalCalculation.isPresent()) {
            Calculation calc = optionalCalculation.get();
            return calc.getPayments().stream().map(payment -> new PaymentDTO(payment.getId(), payment.getReceiptId(),
                    payment.getAmount(), payment.getDate(), payment.getNotes())).collect(Collectors.toList());
        }
        return null;
    }

    @Transactional
    public boolean addPayment(Long calcId, Long counterId, Long receiptId, Double amount, LocalDateTime date, String notes) {
        Optional<Calculation> optionalCalculation = calculationRepository.findById(calcId);
        if(optionalCalculation.isPresent()) {
            Calculation calc = optionalCalculation.get();
            Payment payment = new Payment(receiptId, amount, null, null, date, notes);
            payment.setCalculation(calc);

            Optional<Counter> optionalCounter = counterRepository.findById(counterId);
            Counter counter = optionalCounter.get();
            payment.setCounter(counter);

            CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername()); // get current user
            payment.setUser(user);

            paymentRepository.save(payment);
            return true;
        }
        return false;
    }
}
