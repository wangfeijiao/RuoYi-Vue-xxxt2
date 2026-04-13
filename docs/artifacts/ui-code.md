[artifact:UICode]
status: READY
owner: senior-ui-designer
scope:
- 约定前端代码实现时的页面结构、组件复用与目录组织
- 为 `senior-frontend-engineer` 提供高保真到代码层的映射说明
inputs:
- [artifact:DesignSpec]
- [artifact:Prototype]
- RuoYi 现有 `views/system/*` 页面模式
deliverables:
- 页面与组件映射建议
- 表格列、标签、对话框和分页复用方式
risks:
- 一期不抽离共享业务组件库，复用以页面内方法和少量工具函数为主
handoff_to:
- engineering-manager
- senior-frontend-engineer
exit_criteria:
- 前端工程师可以按此直接编码

## 页面与代码目录映射

- `views/information/dashboard/index.vue`
- `views/information/project/index.vue`
- `views/information/application/index.vue`
- `views/information/resource/index.vue`
- `views/information/dataAsset/index.vue`
- `views/information/network/index.vue`

## 页面结构约定

- 每个页面沿用 `app-container`
- 查询表单使用 `queryParams`
- 列表数据使用 `xxxList`
- 主弹窗表单使用 `form`
- 工单弹窗可使用 `orderForm`
- 分页使用现有 `pagination` 组件

## 交互代码约定

- 列表查询方法统一命名 `getList`
- 新增弹窗统一命名 `handleAdd`
- 编辑弹窗统一命名 `handleUpdate`
- 删除统一命名 `handleDelete`
- 审批统一命名 `handleApprove`
- 执行统一命名 `handleExecute`

## 详情展示约定

- 一期详情使用 `el-dialog`
- 长文本用 `el-descriptions` + `pre-wrap`
- 版本历史和工单历史使用次级 `el-table`
