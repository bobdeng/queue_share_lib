package cn.bobdeng.line.queue.server.repo;

import cn.bobdeng.line.db.QueueDO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

public interface QueueDAO extends CrudRepository<QueueDO,Integer> {
    List<QueueDO> findByOrgId(int orgId);

    QueueDO findByIdAndOrgId(int queueId, int orgId);
    @Modifying
    @Transactional
    @Query("update QueueDO a set a.orderNumber=:orderNumber where a.id=:id")
    void updateQueueOrderNumber(@Param("orderNumber") int orderNumber,@Param("id") int queueId);

    QueueDO findTop1ByOrgIdOrOrderByOrderNumberAsc(int orgId);
}
