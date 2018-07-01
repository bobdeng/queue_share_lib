package cn.bobdeng.line.queue.server.repo;

import cn.bobdeng.line.db.QueueDO;
import org.springframework.data.repository.CrudRepository;

import java.util.Arrays;
import java.util.List;

public interface QueueDAO extends CrudRepository<QueueDO,Integer> {
    List<QueueDO> findByOrgId(int orgId);
}
