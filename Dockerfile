# =============================================================================
# PMS 项目容器化构建镜像（多阶段）
#
# 目标：构建 PMS-springmvc（pms2 profile）WAR，并在 Tomcat 9 / JDK 8 上运行。
#
# Profile 说明（构建参数可覆盖）：
#   BUILD_PROFILE（产品线 profile，决定 WAR 产物名）：
#     - pms2（默认）→ 产物 PMS2.war
#     - pms3          → 产物 AFPMS3.war
#   ENV_PROFILE（环境 profile，决定 config/profiles/<env> 资源过滤）：
#     - dev（默认）/ test / release
#
# 构建示例：
#   docker build -t pms-app .
#   docker build --build-arg BUILD_PROFILE=pms3 --build-arg WAR_NAME=AFPMS3.war -t pms-app:pms3 .
# =============================================================================

# ----------------------------------------------------------------------------
# 阶段 1：Maven 构建器
# ----------------------------------------------------------------------------
FROM maven:3.8-jdk-8 AS builder

# 构建参数：profile 与产物 WAR 名（须与所选 BUILD_PROFILE 对应）
ARG BUILD_PROFILE=pms2
ARG ENV_PROFILE=dev
ARG WAR_NAME=PMS2.war

WORKDIR /build

# 第一步：先拷贝根 pom 与各模块 pom，利用 Docker 层缓存加速依赖解析
# （pom 未变时，后续依赖下载层可命中缓存）
COPY pom.xml ./
COPY core/pom.xml core/
COPY PMS-struts/pom.xml PMS-struts/
COPY PMS-activiti/pom.xml PMS-activiti/
COPY PMS-springmvc/pom.xml PMS-springmvc/
COPY PMS-ext-d365/pom.xml PMS-ext-d365/
COPY PMS-security/pom.xml PMS-security/
COPY pms-rules/pom.xml pms-rules/
COPY pms-ext-fp/pom.xml pms-ext-fp/

# 预热本地仓库（下载依赖）。允许失败：私有制品缺失时不阻断，最终构建步骤会给出明确报错。
RUN mvn -B -q -pl PMS-springmvc -am -P ${ENV_PROFILE},${BUILD_PROFILE} dependency:go-offline || true

# 第二步：拷贝全部源码与配置（PMS-struts 的 WebContent/WEB-INF/lib 系统作用域 jar 一并拷入）
COPY . .

# 第三步：构建 PMS-springmvc 及其上游依赖模块，跳过测试
#   -pl PMS-springmvc -am ：仅构建 PMS-springmvc 及其依赖模块，避免构建无关 WAR，加速构建
#   -P dev,pms2           ：环境层 dev + 产品线 pms2，产物为 PMS2.war
RUN mvn -B clean package -P ${ENV_PROFILE},${BUILD_PROFILE} -DskipTests -pl PMS-springmvc -am

# 校验产物存在（名称不匹配时构建在此处失败，便于定位）
RUN ls -lh PMS-springmvc/target/${WAR_NAME}

# ----------------------------------------------------------------------------
# 阶段 2：Tomcat 运行时镜像
# ----------------------------------------------------------------------------
FROM tomcat:9.0-jdk8-corretto

# WAR 文件名（须与阶段 1 产物一致）
ARG WAR_NAME=PMS2.war

# 清理 Tomcat 自带示例应用，避免占用 ROOT 上下文
RUN rm -rf /usr/local/tomcat/webapps/*

# 将构建好的 WAR 拷贝为 ROOT 应用（访问地址 http://host:8080/）
# 若应用依赖固定上下文路径（如 /PMS2），可将目标改为 /usr/local/tomcat/webapps/${WAR_NAME}
COPY --from=builder /build/PMS-springmvc/target/${WAR_NAME} /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
