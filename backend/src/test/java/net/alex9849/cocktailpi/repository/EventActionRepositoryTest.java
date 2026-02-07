package net.alex9849.cocktailpi.repository;

import net.alex9849.cocktailpi.BackendIntegrationTestBase;
import net.alex9849.cocktailpi.model.eventaction.CallUrlEventAction;
import net.alex9849.cocktailpi.model.eventaction.DoNothingEventAction;
import net.alex9849.cocktailpi.model.eventaction.EventAction;
import net.alex9849.cocktailpi.model.eventaction.EventTrigger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventActionRepositoryTest extends BackendIntegrationTestBase {

    @Autowired
    private EventActionRepository eventActionRepository;
    @Autowired
    private EventActionExecutionGroupRepository executionGroupRepository;

    @Test
    void createUpdateAndDeleteEventAction() {
        DoNothingEventAction eventAction = new DoNothingEventAction();
        eventAction.setTrigger(EventTrigger.COCKTAIL_PRODUCTION_STARTED);
        eventAction.setComment("Repo event action");
        eventAction.setExecutionGroups(Set.of("group1", "group2"));
        EventAction created = eventActionRepository.create(eventAction);
        assertNotNull(created);

        Optional<EventAction> found = eventActionRepository.getById(created.getId());
        assertTrue(found.isPresent());

        created.setComment("Updated comment");
        created.setExecutionGroups(Set.of("group3"));
        EventAction updated = eventActionRepository.update(created);
        assertEquals("Updated comment", updated.getComment());

        List<EventAction> byTrigger = eventActionRepository.getByTrigger(EventTrigger.COCKTAIL_PRODUCTION_STARTED);
        assertTrue(byTrigger.stream().anyMatch(a -> a.getId() == created.getId()));

        assertTrue(eventActionRepository.delete(created.getId()));
    }

    @Test
    void createCallUrlAction() {
        CallUrlEventAction action = new CallUrlEventAction();
        action.setTrigger(EventTrigger.COCKTAIL_PRODUCTION_FINISHED);
        action.setComment("");
        action.setExecutionGroups(Set.of());
        action.setUrl("https://example.com");
        action.setRequestMethod(RequestMethod.POST);

        EventAction created = eventActionRepository.create(action);
        assertNotNull(created);

        List<EventAction> all = eventActionRepository.getAll();
        assertTrue(all.stream().anyMatch(a -> a.getId() == created.getId()));
    }

    @Test
    void executionGroupsRoundTrip() {
        DoNothingEventAction eventAction = new DoNothingEventAction();
        eventAction.setTrigger(EventTrigger.COCKTAIL_PRODUCTION_STARTED);
        eventAction.setComment("Groups");
        eventAction.setExecutionGroups(Set.of("alpha", "beta"));
        EventAction created = eventActionRepository.create(eventAction);

        Set<String> groups = executionGroupRepository.getEventExecutionGroups(created.getId());
        assertEquals(Set.of("alpha", "beta"), groups);

        executionGroupRepository.setEventActionExecutionGroups(created.getId(), Set.of("gamma"));
        Set<String> updatedGroups = executionGroupRepository.getEventExecutionGroups(created.getId());
        assertEquals(Set.of("gamma"), updatedGroups);

        assertTrue(executionGroupRepository.getAllEventExecutionGroupsByEventActionId()
                .getOrDefault(created.getId(), Set.of()).contains("gamma"));
    }
}
