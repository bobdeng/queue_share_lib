package cn.bobdeng.line.queue.domain.queue;

import com.tucodec.utils.LastUpdateCache;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
public class QueueServiceImpl implements QueueService {
    QueueRepository queueRepository;

    LastUpdateCache<List<Queue>> queueCache = new LastUpdateCache<>();

    public List<Queue> getOrgQueue(int orgId) {
        return null;
    }

    public int join(Queue queue) {
        queueRepository.lockQueue(queue.getOrgId());
        try {
            List<Queue> queues = queueRepository.getOrgQueue(queue.getOrgId());
            if (queues == null) queues = new ArrayList<>();
            queues.stream()
                    .filter(queue1 -> queue1.getUserId()==queue.getUserId())
                    .findFirst()
                    .ifPresent(queue1 -> {
                        throw new RuntimeException("不允许重复取号");
                    });
            int maxOrder = queues.stream().mapToInt(Queue::getOrderNumber)
                    .max()
                    .orElse(0);
            queue.setOrderNumber(maxOrder + 1);
            queue.setCounterId(-1);
            queueRepository.createQueue(queue);
            return (int)queues.stream()
                    .filter(q->q.getBusinessId()==queue.getBusinessId())
                    .count();
        } finally {
            queueRepository.unlockQueue(queue.getOrgId());
        }
    }

    public void top(Queue queue) {

    }

    public void confirm(Queue queue) {

    }

    public void cancel(Queue queue) {

    }
}
