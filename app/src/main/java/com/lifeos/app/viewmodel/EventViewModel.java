
package com.lifeos.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeos.app.data.entity.Event;
import com.lifeos.app.data.repository.EventRepository;

import java.util.List;

public class EventViewModel extends AndroidViewModel {

    private final EventRepository eventRepository;
    private final LiveData<List<Event>> allEvents;

    public EventViewModel(@NonNull Application application) {
        super(application);

        eventRepository = new EventRepository(application);
        allEvents = eventRepository.getAllEvents();
    }

    // Retrieve all events
    public LiveData<List<Event>> getAllEvents() {
        return allEvents;
    }

    // Retrieve an event by its ID
    public LiveData<Event> getEventById(int id) {
        return eventRepository.getEventById(id);
    }

    // Update an event
    public void update(Event event) {
        eventRepository.update(event);
    }

    // Delete an event
    public void delete(Event event) {
        eventRepository.delete(event);
    }

    // Delete all events
    public void deleteAllEvents() {
        eventRepository.deleteAllEvents();
    }
    public void insertEvent(Event event,java.util.function.Consumer<Event> callback) {
            eventRepository.insert(event, callback);
    }
}

