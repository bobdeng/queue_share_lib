package cn.bobdeng.line.queue.domain.queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueServiceImplTest {
    public static final int ORG_ID = 1;
    public static final int BUSINESS_1 = 1;
    public static final int BUSINESS_2 = 2;
    public static final String BUSINESS_NAME_1 = "业务1";
    public static final String BUSINESS_NAME_2 = "业务2";
    public static final int COUNTER_1 = 1;
    public static final String COUNTER_NAME_1 = "窗口1";
    public static final int QUEUE_1 = 1;
    public static final int QUEUE_2 = 2;
    @InjectMocks
    QueueServiceImpl queueService=new QueueServiceImpl();
    @Mock
    QueueRepository queueRepository;
    @Test
    public void getOrgQueue() {
        Queue queueWithCounter = Queue.builder().id(QUEUE_1).businessId(BUSINESS_1).build();
        Queue queueWithoutCounter = Queue.builder().id(QUEUE_2).businessId(BUSINESS_1).counterId(COUNTER_1).build();
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
}