package dimploma.rynok.controllers;

import dimploma.rynok.dto.CounterDTO;
import dimploma.rynok.dto.GroupDTO;
import dimploma.rynok.services.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/groups")
    public ResponseEntity<Void> createGroup(@RequestParam String name, @RequestParam String description) {
        if ( ! groupService.createGroup(name, description)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/api/groups/counters/{groupId}")
    public ResponseEntity<Void> addCountersToGroup(@PathVariable Long groupId, @RequestParam List<Long> counterIds) {
        if ( ! groupService.addCountersToGroup(groupId, counterIds)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/api/groups")
    public List<GroupDTO> getGroups() {
        return groupService.getGroups();
    }

    @GetMapping("/api/groups/counters/{groupId}")
    public List<CounterDTO> getCountersByGroup(@PathVariable Long groupId) {
        return groupService.getCountersByGroup(groupId);
    }
}
