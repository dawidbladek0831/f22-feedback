package pl.app.common.event;

public interface DomainObjectEvent extends Event {
    String domainObjectType();

    String domainObjectId();
}
