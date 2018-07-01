package cn.bobdeng.line.queue.domain.queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueServiceImplTest {
    public static final int ORG_ID = 11222;
    public static final int BUSINESS_1 = 11;
    public static final int BUSINESS_2 = 21;
    public static final String BUSINESS_NAME_1 = "业务1";
    public static final String BUSINESS_NAME_2 = "业务2";
    public static final int COUNTER_1 = 122;
    public static final String COUNTER_NAME_1 = "窗口1";
    public static final int QUEUE_1 = 111;
    public static final int QUEUE_2 = 2222;
    public static final int USER_ID=100;
    @InjectMocks
    QueueServiceImpl queueService=new QueueServiceImpl();
    @Mock
    QueueRepository queueRepository;
    @Test
    public void getOrgQueue() {
        Queue queueWithCounter = Queue.builder().id(QUEUE_1).businessId(BUSINESS_1).counterId(COUNTER_1).build();
        Queue queueWithoutCounter = Queue.builder().id(QUEUE_2).businessId(BUSINESS_2).build();
        when(queueRepository.getLastQueueUpdate(QueueServiceImpl.QUEUE_LAST_UPDATE_PREFIX+ ORG_ID)).thenReturn(System.currentTimeMillis());
        when(queueRepository.getOrgQueue(ORG_ID)).thenReturn(Arrays.asList(queueWithCounter,queueWithoutCounter));
        when(queueRepository.getAllBusinessNames(ORG_ID)).thenReturn(new HashMap<Integer, String>(){{
            put(BUSINESS_1, BUSINESS_NAME_1);
            put(BUSINESS_2, BUSINESS_NAME_2);
        }});
        when(queueRepository.getAllCounterNames(ORG_ID)).thenReturn(new HashMap<Integer, String>(){{
            put(COUNTER_1, COUNTER_NAME_1);
        }});

        List<Queue> queues = queueService.getOrgQueue(ORG_ID);
        Queue queue = findQueue(queues, QUEUE_1);
        assertEquals(queue.getBusinessName(),BUSINESS_NAME_1);
        assertEquals(queue.getCounterName(),COUNTER_NAME_1);

        Queue queue2 = findQueue(queues, QUEUE_2);
        assertEquals(queue2.getBusinessName(),BUSINESS_NAME_2);

        assertNull(queue2.getCounterName());

    }

    private Queue findQueue(List<Queue> queues, int id) {
        return queues.stream()
                .filter(queue->queue.getId()==id)
                .findFirst().get();
    }

    @Test
    public void joinQueue_null() {
        when(queueRepository.getOrgQueue(ORG_ID)).thenReturn(null);
        Queue queue = Queue.builder().orgId(ORG_ID).build();
        int before = queueService.join(queue);
        assertEquals(before, 0);
        assertEquals(queue.getOrderNumber(), 1);
    }

    @Test
    public void joinQueue_empty() {
        when(queueRepository.getOrgQueue(ORG_ID)).thenReturn(new ArrayList<>());
        Queue queue = Queue.builder().orgId(ORG_ID).build();
        int before = queueService.join(queue);
        assertEquals(before, 0);
        assertEquals(queue.getOrderNumber(), 1);
    }

    @Test
    public void joinQueue_not_empty() {
        List<Queue> queues = IntStream.range(0, 10).mapToObj(i -> Queue.builder().orderNumber(i).build())
                .collect(Collectors.toList());
        when(queueRepository.getOrgQueue(ORG_ID)).thenReturn(queues);
        Queue queue = Queue.builder().orgId(ORG_ID).userId(USER_ID).build();
        int before = queueService.join(queue);
        assertEquals(before, 10);
        assertEquals(queue.getOrderNumber(), 10);
    }
    @Test(expected = RuntimeException.class)
    public void joinQueue_not_repeat() {
        when(queueRepository.getOrgQueue(ORG_ID)).thenReturn(Arrays.asList(Queue.builder().userId(USER_ID).build()));
        Queue queue = Queue.builder().orgId(ORG_ID).userId(USER_ID).build();
        int before = queueService.join(queue);
        assertEquals(before, 10);
        assertEquals(queue.getOrderNumber(), 10);
        assertEquals(queue.getCounterId(),-1);
    }
}