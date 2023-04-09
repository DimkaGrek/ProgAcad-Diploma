package dimploma.rynok.services;

import dimploma.rynok.dto.ArendatorAllDataDTO;
import dimploma.rynok.dto.ArendatorDTO;
import dimploma.rynok.model.Arendator;
import dimploma.rynok.model.CustomUser;
import dimploma.rynok.repo.ArendatorRepository;
import dimploma.rynok.repo.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class ArendatorService {

    private final ArendatorRepository arendatorRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ArendatorDTO> getAllArendators() {
        List<Arendator> arendators = arendatorRepository.findAll();
        return arendators.stream().map(arendator -> new ArendatorDTO(arendator.getId(), arendator.getName(), arendator.getSurname(),
                arendator.getPhone1(), arendator.getEmail(), arendator.getNotes())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ArendatorAllDataDTO getArendatorById(Long id) {
        Optional<Arendator> optionalArendator = arendatorRepository.findById(id);
        if(optionalArendator.isPresent()) {
            Arendator arendator = optionalArendator.get();
            ArendatorAllDataDTO arendatorDTO = new ArendatorAllDataDTO(arendator.getId(), arendator.getName(),arendator.getSurname(), arendator.getEvidence(), arendator.getPassport(), arendator.getAddress(), arendator.getTypeCompany(), arendator.getPhone1(), arendator.getPhone2(), arendator.getEmail(), arendator.getNotes());
            return arendatorDTO;
        }
        return null;
    }

    @Transactional
    public boolean createArendator(String name, String surname, String evidence, String passport, String address,
                                   String typeCompany, String phone1, String phone2, String notes, String email) {
        CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername());
        Arendator arendator = new Arendator(name, surname, evidence, passport, address, typeCompany, phone1, phone2, notes, email);
        arendator.setUser(user);
        arendatorRepository.save(arendator);
        return true;
    }

    @Transactional
    public boolean updateArendator(Long id, String name, String surname, String evidence, String passport, String address,
                                   String typeCompany, String phone1, String phone2, String notes, String email) {
        Optional<Arendator> optionalArendator = arendatorRepository.findById(id);
        if (optionalArendator.isPresent()) {
            Arendator arendator = optionalArendator.get();
            arendator.setName(name);
            arendator.setSurname(surname);
            arendator.setEvidence(evidence);
            arendator.setPassport(passport);
            arendator.setAddress(address);
            arendator.setTypeCompany(typeCompany);
            arendator.setPhone1(phone1);
            arendator.setPhone2(phone2);
            arendator.setNotes(notes);
            arendator.setEmail(email);
            arendatorRepository.save(arendator);
            return true;
        }
        return false;
    }
}
