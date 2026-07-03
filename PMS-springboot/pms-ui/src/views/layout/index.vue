<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <img src="" alt="" style="display:none" />
        <span v-show="!isCollapse">PMS</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        <el-menu-item index="/project">
          <el-icon><Folder /></el-icon>
          <template #title>项目管理</template>
        </el-menu-item>
        <el-menu-item index="/presales">
          <el-icon><Promotion /></el-icon>
          <template #title>售前管理</template>
        </el-menu-item>
        <el-menu-item index="/callback">
          <el-icon><Phone /></el-icon>
          <template #title>回访管理</template>
        </el-menu-item>
        <el-menu-item index="/closed-loop">
          <el-icon><CircleCheck /></el-icon>
          <template #title>项目闭环</template>
        </el-menu-item>
        <el-menu-item index="/subcontract">
          <el-icon><Share /></el-icon>
          <template #title>转包管理</template>
        </el-menu-item>
        <el-menu-item index="/prob">
          <el-icon><Warning /></el-icon>
          <template #title>技术公告</template>
        </el-menu-item>
        <el-menu-item index="/maintenance">
          <el-icon><Tools /></el-icon>
          <template #title>维保管理</template>
        </el-menu-item>
        <el-menu-item index="/supervision">
          <el-icon><View /></el-icon>
          <template #title>项目督查</template>
        </el-menu-item>
        <el-menu-item index="/certificate">
          <el-icon><Stamp /></el-icon>
          <template #title>合格证</template>
        </el-menu-item>
        <el-menu-item index="/warrantyCallback">
          <el-icon><Service /></el-icon>
          <template #title>维保回访</template>
        </el-menu-item>
        <el-menu-item index="/workflow">
          <el-icon><Connection /></el-icon>
          <template #title>工作流</template>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>报表</template>
        </el-menu-item>
        <el-menu-item index="/weekly">
          <el-icon><Calendar /></el-icon>
          <template #title>周报</template>
        </el-menu-item>
        <el-menu-item index="/notification">
          <el-icon><Bell /></el-icon>
          <template #title>通知</template>
        </el-menu-item>
        <el-sub-menu index="/ehr">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>人事管理</span>
          </template>
          <el-menu-item index="/ehr/employee">员工管理</el-menu-item>
          <el-menu-item index="/ehr/job">岗位管理</el-menu-item>
          <el-menu-item index="/ehr/holiday">假期管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/dept">部门管理</el-menu-item>
          <el-menu-item index="/system/basic-data">基础数据</el-menu-item>
          <el-menu-item index="/system/operate-log">操作日志</el-menu-item>
          <el-menu-item index="/system/login-record">登录日志</el-menu-item>
          <el-menu-item index="/system/menu">菜单管理</el-menu-item>
          <el-menu-item index="/system/dict">字典管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.userInfo?.realname || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

onMounted(async () => {
  try {
    await userStore.fetchUserInfo()
  } catch (e) { /* handled */ }
})

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    await userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  background-color: #263445;
}
.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #666;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #666;
}
.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
