package lc.dynamicupdateporperties;

import java.io.File;

/**
 * 文件产生的事件类
 *
 * @author liuchaoOvO
 */
public class Event {

    private final File origin;

    private final EventType eventType;

    public Event(File origin, EventType eventType) {
        this.origin = origin;
        this.eventType = eventType;
    }


    public File getOrigin() {
        return origin;
    }

    public EventType getEventType() {
        return eventType;
    }


    /**
     * 事件类型
     */
    enum EventType {
        CREATE,
        DELETE,
        CHANGE
    }

    @Override
    public String toString() {
        return "Event{" +
                "origin=" + origin.getAbsolutePath() +
                ", eventType=" + eventType +
                '}';
    }
}
