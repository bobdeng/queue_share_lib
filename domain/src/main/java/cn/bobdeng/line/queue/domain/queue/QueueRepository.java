package cn.bobdeng.line.queue.domain.queue;

import java.util.List;
import java.util.Map;

public interface QueueRepository {
    void lockQueue(int orgId);

    void unlockQueue(int orgId);

    List<Queue> getOrgQueue(int orgId);

    Long getLastQueueUpdate(String key);

    void getAndIncLastUpdate(String key);

    void createQueue(Queue queue);

    Map<Integer,String> getAllBusinessNames(int orgId);

    Map<Integer,String> getAllCounterNames(int orgId);
}
