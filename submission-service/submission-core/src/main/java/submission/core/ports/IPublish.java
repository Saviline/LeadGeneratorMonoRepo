package submission.core.ports;

public interface IPublish<Entity> {
    public void publish(Entity entity);
    
}
