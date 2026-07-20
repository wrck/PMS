-- =====================================================================
-- V66__fix_url_html_escape.sql
-- 修复列表配置中 URL 的 HTML 转义问题：
--   V65 迁移脚本中 URL 参数分隔符 & 被存储为 &amp;，
--   导致路由解析时 query 参数 key 变为 "amp;id" 而非 "id"，
--   表单查看/编辑页面无法按 ID 加载数据。
-- =====================================================================

UPDATE pms_lowcode_list
SET list_config = REPLACE(list_config, '&amp;', '&')
WHERE code IN ('list_demo_employee', 'list_demo_onboarding_task', 'list_demo_department')
  AND deleted = 0;

SELECT 'URL HTML 转义修复完成' AS message;
