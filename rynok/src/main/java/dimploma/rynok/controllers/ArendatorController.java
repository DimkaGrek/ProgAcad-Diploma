package dimploma.rynok.controllers;

import dimploma.rynok.dto.ArendatorAllDataDTO;
import dimploma.rynok.dto.ArendatorDTO;
import dimploma.rynok.services.ArendatorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ArendatorController {

    private final ArendatorService arendatorService;

    @GetMapping("/api/arendators")
    public List<ArendatorDTO> getAllArendators() {
        return arendatorService.getAllArendators();
    }

//    @PostMapping("/api/arendators")
//    public ResponseEntity<Void> createArendator(@RequestParam String name,
//                                                @RequestParam String surname,
//                                                @RequestParam String evidence,
//                                                @RequestParam String passport,
//                                                @RequestParam String address,
//                                                @RequestParam String typeCompany,
//                                                @RequestParam String phone1,
//                                                @RequestParam String phone2,
//                                                @RequestParam String notes,
//                                                @RequestParam String email) {
//        if ( !arendatorService.createArendator(name, surname, evidence, passport, address, typeCompany, phone1, phone2, notes, email)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body(null);
//    }

    @PostMapping("/api/arendators")
    public ResponseEntity<Void> createArendator(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String surname = request.get("surname");
        String evidence = request.get("evidence");
        String passport = request.get("passport");
        String address = request.get("address");
        String typeCompany = request.get("typeCompany");
        String phone1 = request.get("phone1");
        String phone2 = request.get("phone2");
        String notes = request.get("notes");
        String email = request.get("email");
        if ( !arendatorService.createArendator(name, surname, evidence, passport, address, typeCompany, phone1, phone2, notes, email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/api/arendators/update/{id}")
    public ResponseEntity<Void> updateArendator(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String surname = request.get("surname");
        String evidence = request.get("evidence");
        String passport = request.get("passport");
        String address = request.get("address");
        String typeCompany = request.get("typeCompany");
        String phone1 = request.get("phone1");
        String phone2 = request.get("phone2");
        String notes = request.get("notes");
        String email = request.get("email");
        if ( !arendatorService.updateArendator(id, name, surname, evidence, passport, address, typeCompany, phone1, phone2, notes, email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/api/arendators/{id}")
    public ArendatorAllDataDTO getArendatorById(@PathVariable Long id) { return arendatorService.getArendatorById(id);}
}
