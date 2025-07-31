package pl.app.common.event;

import org.bson.types.ObjectId;

public interface Event {
    ObjectId id();
}
