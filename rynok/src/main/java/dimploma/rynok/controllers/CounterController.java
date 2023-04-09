package dimploma.rynok.controllers;

import dimploma.rynok.dto.CounterAllDataDTO;
import dimploma.rynok.dto.CounterDTO;
import dimploma.rynok.repo.CounterRepository;
import dimploma.rynok.services.CounterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class CounterController {
    private final CounterService counterService;

    private final CounterRepository counterRepository;

    @GetMapping("/api/counters")
    public List<CounterDTO> getAllCounters(){
        return counterService.getAllCounters();
    }

    @GetMapping("/api/counters/{id}")
    public CounterAllDataDTO getCounterById(@PathVariable Long id) { return counterService.getCounterById(id);}

    @PostMapping("/api/counters")
    public ResponseEntity<String> createCounter(@RequestBody Map<String, String> request) {
        String pavilion = request.get("pavilion");
        String place = request.get("place");
        String type = request.get("type");
        Long number = Long.parseLong(request.get("number"));
        Long atomicity = Long.parseLong(request.get("capacity"));
        LocalDate dateManufac = LocalDate.parse(request.get("dateManufac"), DateTimeFormatter.ISO_DATE);
        LocalDate dateInstall = LocalDate.parse(request.get("dateInstall"), DateTimeFormatter.ISO_DATE);
        Long arendator = Long.parseLong(request.get("arendator"));
        String notes = request.get("notes");
        Long parent = (request.get("parent") != null && !request.get("parent").isEmpty()) ? Long.parseLong(request.get("parent")) : null;
        System.out.println("API/newCOUNTER");
//        Counter entity = new Counter(pavilion, place, type, number, atomicity, dateManufac, dateInstall, notes);
//        counterService.save(entity);
        Long counterId = counterService.addCounter(pavilion, place, type, number, atomicity, dateManufac, dateInstall, notes, arendator, parent);
        if ( counterId == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("number exist");
        }
        System.out.println("Counter ID: " + counterId);
        if ( !counterService.addCountersToUser(counterService.getIdByCurrentUser(), Collections.singletonList(counterId))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        System.out.println("counter saved!!!");
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/api/counters/update/{counterId}")
    public ResponseEntity<String> updateCounter(@PathVariable Long counterId, @RequestBody Map<String, String> request) {
        String pavilion = request.get("pavilion");
        String place = request.get("place");
        String type = request.get("type");
        Long number = Long.parseLong(request.get("number"));
        Long atomicity = Long.parseLong(request.get("capacity"));
        LocalDate dateManufac = LocalDate.parse(request.get("dateManufac"), DateTimeFormatter.ISO_DATE);
        LocalDate dateInstall = LocalDate.parse(request.get("dateInstall"), DateTimeFormatter.ISO_DATE);
        Long arendator = Long.parseLong(request.get("arendator"));
        String notes = request.get("notes");
        Long parent = (request.get("parent") != null && !request.get("parent").isEmpty()) ? Long.parseLong(request.get("parent")) : null;
        System.out.println("API/updateCOUNTER");

        if ( !counterService.updateCounter(counterId, pavilion, place, type, number, atomicity, dateManufac, dateInstall, notes, arendator, parent)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        System.out.println("counter saved!!!");
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/api/counters/{userid}")
    public ResponseEntity<Void> addCountersToUser(@PathVariable Long userid, @RequestParam List<Long> counterIds) {
        if ( ! counterService.addCountersToUser(userid, counterIds)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/api/counters/parent/{id}") // получаем все счетчики, у которых parent_id = id
    public List<CounterDTO> getCountersByParent(@PathVariable Long id) {
        return counterService.getCountersByParent(id);
    }

    @PostMapping("/api/counters/delete/{counterIds}")
    public ResponseEntity<Void> deleteCounters(@PathVariable List<Long> counterIds) {
        if ( ! counterService.deleteCounters(counterIds)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/api/counters/undelete/{counterIds}")
    public ResponseEntity<Void> unDeleteCounters(@PathVariable List<Long> counterIds) {
        if ( ! counterService.unDeleteCounters(counterIds)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/api/counters/delete") // получаем удаленные счетчики
    public List<CounterDTO> getDeletedCounters() {return counterService.getDeletedCounters();}

    @GetMapping("/api/counters/delCount") // получаем количество удаленных счетчиков
    public Long getCountDeletedCounters() {
        return counterRepository.getCountDeletedCounters();
    }
}
