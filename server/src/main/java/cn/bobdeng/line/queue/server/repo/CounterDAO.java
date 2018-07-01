package cn.bobdeng.line.queue.server.repo;

import cn.bobdeng.line.db.CounterDO;
import org.springframework.data.repository.CrudRepository;

public interface CounterDAO extends CrudRepository<CounterDO,Integer> {
}
