package cn.bobdeng.line.queue.domain.queue;

import com.tucodec.utils.LastUpdateCache;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
public class QueueServiceImpl implements QueueService {
    QueueRepository queueRepository;
    public static final String QUEUE_LAST_UPDATE_PREFIX = "queue_last_update_";
    LastUpdateCache<List<Queue>> queueCache = new LastUpdateCache<>();

    public List<Queue> getOrgQueue(int orgId) {
        return queueCache.getValue(String.valueOf(orgId),
                () -> queueRepository.getLastQueueUpdate(QUEUE_LAST_UPDATE_PREFIX + orgId),
                () -> listQueue(orgId)).getValue();
    }

    private List<Queue> listQueue(int orgId) {
        Map<Integer, String> businessNames = queueRepository.getAllBusinessNames(orgId);
        Map<Integer,String> counterNames = queueRepository.getAllCounterNames(orgId);
        return queueRepository.getOrgQueue(orgId)
                .stream()
                .peek(queue -> queue.setBusinessName(businessNames.getOrDefault(queue.getBusinessId(),"")))
                .peek(queue -> queue.setCounterName(counterNames.getOrDefault(queue.getCounterId(),null)))
                .collect(Collectors.toList());
    }

    public int join(Queue queue) {
        queueRepository.lockQueue(queue.getOrgId());
        try {
            List<Queue> queues = queueRepository.getOrgQueue(queue.getOrgId());
            if (queues == null) queues = new ArrayList<>();
            queues.stream()
                    .filter(queue1 -> queue1.getUserId() == queue.getUserId())
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
            return (int) queues.stream()
                    .filter(q -> q.getBusinessId() == queue.getBusinessId())
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
