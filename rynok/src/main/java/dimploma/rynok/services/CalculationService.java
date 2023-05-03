package dimploma.rynok.services;

import dimploma.rynok.dto.CalculationDTO;
import dimploma.rynok.dto.CalculationPrintDTO;
import dimploma.rynok.dto.PaymentDTO;
import dimploma.rynok.model.*;
import dimploma.rynok.repo.CalculationRepository;
import dimploma.rynok.repo.CounterRepository;
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
public class CalculationService {

    private final CalculationRepository calculationRepository;

    private final CounterRepository counterRepository;

    private final PaymentService paymentService;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CalculationDTO> getCalculationByCounter(Long counterId) {
        Optional<Counter> optionalCounter = counterRepository.findById(counterId);
        if(optionalCounter.isPresent()) {
            Counter counter = optionalCounter.get();
            return counter.getCalculations().stream().map(calc -> new CalculationDTO(calc.getId(), calc.getMonth(), calc.getDate(),
                    (calc.getArendator() == null) ? counter.getArendator().getName() : calc.getArendator().getName(),
                    (calc.getArendator() == null) ? counter.getArendator().getSurname() : calc.getArendator().getSurname(), calc.getCountNow(), calc.getCountBefore(),
                    calc.getDifference(), calc.getRate(), calc.getAmount(), calc.getNotes(), getPayments(calc.getId(), calc.getAmount()))).collect(Collectors.toList());
        }
        return null;
    }

    // проверяем, оплачено ли начисление
    private Double getPayments(Long calcId, Double calcAmount) {
        List<PaymentDTO> payments = paymentService.getPaymentsByCalculation(calcId);
        if ( ! payments.isEmpty()) {
            for (PaymentDTO payment: payments) {
                calcAmount = calcAmount - payment.getAmount();
            }
            return calcAmount;
        }
        return null;
    }

    @Transactional
    public boolean addCalculation(Long counterId, String month, String year, Long countNow, Long countBefore, Double rate, LocalDateTime date, String notes) {
        Optional<Counter> counterOptional = counterRepository.findById(counterId);
        if(counterOptional.isPresent()) {
            Counter counter = counterOptional.get();
            Long difference = countNow - countBefore;
            Calculation calc = new Calculation(month+" "+year, countNow, countBefore, difference, rate, difference * rate, null, null, date, notes);
            calc.setCounter(counter);
            CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername()); // get current user
            calc.setUser(user);
            Arendator arendator = counter.getArendator();
            calc.setArendator(arendator);
            calculationRepository.save(calc);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updateCalculation(Long calcId, String month, String year, Long countNow, Long countBefore, Double rate, LocalDateTime date, String notes) {
        Optional<Calculation> optionalCalculation = calculationRepository.findById(calcId);
        if (optionalCalculation.isPresent()) {
            Calculation calc = optionalCalculation.get();
            calc.setMonth(month + " " + year);
            calc.setCountNow(countNow);
            calc.setCountBefore(countBefore);
            Long difference = countNow - countBefore;
            calc.setDifference(difference);
            calc.setAmount(difference * rate);
            calc.setRate(rate);
            calc.setDate(date);
            calc.setNotes(notes);
            calculationRepository.save(calc);
            return true;
        }
        return false;
    }

    @Transactional
    public List<CalculationPrintDTO> getCalculationsForMonthByCounters(List<Long> counterIds, String month) {
        List<Calculation> calculations = calculationRepository.getCalculationsForMonthByCounters(counterIds, month);
        return calculations.stream().map(calc -> new CalculationPrintDTO(calc.getId(), calc.getMonth(), calc.getDate(),
                (calc.getArendator() == null) ? calc.getCounter().getArendator().getName() : calc.getArendator().getName(),
                (calc.getArendator() == null) ? calc.getCounter().getArendator().getSurname() : calc.getArendator().getSurname(),
                calc.getCountNow(), calc.getCountBefore(), calc.getDifference(), calc.getRate(), calc.getAmount(), calc.getNotes(),
                getPayments(calc.getId(), calc.getAmount()), calc.getCounter().getPavilion(), calc.getCounter().getPlace(), calc.getCounter().getId(),
                        getTotalCalcAmount(calc.getCounter()), getTotalPayAmount(calc.getCounter())))
                .collect(Collectors.toList());

    }

    private Double getTotalCalcAmount(Counter counter) {
        List<Calculation> calculations = counter.getCalculations();
        Double totalAmount = 0.0;
        for (Calculation calculation : calculations) {
            totalAmount += calculation.getAmount();
        }
        return totalAmount;
    }

    private Double getTotalPayAmount(Counter counter) {
        List<Payment> payments = counter.getPayments();
        Double totalAmount = 0.0;
        for (Payment payment : payments) {
            totalAmount += payment.getAmount();
        }
        return totalAmount;
    }
}