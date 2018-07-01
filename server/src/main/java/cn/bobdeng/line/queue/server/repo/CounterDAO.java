package cn.bobdeng.line.queue.server.repo;

import cn.bobdeng.line.db.CounterDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

public interface CounterDAO extends CrudRepository<CounterDO,Integer> {
    List<CounterDO> findByOrgId(int orgId);
}
