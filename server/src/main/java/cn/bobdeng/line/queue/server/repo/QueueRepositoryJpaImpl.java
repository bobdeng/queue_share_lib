package cn.bobdeng.line.queue.server.repo;

import cn.bobdeng.line.db.BusinessDO;
import cn.bobdeng.line.db.CounterDO;
import cn.bobdeng.line.db.QueueDO;
import cn.bobdeng.line.queue.domain.queue.Queue;
import cn.bobdeng.line.queue.domain.queue.QueueRepository;
import com.tucodec.utils.BeanCopier;
import lombok.extern.java.Log;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log
public class QueueRepositoryJpaImpl implements QueueRepository {
    public static final String ENQUEUE = "ENQUEUE";
    private static final long TIMEOUT = 5000;
    @Autowired
    QueueDAO queueDAO;
    @Autowired
    CounterDAO counterDAO;
    @Autowired
    BusinessDAO businessDAO;
    @Autowired
    RedissonClient redissonClient;

    public static final String QUEUE_LOCK_KEY_PREFIX = "queue_lock_key_prefix_";

    @Override
    public List<Queue> getOrgQueue(int orgId) {
        return queueDAO.findByOrgId(orgId).stream()
                .map(this::fromDO)
                .collect(Collectors.toList());
    }

    private Queue fromDO(QueueDO queueDO) {
        Queue queue = new Queue();
        BeanUtils.copyProperties(queueDO, queue);
        return queue;
    }

    @Override
    public void lockQueue(int orgId) {
        redissonClient.getLock(QUEUE_LOCK_KEY_PREFIX + orgId).lock(1, TimeUnit.MINUTES);
    }

    @Override
    public void unlockQueue(int orgId) {
        redissonClient.getLock(QUEUE_LOCK_KEY_PREFIX + orgId).unlock();
    }

    @Override
    public Long getLastQueueUpdate(String key) {

        return checkAtomAndGet(key);
    }

    @Override
    public void getAndIncLastUpdate(String key) {
        checkAtomAndGet(key);
        redissonClient.getAtomicLong(key).getAndIncrement();
    }

    private Long checkAtomAndGet(String key) {
        RAtomicLong keyAtom = redissonClient.getAtomicLong(key);
        long ret = keyAtom.get();
        if (ret == 0) {
            long lastUpdate = System.currentTimeMillis();
            if (keyAtom.compareAndSet(0, lastUpdate)) {
                return lastUpdate;
            } else {
                return keyAtom.get();
            }
        }
        return ret;
    }

    @Override
    public void createQueue(Queue queue) {
        queueDAO.save(queueToDo(queue));
    }

    @Override
    public Map<Integer, String> getAllBusinessNames(int orgId) {
        return businessDAO.findByOrgId(orgId).stream()
                .collect(Collectors.toMap(BusinessDO::getId, BusinessDO::getName));
    }

    @Override
    public Map<Integer, String> getAllCounterNames(int orgId) {
        return counterDAO.findByOrgId(orgId).stream()
                .collect(Collectors.toMap(CounterDO::getId, CounterDO::getName));
    }

    @Override
    public Optional<Queue> findQueueById(int orgId, int queueId) {
        return Optional.ofNullable(queueDAO.findByIdAndOrgId(queueId, orgId))
                .map(queueDO -> BeanCopier.copyFrom(queueDO, Queue.class));
    }

    @Override
    public void updateQueueOrderNumber(int queueId, int orderNumber) {
        queueDAO.updateQueueOrderNumber(orderNumber, queueId);
    }

    @Override
    public int findSmallestOrderNumber(int orgId) {
        return Optional.ofNullable(queueDAO.findTop1ByOrgIdOrOrderByOrderNumberAsc(orgId))
                .map(QueueDO::getOrderNumber)
                .orElse(0);
    }

    @Override
    public void remove(int queueId) {
        queueDAO.delete(queueId);
    }

    private QueueDO queueToDo(Queue queue) {
        QueueDO queueDO = QueueDO.builder().build();
        BeanUtils.copyProperties(queue, queueDO);
        return queueDO;
    }


}
