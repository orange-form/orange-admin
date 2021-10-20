<template>
  <el-row type="flex" style="width: 100%;" :style="{height: height + 'px'}">
    <div style="width: 250px; height: 100%; flex-shrink: 0;" v-if="formConfig.formType !== SysOnlineFormType.WORK_ORDER">
      <el-scrollbar :style="{height: height + 'px'}">
        <div>
          <div class="datasource-card" v-if="formConfig.formType !== SysOnlineFormType.QUERY">
            <div class="card-title">
              布局组件
            </div>
            <Draggable class="card-item-box" draggable=".card-item" :list="baseWidgetList"
              :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
              :clone="cloneBaseWidget" :disabled="formConfig.formType === SysOnlineFormType.QUERY"
              :sort="false"
            >
              <div class="card-item" v-for="widget in baseWidgetList" :key="widget.id" @click="onBaseWidgetClick(widget)">
                <i class="el-icon-bank-card" />
                <span style="margin-left: 5px;" :title="widget.showName">{{widget.showName}}</span>
                <div class="item-count" v-if="columnUseCount[widget.id]">{{columnUseCount[widget.id]}}</div>
              </div>
            </Draggable>
          </div>
          <div class="datasource-card" v-for="table in tableList" :key="table.tableName">
            <div class="card-title">
              <span :title="table.tag.datasourceName || table.tag.relationName">
                {{table.tag.datasourceName || table.tag.relationName}}
              </span>
              <el-tag v-if="getMasterTable !== table" size="mini" style="margin-left: 5px;" effect="dark" :type="getTableTagType(table.relationType)">{{getTableRelationName(table.relationType)}}</el-tag>
            </div>
            <Draggable class="card-item-box" draggable=".card-item" :list="getTableColumnList(table)"
              :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
              :clone="cloneComponent"
              :sort="false"
            >
              <div class="card-item" v-if="table.relationType === SysOnlineRelationType.ONE_TO_MANY && getMasterTable !== table"
                style="width: 100%;" @click="onColumnClick(getTableColumnList(table)[0])">
                <i class="el-icon-bank-card" />
                <span style="margin-left: 5px;" :title="table.tag.datasourceName || table.tag.relationName + '关联数据'">关联数据</span>
              </div>
              <div v-else class="card-item" v-for="column in getTableColumnList(table)" :key="column.id" @click="onColumnClick(column)">
                <i class="el-icon-bank-card" />
                <span style="margin-left: 5px;" :title="column.columnComment">{{column.columnComment}}</span>
              </div>
            </Draggable>
          </div>
        </div>
      </el-scrollbar>
    </div>
    <div style="height: 100%; width: 100%; background: white; margin-right: 10px; border-radius: 3px;"
      :style="{
        'margin-left': formConfig.formType !== SysOnlineFormType.WORK_ORDER ? '10px' : '0px',
        width: getClientWidth - (formConfig.formType === SysOnlineFormType.WORK_ORDER ? 390: 640) + 'px'
      }"
    >
      <el-row type="flex" justify="end" align="middle" style="padding-right: 10px; border-bottom: 1px solid #dcdfe6">
        <el-breadcrumb separator-class="el-icon-arrow-right" style="width: 100%; margin-left: 10px;">
          <el-breadcrumb-item><i class="el-icon-setting" style="margin-right: 10px;" />{{form.formName}}</el-breadcrumb-item>
        </el-breadcrumb>
        <el-checkbox v-model="isLocked" style="margin-right: 10px;">锁定容器</el-checkbox>
        <el-button class="table-btn delete" type="text" icon="el-icon-delete" @click="onReset">重置</el-button>
        <el-button type="text" icon="el-icon-video-play" @click="onPreview">预览</el-button>
        <el-button class="table-btn warning" type="text" icon="el-icon-refresh" @click="onSaveClick">保存</el-button>
        <el-button class="table-btn success" type="text" icon="el-icon-back" @click="goBack">返回</el-button>
      </el-row>
      <el-row class="form-genarated">
        <el-col :span="24">
          <el-scrollbar :style="{height: height - 50 + 'px'}" style="overflow: hidden;">
            <div @click="onFormClick" :style="{'min-height': height - 50 + 'px'}">
              <template v-if="formConfig.formType === SysOnlineFormType.QUERY || formConfig.formType === SysOnlineFormType.WORK_ORDER">
                <div style="position: relative;">
                  <el-form :label-width="formConfig.labelWidth + 'px'" size="mini" :label-position="formConfig.labelPosition" @submit.native.prevent>
                    <DraggableFilterBox :list="formWidgetList" :itemWidth="formConfig.labelWidth + 272"
                      :style="{'min-height': '50px'}" style="padding: 20px 20px 0px 20px; overflow: hidden; display: flex; justify-content: space-between;"
                    >
                      <template v-if="formConfig.formType === SysOnlineFormType.QUERY">
                        <drag-widget-item v-for="widget in formWidgetList" :key="widget.id"
                          :class="{'active-widget': (widget === currentWidgetItem && !widget.hasError)}"
                          :widgetConfig="widget"
                          @click="onWidgetClick"
                          @delete="onWidgetDeleteClick"
                        />
                        <div slot="operator" style="padding: 13px 10px;" v-if="Array.isArray(formWidgetList) && formWidgetList.length > 0">
                          <el-button type="primary" :plain="true" size="mini">查询</el-button>
                        </div>
                        <div slot="operator" style="padding: 13px 10px;" v-for="operation in getTableOperation(false)" :key="operation.id">
                          <el-button size="mini"
                            :plain="operation.plain"
                            :type="operation.btnType"
                            @click.stop="">
                            {{operation.name}}
                          </el-button>
                        </div>
                      </template>
                      <template v-else>
                        <el-form-item label="工单状态">
                          <el-select class="filter-item" v-model="flowWorkStatus" :clearable="true" placeholder="工单状态" />
                        </el-form-item>
                        <el-form-item label="创建日期">
                          <date-range class="filter-item" :clearable="true" :allowTypes="['day']" align="left"
                            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
                            format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" />
                        </el-form-item>
                        <div slot="operator" style="padding: 13px 10px;">
                          <el-button type="primary" :plain="true" size="mini">查询</el-button>
                        </div>
                        <div slot="operator" style="padding: 13px 10px;">
                          <el-button type="primary" size="mini">新建</el-button>
                        </div>
                      </template>
                    </DraggableFilterBox>
                  </el-form>
                  <el-row style="padding: 0px 20px 20px 20px;">
                    <drag-widget-item v-if="formConfig.tableWidget"
                      :class="{'active-widget': (formConfig.tableWidget === currentWidgetItem && !formConfig.tableWidget.hasError)}"
                      :widgetConfig="formConfig.tableWidget"
                      :canDelete="false"
                      @click="onWidgetClick"
                      @delete="onWidgetDeleteClick"
                    />
                  </el-row>
                </div>
              </template>
              <el-row v-else :gutter="formConfig.gutter">
                <el-form class="full-width-input" size="mini" :label-width="formConfig.labelWidth + 'px'" :label-position="formConfig.labelPosition">
                  <Draggable draggable=".draggable-item" :list="formWidgetList" group="componentsGroup"
                    :style="{'min-height': height - 50 + 'px'}" style="padding: 20px; overflow: hidden;"
                  >
                    <drag-widget-item v-for="widget in formWidgetList" :key="widget.id"
                      :class="{'active-widget': (widget === currentWidgetItem && !widget.hasError)}"
                      :widgetConfig="widget"
                      @click="onWidgetClick"
                      @delete="onWidgetDeleteClick"
                    />
                  </Draggable>
                </el-form>
              </el-row>
            </div>
          </el-scrollbar>
        </el-col>
      </el-row>
    </div>
    <div style="width: 350px; height: 100%; background: white; flex-shrink: 0; border-radius: 3px;">
      <el-tabs class="attribute-box" v-model="activeAttributeName">
        <el-tab-pane :label="currentWidgetItem == null ? '表单属性' : '组件属性'" name="widget">
          <el-row v-if="currentWidgetItem != null" class="scroll-box" :style="{height: height - 50 + 'px'}">
            <el-alert v-if="currentWidgetItem.hasError" :title="currentWidgetItem.errorMessage" type="error" :closable="false" style="margin-bottom: 15px;" />
            <el-form class="full-width-input" size="small" label-position="left" label-width="90px">
              <el-form-item label="组件类型">
                <el-select v-model="currentWidgetItem.widgetType" :disabled="true">
                  <el-option v-for="item in SysCustomWidgetType.getList()" :key="item.id"
                    :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="组件标识">
                <el-input v-model="currentWidgetItem.variableName" placeholder="" disabled />
              </el-form-item>
              <el-form-item label="标题名">
                <el-input v-model="currentWidgetItem.showName" placeholder="" />
              </el-form-item>
              <el-form-item label="占位提示"
                v-if="currentWidgetItem.widgetType !== SysCustomWidgetType.RichEditor &&
                  currentWidgetItem.widgetType !== SysCustomWidgetType.Upload &&
                  currentWidgetItem.widgetKind === SysCustomWidgetKind.Form"
              >
                <el-input v-model="currentWidgetItem.placeholder" placeholder="" />
              </el-form-item>
              <el-form-item label="栅格数量"
                v-if="currentWidgetItem.widgetType !== SysCustomWidgetType.Divider &&
                  currentWidgetItem.widgetType !== SysCustomWidgetType.Text &&
                  currentWidgetItem.widgetKind !== SysCustomWidgetKind.Filter &&
                  formConfig.formType !== this.SysOnlineFormType.QUERY"
              >
                <el-slider v-model="currentWidgetItem.span" :min="1" :max="24" />
              </el-form-item>
              <el-form-item label="下边距"
                v-if="currentWidgetItem.widgetType !== SysCustomWidgetType.Divider &&
                  currentWidgetItem.widgetType !== SysCustomWidgetType.Text &&
                  currentWidgetItem.widgetType !== SysCustomWidgetType.Image &&
                  currentWidgetItem.widgetKind !== SysCustomWidgetKind.Form &&
                  currentWidgetItem.widgetKind !== SysCustomWidgetKind.Filter &&
                  formConfig.formType !== this.SysOnlineFormType.QUERY"
              >
                <el-radio-group v-model="currentWidgetItem.supportBottom">
                  <el-radio-button :label="1">支持</el-radio-button>
                  <el-radio-button :label="0">不支持</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <!-- Input属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Input">
                <el-form-item label="输入框类型"
                  v-if="currentWidgetItem.widgetKind !== SysCustomWidgetKind.Filter"
                >
                  <el-select v-model="currentWidgetItem.type" placeholder="">
                    <el-option value="text" label="单行文本" />
                    <el-option value="textarea" label="多行文本" />
                  </el-select>
                </el-form-item>
                <el-form-item label="最小行数"
                  v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Input && currentWidgetItem.type === 'textarea'">
                  <el-input-number v-model="currentWidgetItem.minRows" />
                </el-form-item>
                <el-form-item label="最大行数"
                  v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Input && currentWidgetItem.type === 'textarea'">
                  <el-input-number v-model="currentWidgetItem.maxRows" />
                </el-form-item>
              </el-row>
              <!-- Date属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Date">
                <el-form-item label="日期类型">
                  <el-select v-model="currentWidgetItem.type" placeholder="" @change="onDateTypeChange">
                    <el-option value="year" label="年（year）" />
                    <el-option value="month" label="月（month）" />
                    <el-option value="date" label="日（date）" />
                    <el-option value="datetime" label="日期时间（datetime）" />
                  </el-select>
                </el-form-item>
                <el-form-item label="显示格式">
                  <el-input v-model="currentWidgetItem.format" placeholder="请输入日期显示格式" />
                </el-form-item>
                <el-form-item label="绑定值格式">
                  <el-input v-model="currentWidgetItem.valueFormat" placeholder="请输入绑定值格式" />
                </el-form-item>
              </el-row>
              <!-- 数字输入框属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.NumberInput">
                <el-form-item label="最小值">
                  <el-input-number v-model="currentWidgetItem.min" />
                </el-form-item>
                <el-form-item label="最大值">
                  <el-input-number v-model="currentWidgetItem.max" />
                </el-form-item>
                <el-form-item label="步长">
                  <el-input-number v-model="currentWidgetItem.step" />
                </el-form-item>
                <el-form-item label="精度">
                  <el-input-number v-model="currentWidgetItem.precision" />
                </el-form-item>
                <el-form-item label="控制按钮">
                  <el-radio-group v-model="currentWidgetItem.controlVisible">
                    <el-radio-button :label="1">显示</el-radio-button>
                    <el-radio-button :label="0">隐藏</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="按钮位置">
                  <el-radio-group v-model="currentWidgetItem.controlPosition">
                    <el-radio-button :label="0">默认</el-radio-button>
                    <el-radio-button :label="1">右侧</el-radio-button>
                  </el-radio-group>
                </el-form-item>
              </el-row>
              <!-- 上传组件属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Upload">
                <el-form-item label="文件字段名">
                  <el-input v-model="currentWidgetItem.fileFieldName" placeholder="" disabled />
                </el-form-item>
                <el-form-item label="上传地址">
                  <el-input v-model="currentWidgetItem.actionUrl" placeholder="" readonly />
                </el-form-item>
                <el-form-item label="下载地址">
                  <el-input v-model="currentWidgetItem.downloadUrl" placeholder="" readonly />
                </el-form-item>
              </el-row>
              <!-- 基础卡片属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Card">
                <el-form-item label="内边距">
                  <el-input-number v-model="currentWidgetItem.padding" />
                </el-form-item>
                <el-form-item label="栅格间距">
                  <el-input-number v-model="currentWidgetItem.gutter" />
                </el-form-item>
                <el-form-item label="卡片高度">
                  <el-input-number v-model="currentWidgetItem.height" placeholder="请输入卡片高度，单位px" />
                </el-form-item>
                <el-form-item label="阴影显示">
                  <el-radio-group v-model="currentWidgetItem.shadow">
                    <el-radio-button label="always">一直显示</el-radio-button>
                    <el-radio-button label="hover">移入显示</el-radio-button>
                    <el-radio-button label="never">永不显示</el-radio-button>
                  </el-radio-group>
                </el-form-item>
              </el-row>
              <!-- 分割线属性 -->
              <el-form-item label="阴影显示"
                v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Divider">
                <el-radio-group v-model="currentWidgetItem.position">
                  <el-radio-button label="left">居左显示</el-radio-button>
                  <el-radio-button label="center">居中显示</el-radio-button>
                  <el-radio-button label="right">居右显示</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <!-- 表格属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Table">
                <el-form-item class="color-select" label="标题颜色" style="height: 32px;"
                  v-if="formConfig.formType !== SysOnlineFormType.QUERY">
                  <el-color-picker v-model="currentWidgetItem.titleColor" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="表格高度">
                  <el-input-number v-model="currentWidgetItem.tableInfo.height" placeholder="请输入表格高度，单位px" />
                </el-form-item>
                <el-form-item label="是否分页">
                  <el-switch v-model="currentWidgetItem.tableInfo.paged" key="tableInfo.paged" />
                </el-form-item>
                <el-col :span="24">
                  <el-divider>表格字段</el-divider>
                </el-col>
                <el-col :span="24" style="border-top: 1px solid #EBEEF5;">
                  <el-table :data="currentWidgetItem.tableColumnList" :show-header="false" empty-text="请选择绑定字段">
                    <el-table-column label="操作" width="45px">
                      <template slot-scope="scope">
                        <el-button class="table-btn delete" type="text" icon="el-icon-remove-outline"
                          @click="onDeleteTableColumn(scope.row)"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="表格列名" prop="showName" width="100px">
                      <template slot-scope="scope">
                        <el-button class="table-btn" type="text" style="text-decoration: underline;" @click="onAddTableColumn(scope.row)">{{scope.row.showName}}</el-button>
                      </template>
                    </el-table-column>
                    <el-table-column label="表格字段">
                      <template slot-scope="scope">
                        <span>{{(scope.row.column || {}).columnName}}</span>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-button class="full-line-btn" icon="el-icon-plus" @click="onAddTableColumn(null)">添加表格字段</el-button>
                </el-col>
              </el-row>
              <!-- 文本属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Text">
                <el-form-item class="color-select" label="背景颜色" style="height: 32px;">
                  <el-color-picker v-model="currentWidgetItem.backgroundColor" style="width: 100%;" />
                </el-form-item>
                <el-form-item class="color-select" label="文字颜色" style="height: 32px;">
                  <el-color-picker v-model="currentWidgetItem.color" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="文字大小">
                  <el-input-number v-model="currentWidgetItem.fontSize" placeholder="请输入文字大小，单位px" />
                </el-form-item>
                <el-form-item label="文字行高">
                  <el-input-number v-model="currentWidgetItem.lineHeight" placeholder="请输入文字行高，单位px" />
                </el-form-item>
                <el-form-item label="首行缩进">
                  <el-input-number v-model="currentWidgetItem.indent" placeholder="请输入首行缩进，单位em" />
                </el-form-item>
                <el-form-item label="内边距">
                  <el-input-number v-model="currentWidgetItem.padding" />
                </el-form-item>
                <el-form-item label="文本修饰">
                  <el-radio-group v-model="currentWidgetItem.decoration">
                    <el-radio-button label="none">标准</el-radio-button>
                    <el-radio-button label="underline">下划线</el-radio-button>
                    <el-radio-button label="line-through">中划线</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="对齐方式">
                  <el-radio-group v-model="currentWidgetItem.align">
                    <el-radio-button label="left">左对齐</el-radio-button>
                    <el-radio-button label="right">右对齐</el-radio-button>
                    <el-radio-button label="center">中间对齐</el-radio-button>
                  </el-radio-group>
                </el-form-item>
              </el-row>
              <!-- 图片属性 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Image">
                <el-form-item label="图片宽度">
                  <el-input-number v-model="currentWidgetItem.width" />
                </el-form-item>
                <el-form-item label="图片高度">
                  <el-input-number v-model="currentWidgetItem.height" />
                </el-form-item>
                <el-form-item label="显示模式">
                  <el-select v-model="currentWidgetItem.fit" placeholder="">
                    <el-option value="fill" label="充满" />
                    <el-option value="contain" label="包含" />
                    <el-option value="cover" label="裁剪" />
                    <el-option value="none" label="原始尺寸" />
                  </el-select>
                </el-form-item>
                <el-form-item label="图片URL">
                  <el-input v-model="currentWidgetItem.src" placeholder="" />
                </el-form-item>
              </el-row>
              <!-- 公共属性 -->
              <el-row>
                <el-col :span="12">
                  <el-form-item label="是否只读" v-if="currentWidgetItem.widgetKind === SysCustomWidgetKind.Form">
                    <el-switch v-model="currentWidgetItem.readOnly" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="是否禁用"
                    v-if="currentWidgetItem.widgetType !== SysCustomWidgetType.RichEditor &&
                      currentWidgetItem.widgetKind === SysCustomWidgetKind.Form"
                  >
                    <el-switch v-model="currentWidgetItem.disabled" />
                  </el-form-item>
                </el-col>
              </el-row>
              <!-- 字典下拉参数设置 -->
              <el-row v-if="currentWidgetItem.widgetType === SysCustomWidgetType.Select && currentWidgetDictInfo">
                <el-col :span="24">
                  <el-divider>下拉选项配置</el-divider>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="数据字典">
                    <el-input v-model="currentWidgetItem.column.dictInfo.dictName" readonly />
                  </el-form-item>
                </el-col>
                <el-col :span="24" style="border-top: 1px solid #EBEEF5;"
                  v-if="Array.isArray(currentWidgetDictInfo.paramList) && currentWidgetDictInfo.paramList.length > 0"
                >
                  <el-table :data="currentWidgetDictInfo.paramList" :show-header="false">
                    <el-table-column label="参数名称" prop="dictParamName" width="120px" />
                    <el-table-column label="参数值">
                      <template slot-scope="scope">
                        <el-button class="table-btn" type="text" style="text-decoration: underline;" @click="onEditDictParam(scope.row)">
                          <span v-if="scope.row.dictValueType === SysOnlineParamValueType.FORM_PARAM">
                            {{(getFormParam(scope.row.dictValue) || {}).columnName}}
                          </span>
                          <span v-else-if="scope.row.dictValueType === SysOnlineParamValueType.TABLE_COLUMN">
                            {{(getTableColumn(scope.row.dictValue) || {}).columnName}}
                          </span>
                          <span v-else-if="scope.row.dictValueType === SysOnlineParamValueType.STATIC_DICT">
                            {{getDictValueShowName(scope.row.dictValue)}}
                          </span>
                          <span v-else-if="scope.row.dictValueType === SysOnlineParamValueType.INPUT_VALUE">
                            {{scope.row.dictValue}}
                          </span>
                          <span v-else>尚未设置参数值</span>
                        </el-button>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="45px">
                      <template slot-scope="scope">
                        <el-button class="table-btn delete" type="text" icon="el-icon-remove-outline"
                          :disabled="scope.row.dictValue == null" @click="onRemoveDictParam(scope.row)"
                        />
                      </template>
                    </el-table-column>
                  </el-table>
                </el-col>
              </el-row>
            </el-form>
          </el-row>
          <el-row v-if="currentWidgetItem == null"
            class="scroll-box" :style="{height: height - 50 + 'px'}">
            <el-form class="full-width-input" size="small" label-position="left" label-width="90px">
              <el-form-item label="表单类别">
                <el-select v-model="formConfig.formKind" placeholder="">
                  <el-option v-for="item in SysOnlineFormKind.getList()" :key="item.id"
                    :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="表单类型">
                <el-input :value="SysOnlineFormType.getValue(formConfig.formType)" readonly disabled />
              </el-form-item>
              <el-form-item label="标签位置">
                <el-radio-group v-model="formConfig.labelPosition">
                  <el-radio-button label="left">居左</el-radio-button>
                  <el-radio-button label="right">居右</el-radio-button>
                  <el-radio-button label="top">顶部</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="标签宽度">
                <el-input-number v-model="formConfig.labelWidth" />
              </el-form-item>
              <el-form-item label="栅格间距">
                <el-input-number v-model="formConfig.gutter" />
              </el-form-item>
              <el-form-item label="弹窗宽度" v-if="formConfig.formKind === SysOnlineFormKind.DIALOG">
                <el-input-number v-model="formConfig.width" />
              </el-form-item>
              <el-form-item label="弹窗高度" v-if="formConfig.formKind === SysOnlineFormKind.DIALOG">
                <el-input-number v-model="formConfig.height" />
              </el-form-item>
            </el-form>
          </el-row>
          <!--</el-scrollbar>-->
        </el-tab-pane>
        <el-tab-pane label="操作管理" name="operation" v-if="currentWidgetItem && currentWidgetItem.widgetType === SysCustomWidgetType.Table">
          <el-row v-if="currentWidgetItem != null && currentWidgetItem.widgetType === SysCustomWidgetType.Table"
            class="scroll-box" :style="{height: height - 50 + 'px'}">
            <el-form class="full-width-input" size="small" label-position="left" label-width="90px">
              <el-row>
                <el-col :span="24">
                  <el-form-item label="操作列宽度">
                    <el-input-number v-model="currentWidgetItem.tableInfo.optionColumnWidth" placeholder="请输入表格高度，单位px" />
                  </el-form-item>
                </el-col>
                <el-col :span="24" style="border-top: 1px solid #EBEEF5;">
                  <el-table :data="currentWidgetItem.operationList" :show-header="false">
                    <el-table-column label="操作" width="45px">
                      <template slot-scope="scope">
                        <el-button class="table-btn delete" type="text" icon="el-icon-remove-outline"
                          :disabled="scope.row.builtin" @click="onDeleteTableOperation(scope.row)"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="操作名称" prop="name">
                      <template slot-scope="scope">
                        <el-button class="table-btn" type="text" style="text-decoration: underline;" @click="onAddTableOperation(scope.row)">{{scope.row.name}}</el-button>
                      </template>
                    </el-table-column>
                    <el-table-column label="是否启动" prop="enabled" width="70px">
                      <template slot-scope="scope">
                        <el-switch v-model="scope.row.enabled" />
                      </template>
                    </el-table-column>
                    <el-table-column label="操作类型" prop="type" width="90px">
                      <template slot-scope="scope">
                        <el-tag size="mini" effect="dark" :type="scope.row.rowOperation ? 'success' : 'warning'">
                          {{scope.row.rowOperation ? '行内操作' : '表格操作'}}
                        </el-tag>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-button class="full-line-btn" icon="el-icon-plus" @click="onAddTableOperation(null)"
                    :disabled="formConfig.formType === SysOnlineFormType.WORK_ORDER"
                  >
                    添加自定义操作
                  </el-button>
                </el-col>
                <el-col :span="24">
                  <el-divider>查询参数设置</el-divider>
                </el-col>
                <el-col :span="24" style="border-top: 1px solid #EBEEF5;">
                  <el-table :data="currentWidgetItem.queryParamList" :show-header="false">
                    <el-table-column label="操作" width="45px">
                      <template slot-scope="scope">
                        <el-button class="table-btn delete" type="text" icon="el-icon-remove-outline"
                          @click="onRemoveQueryParam(scope.row)"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="参数名称" width="120px">
                      <template slot-scope="scope">
                        <el-tag size="mini" effect="dark" :type="scope.row.table.relationType == null ? 'success' : 'primary'">
                          {{scope.row.column.columnName}}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column label="参数值">
                      <template slot-scope="scope">
                        <span v-if="scope.row.paramValueType === SysOnlineParamValueType.FORM_PARAM">
                          {{(getFormParam(scope.row.paramValue) || {}).columnName}}
                        </span>
                        <span v-else-if="scope.row.paramValueType === SysOnlineParamValueType.TABLE_COLUMN">
                          {{(getTableColumn(scope.row.paramValue) || {}).columnName}}
                        </span>
                        <span v-else-if="scope.row.paramValueType === SysOnlineParamValueType.STATIC_DICT">
                          {{getDictValueShowName(scope.row.paramValue)}}
                        </span>
                        <span v-else-if="scope.row.paramValueType === SysOnlineParamValueType.INPUT_VALUE">
                          {{scope.row.paramValue}}
                        </span>
                        <span v-else>尚未设置参数值</span>
                      </template>
                    </el-table-column>
                    
                  </el-table>
                  <el-button class="full-line-btn" icon="el-icon-plus" @click="onAddQueryParam(null)"
                    :disabled="formConfig.formType === SysOnlineFormType.WORK_ORDER"
                  >
                    添加查询参数
                  </el-button>
                </el-col>
              </el-row>
            </el-form>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="表单参数" name="operation" v-if="currentWidgetItem == null">
          <el-row class="scroll-box" :style="{height: height - 50 + 'px'}">
            <el-form class="full-width-input" size="small" label-position="left" label-width="90px">
              <el-col :span="24" style="border-top: 1px solid #EBEEF5;">
                <el-table :data="formConfig.paramList" :show-header="false">
                  <el-table-column label="操作" width="45px">
                    <template slot-scope="scope">
                      <el-button class="table-btn delete" type="text" icon="el-icon-remove-outline"
                        :disabled="scope.row.builtin" @click="onDeleteFormParam(scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="参数名" prop="columnName" />
                  <el-table-column label="参数类型" width="100px">
                    <template slot-scope="scope">
                      <el-tag v-if="scope.row.primaryKey" size="mini" type="warning">主键</el-tag>
                      <el-tag v-if="scope.row.slaveColumn" size="mini" type="primary">关联字段</el-tag>
                    </template>
                  </el-table-column>
                </el-table>
                <!--
                <el-button class="full-line-btn" icon="el-icon-plus" @click="onAddFormParam(null)">添加表单参数</el-button>
                -->
              </el-col>
            </el-form>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-row>
</template>

<script>
import Vue from 'vue';
import { findItemFromList } from '@/utils';
import { mapGetters } from 'vuex';
import * as StaticDict from '@/staticDict';
import Draggable from 'vuedraggable';
import { defaultWidgetAttributes, baseWidgetList } from '../data/onlineFormOptions.js';
import DragWidgetItem from './editableWidgetItem.vue';
import DraggableFilterBox from './dragableFilterBox.vue';
import EditWidgetTableOperation from './editWidgetTableOperation.vue';
import EditWidgetTableColumn from './editWidgetTableColumn.vue';
import EditDictParamValue from './editDictParamValue.vue';
import EditFormParam from './editFormParam.vue';
import EditTableQueryParam from './editTableQueryParam.vue';
import OnlineFormPreview from '@/views/onlineForm/index.vue';
import { OnlineFormController } from '@/api/onlineController.js';
Vue.component('drag-widget-item', DragWidgetItem);

export default {
  props: {
    pageType: {
      type: Number,
      required: true
    },
    height: {
      type: Number,
      required: true
    },
    tableList: {
      type: Array,
      required: true
    },
    datasourceTableList: {
      type: Array,
      required: true
    },
    formList: {
      type: Array,
      required: true
    },
    form: {
      type: Object,
      required: true
    },
    formDatasourceList: {
      type: Array,
      required: true
    }
  },
  components: {
    Draggable,
    DraggableFilterBox
  },
  data () {
    return {
      flowWorkStatus: undefined,
      workflowFilterWidget: {},
      baseWidgetList: baseWidgetList,
      formWidgetList: [],
      formConfig: {},
      currentWidgetDictInfo: {},
      activeAttributeName: 'widget',
      isLocked: false,
      currentWidgetItem: undefined,
      columnUseCount: {},
      widgetVariableNameSet: new Set()
    }
  },
  provide () {
    return {
      current: () => this.currentWidgetItem,
      isLocked: () => this.isLocked,
      formConfig: () => this.formConfig,
      preview: () => true
    }
  },
  methods: {
    goBack () {
      this.$confirm('是否退出设计页面，请确保表单设计的修改已经保存！', '表单设计', {
        type: 'warning',
        cancelButtonText: '取消',
        confirmButtonText: '退出'
      }).then(res => {
        this.$emit('close');
      }).catch(e => {});
    },
    onReset () {
      this.$confirm('是否重置表单组件？', '表单设计', {
        type: 'warning'
      }).then(res => {
        this.widgetVariableNameSet.clear();
        if (this.formConfig.formType === this.SysOnlineFormType.QUERY) {
          this.widgetVariableNameSet.add(this.formConfig.tableWidget.variableName);
        }
        this.formWidgetList = [];
      }).catch(e => {});
    },
    onPreview () {
      this.onSave().then(res => {
        let dialogPos = {
          area: this.formConfig.formType === this.SysOnlineFormType.QUERY ? ['70vw', '80vh'] : this.formConfig.width + 'px',
          offset: this.formConfig.formType === this.SysOnlineFormType.QUERY ? undefined : '100px'
        }
        this.$dialog.show(this.form.formName, OnlineFormPreview, dialogPos, {
          formId: this.form.formId,
          formCode: this.form.formCode,
          formType: this.form.formType,
          operationType: this.SysCustomWidgetOperationType.ADD,
          isPreview: true
        }).then(res => {}).catch(e => {});
      }).catch(e => {});
    },
    onSave () {
      let onlineFormDto = {
        formId: this.form.formId,
        pageId: this.form.pageId,
        formCode: this.form.formCode,
        formName: this.form.formName,
        formKind: this.formConfig.formKind,
        formType: this.form.formType,
        masterTableId: this.form.masterTableId
      }

      let formConfig = {
        formKind: this.formConfig.formKind,
        formType: this.form.formType,
        gutter: this.formConfig.gutter,
        labelWidth: this.formConfig.labelWidth,
        labelPosition: this.formConfig.labelPosition,
        width: this.formConfig.width,
        height: this.formConfig.height,
        paramList: this.formConfig.paramList
      };

      if (this.formConfig.tableWidget != null) {
        formConfig.tableWidget = {
          ...this.formConfig.tableWidget,
          tableId: this.formConfig.tableWidget.table.tableId,
          table: undefined,
          tableColumnList: Array.isArray(this.formConfig.tableWidget.tableColumnList) ? this.formConfig.tableWidget.tableColumnList.map(tableColumn => {
            return {
              ...tableColumn,
              column: undefined,
              table: undefined
            }
          }) : [],
          queryParamList: Array.isArray(this.formConfig.tableWidget.queryParamList) ? this.formConfig.tableWidget.queryParamList.map(param => {
            return {
              ...param,
              column: undefined,
              table: undefined
            }
          }) : []
        }
      }

      let widgetList = this.formWidgetList.map(widget => {
        let dictParamList = null;
        if (widget.column && widget.column.dictInfo) {
          dictParamList = widget.column.dictInfo.paramList || widget.dictParamList;
        }
        return {
          ...widget,
          dictParamList,
          queryParamList: Array.isArray(widget.queryParamList) ? widget.queryParamList.map(param => {
            return {
              ...param,
              relation: undefined,
              column: undefined,
              table: undefined
            }
          }) : [],
          tableColumnList: Array.isArray(widget.tableColumnList) ? widget.tableColumnList.map(tableColumn => {
            return {
              ...tableColumn,
              relation: undefined,
              column: undefined,
              table: undefined
            }
          }) : [],
          table: undefined,
          column: undefined,
          columnList: undefined
        }
      });

      onlineFormDto.widgetJson = JSON.stringify({
        formConfig,
        widgetList
      });

      onlineFormDto.datasourceIdList = this.formDatasourceList.map(item => {
        return item.datasourceId;
      });

      onlineFormDto.paramsJson = JSON.stringify(this.getFormParameterList);
      return new Promise((resolve, reject) => {
        OnlineFormController.update(this, {
          onlineFormDto
        }).then(res => {
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    onSaveClick () {
      this.onSave().then(res => {
        this.$message.success('保存成功！');
      }).catch(e => {});
    },
    onAddQueryParam () {
      let tableFilterColumnList = [];
      if (this.getMasterTable != null && Array.isArray(this.getMasterTable.columnList)) {
        tableFilterColumnList = this.tableList.map(table => {
          return {
            tableId: table.tableId,
            tableName: table.tableName,
            columnList: Array.isArray(table.columnList) ? table.columnList.filter(column => {
              return (this.formConfig.formType !== this.SysOnlineFormType.QUERY || column.filterType !== this.SysOnlineColumnFilterType.NONE);
            }) : []
          }
        });
      }
      this.$dialog.show('添加查询参数', EditTableQueryParam, {
        area: '600px'
      }, {
        tableList: this.getWidgetColumnTableList(this.currentWidgetItem.table),
        usedColumnList: (this.currentWidgetItem.queryParamList || []).map(item => item.columnId),
        formParamList: this.formConfig.paramList,
        tableFilterColumnList: tableFilterColumnList
      }).then(res => {
        if (this.currentWidgetItem.queryParamList == null) this.currentWidgetItem.queryParamList = [];
        this.currentWidgetItem.queryParamList.push(res);
      }).catch(e => {});
    },
    onRemoveQueryParam (row) {
      this.$confirm('是否移除此表格过滤参数？').then(res => {
        this.currentWidgetItem.queryParamList = this.currentWidgetItem.queryParamList.filter(param => {
          return param.columnId !== row.columnId;
        });
      }).catch(e => {});
    },
    onAddFormParam () {
      let validColumnList = this.getMasterTable.columnList.filter(column => {
        let usedColumn = findItemFromList(this.formConfig.paramList, column.columnName, 'columnName');
        return usedColumn == null;
      });
      this.$dialog.show('添加表单参数', EditFormParam, {
        area: '600px'
      }, {
        columnList: validColumnList
      }).then(res => {
        this.formConfig.paramList.push(res);
      }).catch(e => {});
    },
    onDeleteFormParam (row) {
      this.$confirm('是否删除此表单参数？').then(res => {
        this.formConfig.paramList = this.formConfig.paramList.filter(item => {
          return item.columnName !== row.columnName;
        });
      }).catch(e => {});
    },
    getFormParam (paramName) {
      return findItemFromList(this.formConfig.paramList, paramName, 'columnName');
    },
    getTableColumn (columnId) {
      for (let i = 0; i < this.tableList.length; i++) {
        let table = this.tableList[i];
        if (table && Array.isArray(table.columnList)) {
          let column = findItemFromList(table.columnList, columnId, 'columnId');
          if (column != null) return column;
        }
      }

      return null;
    },
    getDictValueShowName (dictValue) {
      let staticDict = StaticDict[dictValue[0]];
      if (staticDict != null) {
        return staticDict.showName + ' / ' + staticDict.getValue(dictValue[1]);
      }
    },
    onEditDictParam (param) {
      let columnList = [];
      if (this.getMasterTable != null && Array.isArray(this.getMasterTable.columnList)) {
        columnList = this.getMasterTable.columnList.filter(item => {
          return (this.formConfig.formType !== this.SysOnlineFormType.QUERY || item.filterType !== this.SysOnlineColumnFilterType.NONE);
        });
      }
      this.$dialog.show('字典参数设置', EditDictParamValue, {
        area: '600px'
      }, {
        rowData: param,
        formParamList: this.formConfig.paramList,
        columnList: columnList
      }).then(res => {
        this.currentWidgetDictInfo.paramList = this.currentWidgetDictInfo.paramList.map(item => {
          return item.dictParamName === param.dictParamName ? res : item;
        });
        this.currentWidgetDictInfo = {
          ...this.currentWidgetDictInfo
        }
      }).catch(e => {
        console.log(e);
      });
    },
    onRemoveDictParam (param) {
      this.currentWidgetDictInfo.paramList = this.currentWidgetDictInfo.paramList.map(item => {
        if (item.dictParamName === param.dictParamName) {
          item.dictValueType = undefined;
          item.dictValue = undefined;
        }

        return item;
      });
    },
    getWidgetColumnTableList (table) {
      let tableList = [];
      if (table != null) {
        tableList.push(table);
        if (table.tag && Array.isArray(table.tag.relationList)) {
          table.tag.relationList.forEach(relation => {
            if (relation.relationType === this.SysOnlineRelationType.ONE_TO_ONE) {
              let tableInfo = findItemFromList(this.datasourceTableList, relation.slaveTable.tableName, 'tableName');
              if (tableInfo != null) {
                tableList.push(tableInfo);
              }
            }
          });
        }
      }

      return tableList;
    },
    onAddTableColumn (column) {
      let maxShowOrder = 0;
      this.currentWidgetItem.tableColumnList.forEach(item => {
        maxShowOrder = Math.max(item.showOrder, maxShowOrder);
      });
      maxShowOrder++;
      this.$dialog.show(column ? '编辑字段' : '添加字段', EditWidgetTableColumn, {
        area: '600px'
      }, {
        showOrder: maxShowOrder,
        rowData: column,
        tableList: this.getWidgetColumnTableList(this.currentWidgetItem.table),
        usedColumnList: this.currentWidgetItem.tableColumnList.map(item => item.columnId)
      }).then(res => {
        if (column == null) {
          this.currentWidgetItem.tableColumnList.push(res);
        } else {
          this.currentWidgetItem.tableColumnList = this.currentWidgetItem.tableColumnList.map(item => {
            return (item.columnId === column.columnId) ? res : item;
          });
        }
        this.currentWidgetItem.tableColumnList = this.currentWidgetItem.tableColumnList.sort((value1, value2) => {
          return value1.showOrder - value2.showOrder;
        });
      }).catch(e => {});
    },
    onDeleteTableColumn (column) {
      if (column == null) return;
      this.$confirm('是否删除此表格列？').then(res => {
        this.currentWidgetItem.tableColumnList = this.currentWidgetItem.tableColumnList.filter(item => {
          return item.columnId !== column.columnId;
        });
      }).catch(e => {});
    },
    onAddTableOperation (operation) {
      this.$dialog.show(operation ? '编辑操作' : '新建操作', EditWidgetTableOperation, {
        area: '600px'
      }, {
        rowData: operation,
        formList: this.formList.filter(item => item.formId !== this.form.formId)
      }).then(res => {
        if (operation == null) {
          let maxId = 0;
          this.currentWidgetItem.operationList.forEach(item => {
            maxId = Math.max(maxId, item.id);
          });
          res.id = ++maxId;
          this.currentWidgetItem.operationList.push(res);
        } else {
          this.currentWidgetItem.operationList = this.currentWidgetItem.operationList.map(item => {
            return (item.id === operation.id) ? res : item;
          });
        }
      }).catch(e => {});
    },
    onDeleteTableOperation (operation) {
      if (operation == null) return;
      this.$confirm('是否删除此操作？').then(res => {
        this.currentWidgetItem.operationList = this.currentWidgetItem.operationList.filter(item => {
          return item.id !== operation.id;
        });
      }).catch(e => {});
    },
    getFilterRootElement () {
      return this.$refs.filterBox.$el;
    },
    onTableColumnOrderChange () {
      this.currentWidgetItem.tableColumnList = this.currentWidgetItem.tableColumnList.sort((value1, value2) => {
        return (value1.showOrder || 0) - (value2.showOrder || 0);
      });
    },
    getTableColumnList (table) {
      if (table && table.columnList) {
        let temp = [
          ...table.columnList.filter(item => {
            let usedWidget = findItemFromList(this.formWidgetList, item.columnId, 'columnId');
            return (
              this.formConfig.formType !== this.SysOnlineFormType.QUERY ||
              (!item.isVirtualColumn && item.filterType !== this.SysOnlineColumnFilterType.NONE && !usedWidget)
            );
          }).map(item => {
            return {
              ...item,
              relationId: table.relationType == null ? undefined : table.id,
              datasourceId: table.relationType == null ? table.id : table.tag.datasourceId
            }
          })
        ];
        if (table && table.relationType === this.SysOnlineRelationType.ONE_TO_MANY && this.getMasterTable !== table) {
          temp.unshift({
            id: table.id,
            tableId: table.tableId,
            columnName: table.tag.variableName,
            objectFieldName: table.tag.variableName,
            relationId: table.id,
            columnComment: table.tag.relationName,
            widgetType: this.SysCustomWidgetType.Table,
            columnList: table.columnList,
            table: table
          });
        }
        return temp;
      } else {
        return [];
      }
    },
    onColumnClick (column) {
      if (this.currentWidgetItem != null && this.currentWidgetItem.widgetType === this.SysCustomWidgetType.Card) {
        this.currentWidgetItem.childWidgetList.push(this.cloneComponent(column));
      } else {
        this.formWidgetList.push(this.cloneComponent(column));
      }
    },
    onFormClick () {
      if (this.currentWidgetItem != null) this.activeAttributeName = 'widget';
      this.currentWidgetItem = null;
    },
    buildUploadWidgetUrlInfo (widget) {
      let datasource = findItemFromList(this.datasourceTableList, widget.datasourceId, 'id');
      let actionUrl = '/admin/online/onlineOperation/' + (widget.relationId ? 'uploadOneToManyRelation/' : 'uploadDatasource/') + datasource.tag.variableName;
      let downloadUrl = '/admin/online/onlineOperation/' + (widget.relationId ? 'downloadOneToManyRelation/' : 'downloadDatasource/') + datasource.tag.variableName;
      if (this.pageType === this.SysOnlinePageType.FLOW) {
        actionUrl = '/admin/flow/flowOnlineOperation/upload';
        downloadUrl = '/admin/flow/flowOnlineOperation/download';
      }

      return {
        actionUrl,
        downloadUrl
      }
    },
    /**
     * 选中组件
     */
    onWidgetClick (widget) {
      if (widget === this.currentWidgetItem) return;
      this.currentWidgetDictInfo = undefined;
      if (widget != null) {
        // 获取下拉字典信息
        if (widget.column && widget.column.dictInfo && widget.column.dictInfo.paramList == null) {
          if (widget.dictParamList) {
            widget.column.dictInfo.paramList = widget.dictParamList;
          } else {
            widget.column.dictInfo.paramList = (JSON.parse(widget.column.dictInfo.dictDataJson) || {}).paramList;
          }
        }
        if (widget.column) this.currentWidgetDictInfo = widget.column.dictInfo;
      }
      this.currentWidgetItem = widget;
      if (this.currentWidgetItem.widgetType === this.SysCustomWidgetType.Table) {
        this.buildTableWidgetInfo(this.currentWidgetItem);
      }
      if (this.currentWidgetItem.widgetType === this.SysCustomWidgetType.Upload) {
        let urlInfo = this.buildUploadWidgetUrlInfo(this.currentWidgetItem);
        this.currentWidgetItem.actionUrl = urlInfo.actionUrl;
        this.currentWidgetItem.downloadUrl = urlInfo.downloadUrl;
      }
      this.activeAttributeName = 'widget';
    },
    /**
     * 删除组件
     */
    onWidgetDeleteClick (widget, onlyDeleteName = false) {
      this.widgetVariableNameSet.delete(widget.variableName);
      if (!onlyDeleteName) {
        this.formWidgetList = this.formWidgetList.filter(item => {
          return widget.id !== item.id;
        });
      }
      if (this.currentWidgetItem === widget) {
        this.currentWidgetItem = null;
        this.activeAttributeName = 'widget';
      }
    },
    getWidgetAttributes (widget) {
      switch (widget.widgetType) {
        case this.SysCustomWidgetType.Label:
          return { ...defaultWidgetAttributes.label };
        case this.SysCustomWidgetType.Input:
          return { ...defaultWidgetAttributes.input };
        case this.SysCustomWidgetType.NumberInput:
          return { ...defaultWidgetAttributes.numberInput };
        case this.SysCustomWidgetType.Switch:
          return { ...defaultWidgetAttributes.switch };
        case this.SysCustomWidgetType.Select:
          return { ...defaultWidgetAttributes.select };
        case this.SysCustomWidgetType.Cascader:
          return { ...defaultWidgetAttributes.cascader };
        case this.SysCustomWidgetType.Date:
          return { ...defaultWidgetAttributes.date };
        case this.SysCustomWidgetType.RichEditor:
          return { ...defaultWidgetAttributes.richEditor };
        case this.SysCustomWidgetType.Upload:
          return { ...defaultWidgetAttributes.upload };
        case this.SysCustomWidgetType.Divider:
          return { ...defaultWidgetAttributes.divider };
        case this.SysCustomWidgetType.Text:
          return { ...defaultWidgetAttributes.text };
        case this.SysCustomWidgetType.Image:
          return { ...defaultWidgetAttributes.image };
        case this.SysCustomWidgetType.Table:
          return {
            ...defaultWidgetAttributes.table,
            tableInfo: { ...defaultWidgetAttributes.table.tableInfo }
          }
        case this.SysCustomWidgetType.Card:
          return { ...defaultWidgetAttributes.card };
        case this.SysCustomWidgetType.NumberRange:
          return { ...defaultWidgetAttributes.numberRange };
        case this.SysCustomWidgetType.DateRange:
          return { ...defaultWidgetAttributes.dateRange };
      }
    },
    /**
     * 创建基础组件
     */
    onBaseWidgetClick (widget) {
      if (this.currentWidgetItem != null && this.currentWidgetItem.widgetType === this.SysCustomWidgetType.Card) {
        this.currentWidgetItem.childWidgetList.push(this.cloneBaseWidget(widget));
      } else {
        this.formWidgetList.push(this.cloneBaseWidget(widget));
      }
    },
    generatorWidgetVariableName (variableName) {
      let index = 0;
      let name = variableName;
      do {
        if (!this.widgetVariableNameSet.has(name)) {
          this.widgetVariableNameSet.add(name);
          break;
        }
        index++;
        name = variableName + index;
      } while (true);
      return name;
    },
    cloneBaseWidget (widget) {
      let attrs = this.getWidgetAttributes(widget);
      return {
        ...attrs,
        id: new Date().getTime(),
        columnName: widget.columnName,
        showName: widget.showName,
        variableName: this.generatorWidgetVariableName(widget.columnName),
        widgetKind: this.formConfig.formType === this.SysOnlineFormType.QUERY ? this.SysCustomWidgetKind.Filter : attrs.widgetKind
      }
    },
    /**
     * 根据字段创建组件
     */
    getColumnDefaultAttributes (column) {
      if (column == null) return {};
      // 查询页面的范围查询
      if (column.filterType === this.SysOnlineColumnFilterType.RANFGE_FILTER && this.formConfig.formType === this.SysOnlineFormType.QUERY) {
        if (column.objectFieldType === 'Date') {
          return { ...defaultWidgetAttributes.dateRange };
        } else {
          return { ...defaultWidgetAttributes.numberRange };
        }
      }
      switch (column.fieldKind) {
        case this.SysOnlineFieldKind.UPLOAD:
        case this.SysOnlineFieldKind.UPLOAD_IMAGE:
        {
          return {
            ...defaultWidgetAttributes.upload,
            isImage: column.fieldKind === this.SysOnlineFieldKind.UPLOAD_IMAGE,
            ...this.buildUploadWidgetUrlInfo(column)
          };
        }
        case this.SysOnlineFieldKind.RICH_TEXT:
          return { ...defaultWidgetAttributes.richEditor };
        case this.SysOnlineFieldKind.CREATE_TIME:
        case this.SysOnlineFieldKind.UPDATE_TIME:
          return { ...defaultWidgetAttributes.date };
        case this.SysOnlineFieldKind.CREATE_USER_ID:
        case this.SysOnlineFieldKind.UPDATE_USER_ID:
        case this.SysOnlineFieldKind.LOGIC_DELETE:
          return { ...defaultWidgetAttributes.label };
      }
      if (column.dictId != null && column.dictId !== '' && column.dictInfo != null) {
        return column.dictInfo.treeFlag ? { ...defaultWidgetAttributes.cascader } : { ...defaultWidgetAttributes.select };
      }
      // 如果是虚拟字段
      if (column.isVirtualColumn) {
        return {
          ...defaultWidgetAttributes.label
        }
      }

      if (column.objectFieldType === 'Boolean') {
        return { ...defaultWidgetAttributes.switch }
      } else if (column.objectFieldType === 'Date') {
        return { ...defaultWidgetAttributes.date };
      } else if (column.objectFieldType === 'String') {
        return { ...defaultWidgetAttributes.input };
      } else {
        return { ...defaultWidgetAttributes.numberInput };
      }
    },
    cloneComponent (columnItem) {
      let attrs = null;
      if (columnItem.table != null) {
        attrs = {
          ...defaultWidgetAttributes.table,
          tableColumnList: [],
          operationList: [...defaultWidgetAttributes.table.operationList],
          queryParamList: [],
          tableInfo: { ...defaultWidgetAttributes.table.tableInfo }
        }
      } else {
        attrs = this.getColumnDefaultAttributes(columnItem);
      }

      return {
        ...attrs,
        id: new Date().getTime(),
        datasourceId: columnItem.datasourceId,
        relationId: columnItem.relationId,
        tableId: columnItem.tableId,
        columnId: columnItem.columnId,
        columnName: columnItem.columnName,
        showName: columnItem.columnComment,
        variableName: this.generatorWidgetVariableName(columnItem.objectFieldName),
        widgetKind: this.formConfig.formType === this.SysOnlineFormType.QUERY ? this.SysCustomWidgetKind.Filter : attrs.widgetKind,
        column: columnItem,
        table: columnItem.table
      }
    },
    getTableTagType (relationType) {
      switch (relationType) {
        case this.SysOnlineRelationType.ONE_TO_ONE: return 'success';
        case this.SysOnlineRelationType.ONE_TO_MANY: return 'warning';
        default: return 'danger';
      }
    },
    getTableRelationName (relationType) {
      switch (relationType) {
        case this.SysOnlineRelationType.ONE_TO_ONE: return '一对一';
        case this.SysOnlineRelationType.ONE_TO_MANY: return '一对多';
        default: return '主表';
      }
    },
    onDateTypeChange (type) {
      switch (type) {
        case 'year':
          this.currentWidgetItem.format = 'yyyy';
          this.currentWidgetItem.valueFormat = 'yyyy';
          break;
        case 'month':
          this.currentWidgetItem.format = 'yyyy-MM';
          this.currentWidgetItem.valueFormat = 'yyyy-MM';
          break;
        case 'date':
          this.currentWidgetItem.format = 'yyyy-MM-dd';
          this.currentWidgetItem.valueFormat = 'yyyy-MM-dd';
          break;
        case 'datetime':
          this.currentWidgetItem.format = 'yyyy-MM-dd HH:mm:ss';
          this.currentWidgetItem.valueFormat = 'yyyy-MM-dd HH:mm:ss';
          break;
      }
    },
    getTableOperation (rowOperation) {
      return this.formConfig.tableWidget.operationList.filter(operation => {
        return operation.rowOperation === rowOperation && operation.enabled;
      });
    },
    buildTableWidgetInfo (tableWidget) {
      if (tableWidget != null) {
        let table = findItemFromList(this.tableList, tableWidget.tableId, 'tableId');
        if (table != null) {
          tableWidget.datasourceId = table.relationType == null ? table.id : undefined;
          tableWidget.relationId = table.relationType == null ? undefined : table.id;
          tableWidget.table = table;
          if (Array.isArray(table.columnList) && Array.isArray(tableWidget.tableColumnList)) {
            tableWidget.tableColumnList.forEach(item => {
              let columnTable = findItemFromList(this.datasourceTableList, item.tableId, 'tableId');
              item.table = columnTable;
              item.column = findItemFromList(columnTable.columnList, item.columnId, 'columnId');
            });
          }
          if (Array.isArray(table.columnList) && Array.isArray(tableWidget.queryParamList)) {
            tableWidget.queryParamList.forEach(item => {
              let columnTable = findItemFromList(this.datasourceTableList, item.tableId, 'tableId');
              item.table = columnTable;
              item.column = findItemFromList(columnTable.columnList, item.columnId, 'columnId');
            });
          }
        }
      }
    },
    checkFormWidgetList () {
      let that = this;
      function checkFormWidget (widget) {
        if (widget && widget.variableName) {
          widget.hasError = false;
          // 判断组件绑定字段所属数据源或者关联是否存在
          let rootId = widget.datasourceId || widget.relationId;
          let datasource = findItemFromList(that.datasourceTableList, widget.datasourceId, 'id');
          let relation = findItemFromList(that.datasourceTableList, widget.relationId, 'id');
          if (datasource != null || relation != null) {
            let root = findItemFromList(that.datasourceTableList, rootId, 'id');
            widget.table = findItemFromList(that.datasourceTableList, widget.tableId, 'tableId');
            if (widget.table == null || (relation == null && widget.tableId !== datasource.tableId)) {
              widget.hasError = true;
              widget.errorMessage = '组件 [' + widget.showName + '] 绑定数据表并不属于' + (widget.datasourceId ? '数据源 [' : '数据源关联 [') + (root.tag.datasourceName || root.tag.relationName) + ']';
            } else {
              if (widget.columnId != null) {
                widget.column = findItemFromList(widget.table.columnList, widget.columnId, 'columnId');
                if (widget.column == null) {
                  widget.hasError = true;
                  widget.errorMessage = '组件 [' + widget.showName + ' ]绑定数据表字段并不属于数据表 [' + widget.table.tableName + ' ]';
                }
              }
            }
          } else {
            if (rootId != null) {
              widget.hasError = true;
              widget.errorMessage = widget.datasourceId == null ? ('组件 [' + widget.showName + ' ]绑定字段所属关联不存在！') : ('组件 [' + widget.showName + ' ]绑定字段所属数据源不存在！');
            }
          }

          if (widget.hasError) {
            console.error(widget.errorMessage);
          }
          
          that.widgetVariableNameSet.add(widget.variableName);
          if (Array.isArray(widget.childWidgetList)) {
            widget.childWidgetList.forEach(subWidget => {
              checkFormWidget(subWidget);
            })
          }
        }
      }
      this.formWidgetList.forEach(widget => {
        checkFormWidget(widget);
      });
      checkFormWidget(this.formConfig.tableWidget);
    }
  },
  computed: {
    getMasterTable () {
      return this.tableList[0];
    },
    getFormParameterList () {
      if (this.getMasterTable != null) {
        return this.getMasterTable.columnList.filter(item => {
          if (this.formConfig.formType === this.SysOnlineFormType.QUERY) {
            // 查询页面表单参数为主表的查询字段
            return item.filterType !== this.SysOnlineColumnFilterType.NONE;
          } else {
            // 编辑页面表单参数为主表的主键字段
            return item.primaryKey;
          }
        });
      } else {
        return [];
      }
    },
    ...mapGetters(['getClientWidth'])
  },
  mounted () {
    let formJsonData = JSON.parse(this.form.widgetJson);
    this.formWidgetList = formJsonData.widgetList || [];
    this.formConfig = {
      ...formJsonData.formConfig,
      formType: this.form.formType,
      formKind: this.form.formKind
    };
    // 初始化表单的参数
    this.formConfig.paramList = [];
    if (this.getMasterTable.relationType != null || this.formConfig.formType !== this.SysOnlineFormType.QUERY) {
      // 编辑表单必须包含主表的主键id
      if (this.formConfig.formType !== this.SysOnlineFormType.QUERY) {
        let primaryKeyColumn = findItemFromList(this.getMasterTable.columnList, true, 'primaryKey');
        if (primaryKeyColumn != null) {
          this.formConfig.paramList.unshift({
            columnName: primaryKeyColumn.columnName,
            primaryKey: true,
            slaveClumn: false,
            builtin: true
          });
        }
      }
      // 一对多从表的查询页面，必须包含从表关联字段
      if (this.formConfig.formType === this.SysOnlineFormType.QUERY && this.getMasterTable.relationType === this.SysOnlineRelationType.ONE_TO_MANY) {
        let slaveColumn = findItemFromList(this.getMasterTable.columnList, this.getMasterTable.tag.slaveColumnId, 'columnId');
        if (slaveColumn != null) {
          this.formConfig.paramList.unshift({
            columnName: slaveColumn.columnName,
            primaryKey: false,
            slaveColumn: true,
            builtin: true
          });
        }
      }
    }
    if (this.formConfig.formType === this.SysOnlineFormType.QUERY) {
      this.buildTableWidgetInfo(this.formConfig.tableWidget);
      this.widgetVariableNameSet.add(this.formConfig.tableWidget.variableName);
    }
    this.checkFormWidgetList();
  }
}
</script>

<style scoped>
  .form-genarated >>> .el-scrollbar__wrap {
    overflow-x: hidden;
  }

  .color-select >>> .el-color-picker__trigger {
    width: 100%;
  }

  .attribute-box >>> .el-tabs__item {
    width: 175px;
    text-align: center;
  }

  .attribute-box >>> .el-tabs__active-bar {
    width: 175px!important;
  }

  .attribute-box >>> .el-tabs__header {
    margin: 0px;
  }

  .attribute-box >>> .el-tabs__content {
    padding: 0px;
  }

  .attribute-box >>> .el-form-item--small.el-form-item {
    margin-bottom: 15px;
  }

  .table-column-popover >>> .el-form-item--small.el-form-item {
    margin-bottom: 0px;
  }

  .full-line-btn {
    width: 100%;
    margin-top: 10px;
    border: 1px dashed #EBEEF5;
  }

  .scroll-box {
    overflow-x: hidden;
    overflow-y: auto;
    padding: 10px;
  }

  .datasource-card {
    background: white;
    border-radius: 3px;
    padding: 10px;
    margin-bottom: 10px;
  }

  .card-title {
    font-size: 14px;
    color: #043254;
    line-height: 30px;
    margin-bottom: 10px;
    font-weight: 700;
    display: flex;
    align-items: center;
    border-bottom: 1px dashed #E3E3E3;
  }

  .card-title span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 200px;
    flex-shrink: 0;
  }

  .card-item-box {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
  }

  .card-item {
    display: flex;
    position: relative;
    align-items: center;
    padding: 0px 8px;
    margin-bottom: 10px;
    background: #f3f9ff;
    border: 1px dashed #f3f9ff;
    font-size: 12px;
    color: #043254;
    cursor: move;
    border-radius: 3px;
    height: 35px;
    line-height: 35px;
    width: 110px;
  }

  .card-item .item-count {
    position: absolute;
    top: -6px;
    right: 5px;
    padding: 0px 3px;
    min-width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    border-radius: 8px;
    color: white;
    background: #F56C6C;
  }

  .card-item:hover {
    border: 1px dashed #40a0ffb0;
    color: #40a0ffb0;
  }

  .card-item span {
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
</style>
