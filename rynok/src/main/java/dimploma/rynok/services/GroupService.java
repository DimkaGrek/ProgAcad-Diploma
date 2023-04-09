package dimploma.rynok.services;

import dimploma.rynok.dto.CounterDTO;
import dimploma.rynok.dto.GroupDTO;
import dimploma.rynok.model.Counter;
import dimploma.rynok.model.CustomUser;
import dimploma.rynok.model.Group;
import dimploma.rynok.repo.CounterRepository;
import dimploma.rynok.repo.GroupRepository;
import dimploma.rynok.repo.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CounterRepository counterRepository;

    @Transactional
    public boolean createGroup(String name, String description) {
        CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername());
        Group group = new Group(name, description);
        group.setUser(user);
        groupRepository.save(group);
        return true;
    }

    @Transactional
    public boolean addCountersToGroup(Long groupId, List<Long> counterIds) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        if(groupOptional.isPresent()) {
            Group group = groupOptional.get();
            List<Counter> counters = counterRepository.findAllById(counterIds);
            group.setCounters(counters);
            groupRepository.save(group);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<GroupDTO> getGroups() {
        CustomUser user = userRepository.findByLogin(SharedService.getCurrentUser().getUsername());
        return user.getGroups().stream().map(group -> new GroupDTO(group.getId(), group.getName(), group.getDescription())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CounterDTO> getCountersByGroup(Long groupId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if(optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            return group.getCounters().stream().map(counter -> new CounterDTO(counter.getId(), counter.getPavilion(), counter.getPlace(),
                    counter.getNumber(), counter.getArendator().getSurname(), counter.getArendator().getName(),
                    counter.getNotes())).collect(Collectors.toList());
        }
        return null;
    }
}
