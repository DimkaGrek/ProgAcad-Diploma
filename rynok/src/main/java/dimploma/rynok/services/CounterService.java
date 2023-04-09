package dimploma.rynok.services;

import dimploma.rynok.dto.CounterAllDataDTO;
import dimploma.rynok.dto.CounterDTO;
import dimploma.rynok.model.Arendator;
import dimploma.rynok.model.Counter;
import dimploma.rynok.model.CustomUser;
import dimploma.rynok.repo.ArendatorRepository;
import dimploma.rynok.repo.CounterRepository;
import dimploma.rynok.repo.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
public class CounterService {

    private final CounterRepository counterRepository;
    private final UserRepository userRepository;
    private final ArendatorRepository arendatorRepository;

    @Transactional(readOnly = true)
    public List<CounterDTO> getAllCounters() {
        CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername());

        List<Counter> counters = user.getCounters();
        // убираем из списка полученных счетчиков все, у которых поле delete_data не null
        List<Counter> countersWithoutDeleted = new ArrayList<>();
        for (Counter counter : counters) {
            if (counter.getDeleteDate() == null) { countersWithoutDeleted.add(counter); }
        }

        return countersWithoutDeleted.stream().map(counter -> new CounterDTO(counter.getId(), counter.getPavilion(), counter.getPlace(),
                counter.getNumber(), counter.getArendator().getSurname(), counter.getArendator().getName(), counter.getNotes())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CounterAllDataDTO getCounterById(Long id) {
        Optional<Counter> optionalCounter = counterRepository.findById(id);
        if(optionalCounter.isPresent()) {
            Counter counter = optionalCounter.get();
            CounterAllDataDTO counterDTO = new CounterAllDataDTO(counter.getId(), counter.getPavilion(), counter.getPlace(), counter.getType(),
                    counter.getNumber(), counter.getAtomicity(), counter.getDateManufac(), counter.getDateInstall(), counter.getParent(), counter.getArendator().getSurname(), counter.getArendator().getName(), counter.getNotes());
            return counterDTO;
        }
        return null;
    }

    @Transactional
    public Long addCounter(String pavilion, String place, String type, Long number, Long atomicity, LocalDate dateManufac,
                              LocalDate dateInstall, String notes, Long arendatorId, Long parent) {
        if (!counterRepository.existsByNumber(number)) {
            Counter counter = new Counter(pavilion, place, type, number, atomicity, dateManufac, dateInstall, notes, parent);
            Optional<Arendator> arendatorOptional = arendatorRepository.findById(arendatorId);
            if (arendatorOptional.isPresent()) {
                counter.setArendator(arendatorOptional.get());
                CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername());
                System.out.println("login: " + user.getLogin());


                //System.out.println(counter.getUsers());
                System.out.println("counter id:" + counter.getId());
                //user.setCounters(Collections.singletonList(counter));
                counterRepository.save(counter);
                System.out.println("counter id:" + counter.getId());

                return counter.getId();
            }
        }
        return null;
    }

    @Transactional
    public boolean updateCounter(Long counterId, String pavilion, String place, String type, Long number, Long atomicity, LocalDate dateManufac,
                                 LocalDate dateInstall, String notes, Long arendatorId, Long parent) {
        Optional<Counter> optionalCounter = counterRepository.findById(counterId);
        if(optionalCounter.isPresent()) {
            Counter counter = optionalCounter.get();
            counter.setPavilion(pavilion);
            counter.setPlace(place);
            counter.setType(type);
            counter.setNumber(number);
            counter.setAtomicity(atomicity);
            counter.setDateManufac(dateManufac);
            counter.setDateInstall(dateInstall);
            counter.setNotes(notes);
            counter.setParent(parent);
            Optional<Arendator> arendatorOptional = arendatorRepository.findById(arendatorId);
            if(arendatorOptional.isPresent()) {
                Arendator arendator = arendatorOptional.get();
                counter.setArendator(arendator);
                counterRepository.save(counter);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean addCountersToUser(Long userId, List<Long> counterIds) {
        Optional<CustomUser> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            CustomUser user = userOptional.get();
//            List<Counter> currentCounters = user.getCounters(); // получаем список счетчиков, который есть у пользователя
            List<Long> userCountersIds = userRepository.findCountersIdsByUserId(userId); // получаем список id счетчиков, который есть у пользователя

            System.out.print("userCountersIds");
            System.out.println(userCountersIds);
            System.out.print("CounterIds");
            System.out.println(counterIds);
            // добавляем к существующему списку счетчиков полученные счетчики
            Set<Long> uniqueValues = new HashSet<>();
            uniqueValues.addAll(userCountersIds);
            uniqueValues.addAll(counterIds);
            List<Long> countersIds = new ArrayList<>(uniqueValues);
            System.out.println(countersIds);

            List<Counter> counters = counterRepository.findAllById(countersIds);
            System.out.println("Save counters to user!!!");
            user.setCounters(counters);
            userRepository.save(user);
            return true;
        }
        return false;
    }

//    @Transactional
//    public boolean addCounterToCurrentUser(Long counterId) {
//        CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername());
//        Optional<Counter> counterOptional = counterRepository.findById(counterId);
//        if (counterOptional.isPresent()) {
//            Counter counter = counterOptional.get();
////            user.setCounters(Collections.singletonList(counter));
////            userRepository.save(user);
//            List<CustomUser> users= new ArrayList<>();
//            users.add(user);
//            counter.setUsers(users);
//            counterRepository.save(counter);
//            return true;
//        }
//        return false;
//    }

    @Transactional(readOnly = true)
    public List<CounterDTO> getCountersByParent(Long parentId) {
        System.out.println("ParentId: " + parentId );
        List<Counter> counters = counterRepository.getCountersByParentId(parentId);
        return counters.stream().map(counter -> new CounterDTO(counter.getId(), counter.getPavilion(), counter.getPlace(),
                counter.getNumber(), counter.getArendator().getSurname(), counter.getArendator().getName(),
                counter.getNotes())).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteCounters(List<Long> countersIds) {
        List<Counter> counters = counterRepository.findAllById(countersIds);
        for (Counter counter : counters) {
            counter.setDeleteDate(LocalDateTime.now());
            counterRepository.save(counter);
        }
        return true;
    }

    @Transactional
    public boolean unDeleteCounters(List<Long> countersIds) {
        List<Counter> counters = counterRepository.findAllById(countersIds);
        for (Counter counter : counters) {
            counter.setDeleteDate(null);
            counterRepository.save(counter);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public List<CounterDTO> getDeletedCounters() {
        List<Counter> counters = counterRepository.getDeletedCounters();
        return counters.stream().map(counter -> new CounterDTO(counter.getId(), counter.getPavilion(), counter.getPlace(),
                counter.getNumber(), counter.getArendator().getSurname(), counter.getArendator().getName(),
                counter.getNotes())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getIdByCurrentUser() {
        CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername());
        return user.getId();
    }
}
