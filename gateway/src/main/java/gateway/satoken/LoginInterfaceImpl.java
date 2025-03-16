package gateway.satoken;

import cn.dev33.satoken.stp.StpInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class LoginInterfaceImpl implements StpInterface {
    //TODO 设置权限
    /**
     * 获取权限列表
     * @param loginId
     * @param loginType
     * @return
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        log.info("getPermissionList:loginId:{},loginType:{}",loginId,loginType);
        List<String> list=new ArrayList<>();
        list.add("user");
        return list;
    }

    /**
     * 获取角色列表
     * @param loginId
     * @param loginType
     * @return
     */

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        log.info("getRoleList:loginId:{},loginType:{}",loginId,loginType);
        return null;
    }
}
