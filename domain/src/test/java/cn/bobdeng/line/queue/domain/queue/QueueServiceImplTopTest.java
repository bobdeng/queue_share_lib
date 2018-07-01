package cn.bobdeng.line.queue.domain.queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueServiceImplTopTest {
    public static final int ORG_ID = 10001;
    public static final int ORDER_NUMBER = 100;
    public static final int QUEUE_ID = 20001;
    @InjectMocks
    QueueServiceImpl queueService=new QueueServiceImpl();
    @Mock
    QueueRepository queueRepository;
    @Test
    public void top() {
        doSomethingInQueue((queue)->{
            queueService.top(Queue.builder()
                    .id(QUEUE_ID)
                    .orgId(ORG_ID)
                    .orderNumber(ORDER_NUMBER+2)
                    .build());
        },()->{
            verify(queueRepository).updateQueueOrderNumber(QUEUE_ID,ORDER_NUMBER-1);
        });
    }

    @Test
    public void confirm(){
        doSomethingInQueue((queue)->{
            queueService.confirm(Queue.builder()
                    .id(QUEUE_ID)
                    .orgId(ORG_ID)
                    .orderNumber(ORDER_NUMBER+2)
                    .build());
        },()->{
            verify(queueRepository).remove(QUEUE_ID);
        });
    }
    @Test
    public void cancel(){
        doSomethingInQueue((queue)->{
            queueService.cancel(Queue.builder()
                    .id(QUEUE_ID)
                    .orgId(ORG_ID)
                    .orderNumber(ORDER_NUMBER+2)
                    .build());
        },()->{
            verify(queueRepository).remove(QUEUE_ID);
        });
    }

    private void doSomethingInQueue(Consumer<Queue> runnable, Runnable verify){
        when(queueRepository.findSmallestOrderNumber(ORG_ID)).thenReturn(ORDER_NUMBER);
        Queue queue = Queue.builder()
                .id(QUEUE_ID)
                .orgId(ORG_ID)
                .orderNumber(ORDER_NUMBER + 2)
                .build();
        when(queueRepository.findQueueById(ORG_ID,QUEUE_ID)).thenReturn(Optional.of(queue));
        runnable.accept(queue);

        verify.run();

        verify(queueRepository).getAndIncLastUpdate(QueueServiceImpl.QUEUE_LAST_UPDATE_PREFIX+ORG_ID);
    }
}