package me.intriguing.juggernautevent.managers;

import lombok.Getter;

public class EventManager {

    @Getter boolean running;

    public EventManager() {

    }

    public void start() {
        this.running = true;
    }

}
