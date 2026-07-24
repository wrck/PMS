package com.dp.plat.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot application entry point for the network equipment PMS.
 *
 * <p>组件扫描覆盖 {@code com.dp.plat}（PMS 业务模块）与 {@code cn.iocoder.yudao.module}
 * （yudao 业务模块的 Service/DAO 等组件），使 yudao 的 user/role/menu/auth 等能力在运行时可用。</p>
 *
 * <p>注意：不扫描 {@code cn.iocoder.yudao.framework} 包。该包下的类均为
 * {@code @AutoConfiguration} 自动配置类，已通过 {@code META-INF/spring/...AutoConfiguration.imports}
 * 注册。若被组件扫描重复加载，会绕过 {@code @ConditionalOnProperty} 条件判断，
 * 导致 {@code yudao.tenant.enable=false} 等开关失效。</p>
 *
 * <p><b>全量复用 yudao 底座公共能力</b>：yudao 的 admin Controller（system + infra 模块）
 * 全部放开加载，前端直接调用 yudao 原生 {@code /admin-api/system/*} 与
 * {@code /admin-api/infra/*} 接口。仅排除以下两类：
 * <ul>
 *   <li>app-api Controller（{@code *.controller.app.*}）：PMS 无 C 端 App 场景</li>
 *   <li>demo Controller（{@code *.controller.admin.demo*.*}）：yudao 示例代码，避免污染生产接口</li>
 * </ul>
 * yudao 的 system_xxx / infra_xxx 表已通过 Flyway 迁移（V89/V91）创建，PMS 旧 sys_xxx 表
 * 的数据已迁移至 yudao 体系（V90）。PMS 重叠 Controller（SysUser/SysRole/SysMenu 等）
 * 已标记 {@code @Deprecated}，待下阶段清理。</p>
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {"com.dp.plat", "cn.iocoder.yudao.module"},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        // 仅排除 app-api Controller 和 demo Controller，放开全部 admin Controller
                        pattern = "cn\\.iocoder\\.yudao\\.module\\..*\\.controller\\.(app\\..*|.*\\.demo\\d+\\..*)"
                )
        }
)
@MapperScan({"com.dp.plat.**.mapper", "com.dp.plat.**.dao", "com.dp.plat.**.engine.ddl",
        "cn.iocoder.yudao.module.**.dal.mysql"})
@EnableScheduling
@EnableRetry
public class PmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmsApplication.class, args);
    }
}
