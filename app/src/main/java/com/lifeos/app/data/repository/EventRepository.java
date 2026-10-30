package com.lifeos.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lifeos.app.data.dao.EventDao;
import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository{
    private final EventDao eventDao;
    private final LiveData<List<Event>> allEvents;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public EventRepository(Application application){
        AppDatabase database = DatabaseProvider.getDatabase(application);

        eventDao = database.eventDao();
        allEvents = eventDao.getAllEvents();
    }
    public LiveData<List<Event>> getAllEvents(){
        return allEvents;
    }
    public LiveData<Event> getEventById(int id){
        return eventDao.getEventById(id);
    }

    public void insert(Event event, java.util.function.Consumer<Event> callback) {
        executorService.execute(() -> {
            long insertedId = eventDao.insert(event);
            event.setId((int) insertedId);

            if (callback != null) {
                callback.accept(event);
            }
        });
    }

    public void update(Event event){
        executorService.execute(() -> eventDao.update(event));
    }

    public void delete(Event event){
        executorService.execute(() -> eventDao.delete(event));
    }

    public void deleteAllEvents(){
        executorService.execute(eventDao::deleteAllEvents);
    }
}
