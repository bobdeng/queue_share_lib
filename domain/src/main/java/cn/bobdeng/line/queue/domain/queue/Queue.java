package cn.bobdeng.line.queue.domain.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queue {
    private int id;
    private int userId;
    private int orgId;
    private int businessId;
    private long beginTime;
    private int counterId;
    private String name;
    private String mobile;
    private String number;
    private String internalNumber;
    private int orderNumber;
}
