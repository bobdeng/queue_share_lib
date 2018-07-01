package cn.bobdeng.line.queue.server.repo;

import cn.bobdeng.line.db.BusinessDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

public interface BusinessDAO extends CrudRepository<BusinessDO,Integer> {
    List<BusinessDO> findByOrgId(int orgId);
}
