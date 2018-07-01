package cn.bobdeng.line.queue.domain.queue;

import java.util.List;

public interface QueueService {
    List<Queue> getOrgQueue(int orgId);

    int join(Queue queue);

    void top(Queue queue);

    void confirm(Queue queue);

    void cancel(Queue queue);
}
