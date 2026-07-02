<template>
  <div v-loading="loading">
    <!-- 顶部项目信息卡片 -->
    <el-card style="margin-bottom:16px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <span style="font-size:18px;font-weight:bold">{{ project.projectName }}</span>
            <el-tag style="margin-left:12px" :type="stateTagType">{{ stateText }}</el-tag>
            <el-tag v-if="project.executionState" style="margin-left:8px" type="info">{{ executionStateText }}</el-tag>
          </div>
          <div>
            <el-button-group>
              <el-button v-if="canBack" type="warning" @click="handleBack">回退</el-button>
              <el-button v-if="canAgree" type="success" @click="handleAgree">同意</el-button>
              <el-button v-if="canDeny" type="danger" @click="handleDeny">拒绝</el-button>
              <el-button v-if="canSave" type="primary" @click="handleSave">保存</el-button>
              <el-button @click="$router.back()">返回</el-button>
            </el-button-group>
          </div>
        </div>
      </template>
      <el-descriptions :column="4" border size="small">
        <el-descriptions-item label="项目编码">{{ project.projectCode }}</el-descriptions-item>
        <el-descriptions-item label="合同号">{{ project.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="办事处">{{ project.officeName }}</el-descriptions-item>
        <el-descriptions-item label="项目类型">{{ project.projectType }}</el-descriptions-item>
        <el-descriptions-item label="实施方式">{{ project.projectImplWay }}</el-descriptions-item>
        <el-descriptions-item label="一级渠道">{{ project.firstChannelName }}</el-descriptions-item>
        <el-descriptions-item label="二级渠道">{{ project.secondChannelName }}</el-descriptions-item>
        <el-descriptions-item label="项目等级">{{ project.projectLevel }}</el-descriptions-item>
        <el-descriptions-item label="销售人员">{{ project.salesman }}</el-descriptions-item>
        <el-descriptions-item label="服务经理">{{ project.serviceManagerName }}</el-descriptions-item>
        <el-descriptions-item label="项目经理">{{ project.programManagerName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ project.createTime }}</el-descriptions-item>
        <el-descriptions-item label="执行状态">{{ executionStateText }}</el-descriptions-item>
        <el-descriptions-item label="发货状态">{{ project.shipmentState }}</el-descriptions-item>
        <el-descriptions-item label="闭环状态">{{ project.closeProcessState }}</el-descriptions-item>
        <el-descriptions-item label="工程状态">{{ project.planState }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Tab页签 -->
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 1. 成员管理 -->
        <el-tab-pane label="成员管理" name="member">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="showMemberDialog()">添加成员</el-button>
          </div>
          <el-table :data="members" stripe size="small">
            <el-table-column prop="memberName" label="姓名" width="100" />
            <el-table-column prop="roleName" label="角色" width="120" />
            <el-table-column prop="departmentName" label="部门" width="150" />
            <el-table-column prop="memberMobile" label="手机" width="130" />
            <el-table-column prop="memberEmail" label="邮箱" min-width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="showMemberDialog(row)">编辑</el-button>
                <el-button size="small" type="danger" link @click="deleteMember(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 2. 工程计划 -->
        <el-tab-pane label="工程计划" name="plan">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="showPlanDialog()">添加计划</el-button>
          </div>
          <el-table :data="plans" stripe size="small">
            <el-table-column prop="planName" label="计划名称" min-width="150" />
            <el-table-column prop="planStartDate" label="开始日期" width="120" />
            <el-table-column prop="planEndDate" label="结束日期" width="120" />
            <el-table-column prop="planState" label="状态" width="100">
              <template #default="{ row }"><el-tag :type="row.planState==='完成'?'success':'warning'" size="small">{{ row.planState }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="planPerson" label="负责人" width="100" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="showPlanDialog(row)">编辑</el-button>
                <el-button size="small" type="danger" link @click="deletePlan(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 3. 交付件 -->
        <el-tab-pane label="交付件" name="deliverable">
          <div style="margin-bottom:12px">
            <el-upload :action="`/api/file/upload`" :headers="uploadHeaders" :data="{ projectId: id, fileType: 'deliverable' }" :on-success="handleDeliverableUpload" :show-file-list="false">
              <el-button type="primary" size="small">上传交付件</el-button>
            </el-upload>
          </div>
          <el-table :data="deliverables" stripe size="small">
            <el-table-column prop="fileName" label="文件名" min-width="200" />
            <el-table-column prop="fileSize" label="大小" width="100" />
            <el-table-column prop="uploadBy" label="上传人" width="100" />
            <el-table-column prop="uploadTime" label="上传时间" width="170" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="downloadFile(row)">下载</el-button>
                <el-button size="small" type="danger" link @click="deleteDeliverable(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 4. 订单清单 -->
        <el-tab-pane label="订单清单" name="order">
          <el-table :data="orders" stripe size="small">
            <el-table-column prop="orderNumber" label="订单号" width="150" />
            <el-table-column prop="itemCode" label="物料编码" width="130" />
            <el-table-column prop="itemName" label="物料名称" min-width="200" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="price" label="单价" width="100" />
            <el-table-column prop="amount" label="金额" width="120" />
          </el-table>
        </el-tab-pane>

        <!-- 5. 实施发货清单 -->
        <el-tab-pane label="实施发货清单" name="realOrder">
          <el-table :data="realOrders" stripe size="small">
            <el-table-column prop="orderNumber" label="订单号" width="150" />
            <el-table-column prop="itemCode" label="物料编码" width="130" />
            <el-table-column prop="itemName" label="物料名称" min-width="200" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="serialNumber" label="序列号" width="150" />
          </el-table>
        </el-tab-pane>

        <!-- 6. 发货信息 -->
        <el-tab-pane label="发货信息" name="shipment">
          <div style="margin-bottom:12px;display:flex;gap:8px;flex-wrap:wrap">
            <el-button type="primary" size="small" @click="handleExportSpotCheck">导出现场验货单</el-button>
            <el-button size="small" @click="handleExportOverWarranty">超期保修提醒</el-button>
            <el-button size="small" @click="handleTransferShipment">设备转移</el-button>
            <el-upload :action="`/api/project/importSpotCheckIgnoreItem`" :headers="uploadHeaders" :data="{ projectId: id }" :on-success="handleImportSpotCheck" :show-file-list="false" style="display:inline-block;margin-left:8px">
              <el-button size="small">导入现场验货单</el-button>
            </el-upload>
          </div>
          <el-descriptions :column="3" size="small" border style="margin-bottom:12px">
            <el-descriptions-item label="安装地址">
              <div style="display:flex;gap:8px">
                <el-input v-model="installAddress" size="small" style="width:300px" />
                <el-button size="small" type="primary" @click="saveInstallAddress">保存</el-button>
              </div>
            </el-descriptions-item>
          </el-descriptions>
          <el-table :data="shipments" stripe size="small">
            <el-table-column prop="serialNumber" label="序列号" width="150" />
            <el-table-column prop="itemCode" label="物料编码" width="130" />
            <el-table-column prop="itemName" label="物料名称" min-width="200" />
            <el-table-column prop="shipmentDate" label="发货日期" width="120" />
            <el-table-column prop="warrantyStatus" label="保修状态" width="100" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="danger" link @click="deleteShipment(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 7. 租赁配置清单 -->
        <el-tab-pane label="租赁配置" name="leaseLine">
          <el-table :data="leaseLines" stripe size="small">
            <el-table-column prop="lineNo" label="行号" width="80" />
            <el-table-column prop="itemCode" label="物料" width="130" />
            <el-table-column prop="itemName" label="名称" min-width="200" />
            <el-table-column prop="leaseStartDate" label="租赁开始" width="120" />
            <el-table-column prop="leaseEndDate" label="租赁结束" width="120" />
          </el-table>
        </el-tab-pane>

        <!-- 8. 配置关系清单 -->
        <el-tab-pane label="配置关系" name="configLevel">
          <el-table :data="configLevels" stripe size="small">
            <el-table-column prop="parentItemCode" label="父物料" width="130" />
            <el-table-column prop="childItemCode" label="子物料" width="130" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="level" label="层级" width="80" />
          </el-table>
        </el-tab-pane>

        <!-- 9. 软件版本 -->
        <el-tab-pane label="软件版本" name="softVersion">
          <div style="margin-bottom:12px">
            <el-button v-if="!softVersionEditing" type="primary" size="small" @click="softVersionEditing = true">编辑</el-button>
            <template v-else>
              <el-button type="success" size="small" @click="saveSoftVersions">保存</el-button>
              <el-button size="small" @click="softVersionEditing = false; fetchSoftVersions()">取消</el-button>
            </template>
          </div>
          <el-table :data="softVersions" stripe size="small">
            <el-table-column prop="serialNumber" label="序列号" width="150" />
            <el-table-column prop="itemCode" label="物料编码" width="130" />
            <el-table-column prop="itemName" label="物料名称" min-width="200" />
            <el-table-column prop="softVersion" label="软件版本" width="150">
              <template #default="{ row }">
                <el-input v-if="softVersionEditing" v-model="row.softVersion" size="small" />
                <span v-else>{{ row.softVersion }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="changeRemark" label="变更说明" width="200">
              <template #default="{ row }">
                <el-input v-if="softVersionEditing" v-model="row.changeRemark" size="small" />
                <span v-else>{{ row.changeRemark }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="showSoftVersionHistory(row)">历史</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 10. 周报 -->
        <el-tab-pane label="周报" name="weekly">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="showWeeklyDialog()">新建周报</el-button>
          </div>
          <el-table :data="weeklys" stripe size="small">
            <el-table-column prop="weeklyPerson" label="填报人" width="100" />
            <el-table-column prop="weeklyDate" label="周期" width="170" />
            <el-table-column prop="weeklyState" label="状态" width="80">
              <template #default="{ row }"><el-tag :type="row.weeklyState===1?'success':'info'" size="small">{{ row.weeklyState===1?'已提交':'草稿' }}</el-tag></template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="showWeeklyDialog(row)">编辑</el-button>
                <el-button v-if="row.weeklyState===0" size="small" type="success" link @click="submitWeeklyReport(row)">提交</el-button>
                <el-button size="small" link @click="feedbackWeeklyReport(row)">回复</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 11. 通知批示 -->
        <el-tab-pane label="通知批示" name="notification">
          <el-table :data="notifications" stripe size="small">
            <el-table-column prop="notifySubject" label="主题" min-width="200" />
            <el-table-column prop="notifyContent" label="内容" min-width="300" show-overflow-tooltip />
            <el-table-column prop="createBy" label="发送人" width="100" />
            <el-table-column prop="createTime" label="时间" width="170" />
          </el-table>
          <div style="margin-top:16px">
            <el-divider content-position="left">批示</el-divider>
            <el-input v-model="instructionContent" type="textarea" :rows="3" placeholder="请输入批示内容" />
            <el-button type="primary" style="margin-top:8px" @click="submitInstruction">提交批示</el-button>
          </div>
        </el-tab-pane>

        <!-- 12. 闭环流程 -->
        <el-tab-pane label="闭环流程" name="closedLoop">
          <div v-if="closedLoopData">
            <el-descriptions :column="2" border size="small" style="margin-bottom:16px">
              <el-descriptions-item label="当前步骤">{{ closedLoopData.currentStep }}</el-descriptions-item>
              <el-descriptions-item label="状态">{{ closedLoopData.state }}</el-descriptions-item>
            </el-descriptions>
            <el-table :data="closedLoopData.historyList" stripe size="small">
              <el-table-column prop="stepName" label="步骤" width="150" />
              <el-table-column prop="operator" label="处理人" width="100" />
              <el-table-column prop="result" label="结果" width="100" />
              <el-table-column prop="comment" label="意见" min-width="200" />
              <el-table-column prop="operateTime" label="时间" width="170" />
            </el-table>
          </div>
          <el-empty v-else description="暂无闭环流程" />
        </el-tab-pane>

        <!-- 13. 回访流程 -->
        <el-tab-pane label="回访流程" name="callback">
          <div v-if="callbackData">
            <el-descriptions :column="2" border size="small" style="margin-bottom:16px">
              <el-descriptions-item label="当前步骤">{{ callbackData.currentStep }}</el-descriptions-item>
              <el-descriptions-item label="状态">{{ callbackData.state }}</el-descriptions-item>
            </el-descriptions>
            <el-table :data="callbackData.historyList" stripe size="small">
              <el-table-column prop="stepName" label="步骤" width="150" />
              <el-table-column prop="operator" label="处理人" width="100" />
              <el-table-column prop="result" label="结果" width="100" />
              <el-table-column prop="comment" label="意见" min-width="200" />
              <el-table-column prop="operateTime" label="时间" width="170" />
            </el-table>
          </div>
          <el-empty v-else description="暂无回访流程" />
        </el-tab-pane>

        <!-- 14. 转包 -->
        <el-tab-pane label="转包" name="subcontract">
          <el-table :data="subcontracts" stripe size="small">
            <el-table-column prop="subcontractCode" label="转包编码" width="150" />
            <el-table-column prop="facilitatorName" label="服务商" width="150" />
            <el-table-column prop="subcontractType" label="类型" width="100" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="170" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="$router.push(`/subcontract/detail/${row.id}`)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 15. 维保 -->
        <el-tab-pane label="维保" name="maintenance">
          <el-table :data="maintenances" stripe size="small">
            <el-table-column prop="maintenanceType" label="类型" width="100" />
            <el-table-column prop="maintenanceContent" label="内容" min-width="200" />
            <el-table-column prop="maintenancePerson" label="维护人" width="100" />
            <el-table-column prop="maintenanceTime" label="时间" width="170" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="$router.push(`/maintenance/detail/${row.id}`)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 16. 督查 -->
        <el-tab-pane label="督查" name="supervision">
          <el-table :data="supervisions" stripe size="small">
            <el-table-column prop="supervisionType" label="类型" width="100" />
            <el-table-column prop="supervisionContent" label="内容" min-width="200" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }"><el-tag :type="row.status===1?'success':'warning'" size="small">{{ row.status===1?'已整改':'待整改' }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="170" />
          </el-table>
        </el-tab-pane>

        <!-- 17. 问题单 -->
        <el-tab-pane label="问题单" name="problemTicket">
          <el-table :data="problemTickets" stripe size="small">
            <el-table-column prop="ticketNo" label="问题单号" width="150" />
            <el-table-column prop="ticketTitle" label="标题" min-width="200" />
            <el-table-column prop="ticketStatus" label="状态" width="100" />
            <el-table-column prop="createTime" label="时间" width="170" />
          </el-table>
        </el-tab-pane>

        <!-- 18. License信息 -->
        <el-tab-pane label="License信息" name="license">
          <el-table :data="licenseInfos" stripe size="small">
            <el-table-column prop="licenseCode" label="License编码" width="150" />
            <el-table-column prop="licenseType" label="类型" width="100" />
            <el-table-column prop="licenseStatus" label="状态" width="100" />
            <el-table-column prop="expireDate" label="过期日期" width="120" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 成员编辑弹窗 -->
    <el-dialog v-model="memberDialogVisible" :title="memberForm.id ? '编辑成员' : '添加成员'" width="500px">
      <el-form :model="memberForm" label-width="80px">
        <el-form-item label="用户" required>
          <el-select v-model="memberForm.userId" filterable remote :remote-method="searchUsers" placeholder="输入姓名搜索" :loading="userSearchLoading">
            <el-option v-for="u in userOptions" :key="u.id" :label="u.userName" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="memberForm.roleId">
            <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMember">确定</el-button>
      </template>
    </el-dialog>

    <!-- 计划编辑弹窗 -->
    <el-dialog v-model="planDialogVisible" :title="planForm.id ? '编辑计划' : '添加计划'" width="500px">
      <el-form :model="planForm" label-width="80px">
        <el-form-item label="计划名称" required><el-input v-model="planForm.planName" /></el-form-item>
        <el-form-item label="开始日期" required><el-date-picker v-model="planForm.planStartDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="结束日期" required><el-date-picker v-model="planForm.planEndDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="负责人"><el-input v-model="planForm.planPerson" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePlan">确定</el-button>
      </template>
    </el-dialog>

    <!-- 周报编辑弹窗 -->
    <el-dialog v-model="weeklyDialogVisible" title="周报" width="80%" top="5vh">
      <el-form :model="weeklyForm" label-width="80px">
        <el-divider content-position="left">本周工作内容</el-divider>
        <div v-for="(item, idx) in weeklyForm.workContents" :key="'wc'+idx" style="display:flex;gap:8px;margin-bottom:8px">
          <el-input v-model="item.content" placeholder="工作内容" style="flex:3" />
          <el-input v-model="item.progress" placeholder="进展" style="flex:1" />
          <el-button type="danger" :icon="Delete" circle size="small" @click="weeklyForm.workContents.splice(idx, 1)" />
        </div>
        <el-button size="small" @click="weeklyForm.workContents.push({ content: '', progress: '' })">+ 添加工作内容</el-button>
        <el-divider content-position="left">风险问题</el-divider>
        <div v-for="(item, idx) in weeklyForm.risks" :key="'rk'+idx" style="display:flex;gap:8px;margin-bottom:8px">
          <el-input v-model="item.risk" placeholder="风险描述" style="flex:2" />
          <el-input v-model="item.solution" placeholder="解决方案" style="flex:2" />
          <el-button type="danger" :icon="Delete" circle size="small" @click="weeklyForm.risks.splice(idx, 1)" />
        </div>
        <el-button size="small" @click="weeklyForm.risks.push({ risk: '', solution: '' })">+ 添加风险</el-button>
        <el-divider content-position="left">需要协助</el-divider>
        <el-input v-model="weeklyForm.helpNeeded" type="textarea" :rows="3" />
        <el-divider content-position="left">下周计划</el-divider>
        <div v-for="(item, idx) in weeklyForm.plans" :key="'pl'+idx" style="display:flex;gap:8px;margin-bottom:8px">
          <el-input v-model="item.plan" placeholder="计划内容" style="flex:3" />
          <el-date-picker v-model="item.planDate" type="date" value-format="YYYY-MM-DD" placeholder="计划日期" style="flex:1" />
          <el-button type="danger" :icon="Delete" circle size="small" @click="weeklyForm.plans.splice(idx, 1)" />
        </div>
        <el-button size="small" @click="weeklyForm.plans.push({ plan: '', planDate: '' })">+ 添加计划</el-button>
        <el-divider content-position="left">附件</el-divider>
        <el-upload :action="`/api/file/upload`" :headers="uploadHeaders" :on-success="handleWeeklyFileUpload" :file-list="weeklyForm.attachments" multiple>
          <el-button size="small">上传附件</el-button>
        </el-upload>
      </el-form>
      <template #footer>
        <el-button @click="weeklyDialogVisible = false">取消</el-button>
        <el-button @click="saveWeeklyDraft">保存草稿</el-button>
        <el-button type="primary" @click="saveWeeklySubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 软件版本历史弹窗 -->
    <el-dialog v-model="softVersionHistoryVisible" title="软件版本历史" width="700px">
      <el-table :data="softVersionHistory" stripe size="small">
        <el-table-column prop="oldVersion" label="原版本" width="120" />
        <el-table-column prop="newVersion" label="新版本" width="120" />
        <el-table-column prop="changeRemark" label="变更说明" min-width="200" />
        <el-table-column prop="changeBy" label="变更人" width="100" />
        <el-table-column prop="changeTime" label="变更时间" width="170" />
      </el-table>
    </el-dialog>

    <!-- 设备转移弹窗 -->
    <el-dialog v-model="transferDialogVisible" title="设备转移" width="700px">
      <el-form :model="transferForm" label-width="80px">
        <el-form-item label="目标项目" required>
          <el-select v-model="transferForm.targetProjectId" filterable remote :remote-method="searchProjects" placeholder="输入项目编码搜索" style="width:100%">
            <el-option v-for="p in projectOptions" :key="p.id" :label="`${p.projectCode} - ${p.projectName}`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择设备">
          <el-table :data="shipments" stripe size="small" @selection-change="handleTransferSelectionChange" max-height="300">
            <el-table-column type="selection" width="50" />
            <el-table-column prop="serialNumber" label="序列号" />
            <el-table-column prop="itemCode" label="物料" />
            <el-table-column prop="itemName" label="名称" />
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTransfer">确认转移</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getProject, updateProject, backToLastStep as apiBackToLastStep,
  getProjectMembers, createProjectMember, updateProjectMember, apiDeleteMember,
  getProjectPlans, createProjectPlan, updateProjectPlan, apiDeletePlan,
  getProjectDeliverables, apiDeleteDeliverable,
  getProjectOrders, getProjectRealOrders,
  getProjectShipments, apiDeleteShipment, apiSaveInstallAddress, apiTransferShipment,
  getProjectSoftVersions, apiUpdateSoftVersion, apiGetSoftVersionHistory,
  getProjectWeeklys, apiCreateWeekly, apiSubmitWeekly, apiFeedbackWeekly,
  getProjectNotifications, apiCreateInstruction,
  getProjectClosedLoop, getProjectCallback,
  getProjectSubcontracts, getProjectMaintenances, getProjectSupervisions,
  getProjectProblemTickets, getProjectLicenseInfos,
  getProjectLeaseLines, getProjectConfigLevels,
  apiExportSpotCheck, apiExportOverWarranty, apiQueryTransferProjectList
} from '@/api/project'
import { searchUsers as apiSearchUsers } from '@/api/system'

const route = useRoute()
const id = route.params.id
const loading = ref(false)
const activeTab = ref('member')
const project = ref({})

const members = ref([])
const memberDialogVisible = ref(false)
const memberForm = reactive({ id: null, userId: '', roleId: '' })
const userOptions = ref([])
const userSearchLoading = ref(false)
const roleOptions = ref([])

const plans = ref([])
const planDialogVisible = ref(false)
const planForm = reactive({ id: null, planName: '', planStartDate: '', planEndDate: '', planPerson: '' })

const deliverables = ref([])
const orders = ref([])
const realOrders = ref([])
const shipments = ref([])
const installAddress = ref('')

const softVersions = ref([])
const softVersionEditing = ref(false)
const softVersionHistoryVisible = ref(false)
const softVersionHistory = ref([])

const weeklys = ref([])
const weeklyDialogVisible = ref(false)
const weeklyForm = reactive({ id: null, workContents: [{ content: '', progress: '' }], risks: [{ risk: '', solution: '' }], helpNeeded: '', plans: [{ plan: '', planDate: '' }], attachments: [] })

const notifications = ref([])
const instructionContent = ref('')
const closedLoopData = ref(null)
const callbackData = ref(null)
const subcontracts = ref([])
const maintenances = ref([])
const supervisions = ref([])
const problemTickets = ref([])
const licenseInfos = ref([])
const leaseLines = ref([])
const configLevels = ref([])

const transferDialogVisible = ref(false)
const transferForm = reactive({ targetProjectId: '', selectedShipments: [] })
const projectOptions = ref([])

const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const stateText = computed(() => ({ 0: '草稿', 1: '实施中', 2: '已回退', 3: '待关闭', 4: '已关闭', 5: '已拒绝' }[project.value.projectState] || '未知'))
const stateTagType = computed(() => ({ 0: 'info', 1: '', 2: 'warning', 3: 'warning', 4: 'success', 5: 'danger' }[project.value.projectState] || 'info'))
const executionStateText = computed(() => ({ 0: '未指派', 1: '已指派', 2: '工程中', 3: '已发货', 4: '已安装', 5: '已验收' }[project.value.executionState] || ''))
const canBack = computed(() => project.value.projectState > 0 && project.value.projectState < 4)
const canAgree = computed(() => project.value.projectState === 1)
const canDeny = computed(() => project.value.projectState === 1)
const canSave = computed(() => project.value.projectState < 4)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getProject(id)
    project.value = res.data || {}
    await Promise.all([fetchMembers(), fetchPlans(), fetchOrders(), fetchShipments()])
  } finally { loading.value = false }
})

const handleTabChange = async (tab) => {
  const loaders = {
    deliverable: fetchDeliverables, realOrder: fetchRealOrders,
    softVersion: fetchSoftVersions, weekly: fetchWeeklys,
    notification: fetchNotifications, closedLoop: fetchClosedLoop,
    callback: fetchCallback, subcontract: fetchSubcontracts,
    maintenance: fetchMaintenances, supervision: fetchSupervisions,
    problemTicket: fetchProblemTickets, license: fetchLicenseInfos,
    leaseLine: fetchLeaseLines, configLevel: fetchConfigLevels
  }
  if (loaders[tab]) await loaders[tab]()
}

const fetchMembers = async () => { try { const r = await getProjectMembers(id); members.value = r.data || [] } catch(e) {} }
const fetchPlans = async () => { try { const r = await getProjectPlans(id); plans.value = r.data || [] } catch(e) {} }
const fetchDeliverables = async () => { try { const r = await getProjectDeliverables(id); deliverables.value = r.data || [] } catch(e) {} }
const fetchOrders = async () => { try { const r = await getProjectOrders(id); orders.value = r.data || [] } catch(e) {} }
const fetchRealOrders = async () => { try { const r = await getProjectRealOrders(id); realOrders.value = r.data || [] } catch(e) {} }
const fetchShipments = async () => { try { const r = await getProjectShipments(id); shipments.value = r.data || []; if (shipments.value.length) installAddress.value = shipments.value[0].installAddress || '' } catch(e) {} }
const fetchSoftVersions = async () => { try { const r = await getProjectSoftVersions(id); softVersions.value = r.data || [] } catch(e) {} }
const fetchWeeklys = async () => { try { const r = await getProjectWeeklys(id); weeklys.value = r.data || [] } catch(e) {} }
const fetchNotifications = async () => { try { const r = await getProjectNotifications(id); notifications.value = r.data || [] } catch(e) {} }
const fetchClosedLoop = async () => { try { const r = await getProjectClosedLoop(id); closedLoopData.value = r.data } catch(e) {} }
const fetchCallback = async () => { try { const r = await getProjectCallback(id); callbackData.value = r.data } catch(e) {} }
const fetchSubcontracts = async () => { try { const r = await getProjectSubcontracts(id); subcontracts.value = r.data || [] } catch(e) {} }
const fetchMaintenances = async () => { try { const r = await getProjectMaintenances(id); maintenances.value = r.data || [] } catch(e) {} }
const fetchSupervisions = async () => { try { const r = await getProjectSupervisions(id); supervisions.value = r.data || [] } catch(e) {} }
const fetchProblemTickets = async () => { try { const r = await getProjectProblemTickets(id); problemTickets.value = r.data || [] } catch(e) {} }
const fetchLicenseInfos = async () => { try { const r = await getProjectLicenseInfos(id); licenseInfos.value = r.data || [] } catch(e) {} }
const fetchLeaseLines = async () => { try { const r = await getProjectLeaseLines(id); leaseLines.value = r.data || [] } catch(e) {} }
const fetchConfigLevels = async () => { try { const r = await getProjectConfigLevels(id); configLevels.value = r.data || [] } catch(e) {} }

const handleSave = async () => { await updateProject(id, project.value); ElMessage.success('保存成功') }
const handleBack = () => { ElMessageBox.confirm('确认回退到上一步？', '提示', { type: 'warning' }).then(async () => { await apiBackToLastStep(id); ElMessage.success('已回退'); project.value.projectState-- }).catch(() => {}) }
const handleAgree = async () => { await updateProject(id, { ...project.value, projectState: project.value.projectState + 1 }); ElMessage.success('操作成功'); project.value.projectState++ }
const handleDeny = () => { ElMessageBox.prompt('请输入拒绝原因', '拒绝', { type: 'warning', inputType: 'textarea' }).then(async ({ value }) => { await updateProject(id, { ...project.value, projectState: 5, denyReason: value }); ElMessage.success('已拒绝'); project.value.projectState = 5 }).catch(() => {}) }

const showMemberDialog = (row) => { if (row) Object.assign(memberForm, row); else Object.assign(memberForm, { id: null, userId: '', roleId: '' }); memberDialogVisible.value = true }
const saveMember = async () => { if (memberForm.id) await updateProjectMember(memberForm); else await createProjectMember({ ...memberForm, projectId: id }); ElMessage.success('保存成功'); memberDialogVisible.value = false; fetchMembers() }
const deleteMember = (row) => { ElMessageBox.confirm('确认删除该成员？', '提示', { type: 'warning' }).then(async () => { await apiDeleteMember(row.id); ElMessage.success('已删除'); fetchMembers() }).catch(() => {}) }
const searchUsers = async (query) => { if (!query) return; userSearchLoading.value = true; try { const r = await apiSearchUsers(query); userOptions.value = r.data || [] } finally { userSearchLoading.value = false } }

const showPlanDialog = (row) => { if (row) Object.assign(planForm, row); else Object.assign(planForm, { id: null, planName: '', planStartDate: '', planEndDate: '', planPerson: '' }); planDialogVisible.value = true }
const savePlan = async () => { if (planForm.id) await updateProjectPlan(planForm); else await createProjectPlan({ ...planForm, projectId: id }); ElMessage.success('保存成功'); planDialogVisible.value = false; fetchPlans() }
const deletePlan = (row) => { ElMessageBox.confirm('确认删除该计划？', '提示', { type: 'warning' }).then(async () => { await apiDeletePlan(row.id); ElMessage.success('已删除'); fetchPlans() }).catch(() => {}) }

const deleteDeliverable = (row) => { ElMessageBox.confirm('确认删除该交付件？', '提示', { type: 'warning' }).then(async () => { await apiDeleteDeliverable(row.id); ElMessage.success('已删除'); fetchDeliverables() }).catch(() => {}) }
const handleDeliverableUpload = (res) => { if (res.code === 200) { ElMessage.success('上传成功'); fetchDeliverables() } }
const downloadFile = (row) => { window.open(`/api/file/download/${row.id}`) }

const saveInstallAddress = async () => { await apiSaveInstallAddress(id, installAddress.value); ElMessage.success('保存成功') }
const deleteShipment = (row) => { ElMessageBox.confirm('确认删除该发货信息？', '提示', { type: 'warning' }).then(async () => { await apiDeleteShipment(row.id); ElMessage.success('已删除'); fetchShipments() }).catch(() => {}) }
const handleExportSpotCheck = async () => { const res = await apiExportSpotCheck(id); const url = URL.createObjectURL(res); const a = document.createElement('a'); a.href = url; a.download = '现场验货单.xlsx'; a.click(); URL.revokeObjectURL(url) }
const handleExportOverWarranty = async () => { const res = await apiExportOverWarranty(id); const url = URL.createObjectURL(res); const a = document.createElement('a'); a.href = url; a.download = '超期保修提醒.xlsx'; a.click(); URL.revokeObjectURL(url) }
const handleImportSpotCheck = (res) => { if (res.code === 200) { ElMessage.success('导入成功'); fetchShipments() } }
const handleTransferShipment = () => { transferForm.selectedShipments = []; transferForm.targetProjectId = ''; transferDialogVisible.value = true }
const handleTransferSelectionChange = (selection) => { transferForm.selectedShipments = selection }
const searchProjects = async (query) => { if (query) { const r = await apiQueryTransferProjectList(query); projectOptions.value = r.data || [] } }
const confirmTransfer = async () => { if (!transferForm.targetProjectId) { ElMessage.warning('请选择目标项目'); return } await apiTransferShipment({ sourceProjectId: id, targetProjectId: transferForm.targetProjectId, shipmentIds: transferForm.selectedShipments.map(s => s.id) }); ElMessage.success('转移成功'); transferDialogVisible.value = false; fetchShipments() }

const saveSoftVersions = async () => { await apiUpdateSoftVersion(id, softVersions.value); ElMessage.success('保存成功'); softVersionEditing.value = false }
const showSoftVersionHistory = async (row) => { const r = await apiGetSoftVersionHistory(row.id); softVersionHistory.value = r.data || []; softVersionHistoryVisible.value = true }

const showWeeklyDialog = (row) => {
  if (row) Object.assign(weeklyForm, { ...row, workContents: row.workContents?.length ? row.workContents : [{ content: '', progress: '' }], risks: row.risks?.length ? row.risks : [{ risk: '', solution: '' }], plans: row.plans?.length ? row.plans : [{ plan: '', planDate: '' }], attachments: row.attachments || [] })
  else Object.assign(weeklyForm, { id: null, workContents: [{ content: '', progress: '' }], risks: [{ risk: '', solution: '' }], helpNeeded: '', plans: [{ plan: '', planDate: '' }], attachments: [] })
  weeklyDialogVisible.value = true
}
const saveWeeklyDraft = async () => { await apiCreateWeekly({ ...weeklyForm, projectId: id, weeklyState: 0 }); ElMessage.success('已保存草稿'); weeklyDialogVisible.value = false; fetchWeeklys() }
const saveWeeklySubmit = async () => { await apiCreateWeekly({ ...weeklyForm, projectId: id, weeklyState: 1 }); ElMessage.success('已提交'); weeklyDialogVisible.value = false; fetchWeeklys() }
const submitWeeklyReport = (row) => { ElMessageBox.confirm('确认提交该周报？', '提示', { type: 'warning' }).then(async () => { await apiSubmitWeekly(row.id); ElMessage.success('已提交'); fetchWeeklys() }).catch(() => {}) }
const feedbackWeeklyReport = (row) => { ElMessageBox.prompt('请输入回复内容', '回复', { inputType: 'textarea' }).then(async ({ value }) => { await apiFeedbackWeekly(row.id, value); ElMessage.success('已回复'); fetchWeeklys() }).catch(() => {}) }
const handleWeeklyFileUpload = (res) => { if (res.code === 200) weeklyForm.attachments.push(res.data) }

const submitInstruction = async () => { if (!instructionContent.value.trim()) { ElMessage.warning('请输入批示内容'); return } await apiCreateInstruction({ projectId: id, content: instructionContent.value }); ElMessage.success('已提交'); instructionContent.value = ''; fetchNotifications() }
</script>
