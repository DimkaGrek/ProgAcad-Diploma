package dimploma.rynok.controllers;

import dimploma.rynok.dto.CalculationDTO;
import dimploma.rynok.repo.CalculationRepository;
import dimploma.rynok.services.CalculationService;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class CalculationController {

    private final CalculationService calculationService;

    private final CalculationRepository calculationRepository;

    @GetMapping("/api/calculation/{counterId}")
    public List<CalculationDTO> getCalculationByCounter(@PathVariable Long counterId) {
        return calculationService.getCalculationByCounter(counterId);
    }

    @PostMapping("/api/calculations/{counterId}")
    public ResponseEntity<String> addCalculation(@PathVariable Long counterId, @RequestBody Map<String, String> request) {
        String month = request.get("month");
        String year = request.get("year");
        Long countNow = Long.parseLong(request.get("countNow"));
        Long countBefore = Long.parseLong(request.get("countBefore"));
        Double rate = Double.parseDouble(request.get("rate"));
        LocalDateTime date = LocalDateTime.parse(request.get("date"));
        String notes = request.get("notes");
        if (!calculationService.addCalculation(counterId, month, year, countNow, countBefore, rate, date, notes)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        System.out.println("calculation saved!!!");
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/api/calculations/update/{calcId}")
    public ResponseEntity<String> updateCalculation(@PathVariable Long calcId, @RequestBody Map<String, String> request) {
        String month = request.get("month");
        String year = request.get("year");
        Long countNow = Long.parseLong(request.get("countNow"));
        Long countBefore = Long.parseLong(request.get("countBefore"));
        Double rate = Double.parseDouble(request.get("rate"));
        LocalDateTime date = LocalDateTime.parse(request.get("date"));
        String notes = request.get("notes");
        if (!calculationService.updateCalculation(calcId, month, year, countNow, countBefore, rate, date, notes)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        System.out.println("calculation saved!!!");
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/api/calculations/delete/{calcIds}")
    public ResponseEntity<String> deleteCalculations(@PathVariable List<Long> calcIds) {
        for (Long id : calcIds) {
            try {
                calculationRepository.deleteById(id);
            } catch(EmptyResultDataAccessException e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Calculation does not find");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
