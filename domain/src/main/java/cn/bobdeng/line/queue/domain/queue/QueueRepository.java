package cn.bobdeng.line.queue.domain.queue;

import java.util.List;

public interface QueueRepository {
    void lockQueue(int orgId);

    void unlockQueue(int orgId);

    List<Queue> getOrgQueue(int orgId);

    void createQueue(Queue queue);
}
