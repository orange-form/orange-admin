<template>
  <el-col :span="widgetConfig.span || 24">
    <!-- 表单输入组件 -->
    <template v-if="widgetConfig.widgetKind === SysCustomWidgetKind.Form">
      <!-- 只读或者文本字段 -->
      <template v-if="widgetConfig.widgetType === SysCustomWidgetType.Label || widgetConfig.readOnly">
        <el-form-item :label="widgetConfig.showName + '：'">
          <span v-if="widgetConfig.widgetType === SysCustomWidgetType.Label ||
            widgetConfig.widgetType === SysCustomWidgetType.NumberInput ||
            widgetConfig.widgetType === SysCustomWidgetType.Input ||
            widgetConfig.widgetType === SysCustomWidgetType.Date"
          >
            {{value}}
          </span>
          <span v-else-if="widgetConfig.widgetType === SysCustomWidgetType.Select">{{getDictValue(value)}}</span>
          <div v-else-if="widgetConfig.widgetType === SysCustomWidgetType.RichEditor" v-html="value" />
          <span v-else-if="widgetConfig.widgetType === SysCustomWidgetType.Switch">{{value ? '是' : '否'}}</span>
        </el-form-item>
      </template>
      <template v-else>
        <el-form-item :label="widgetConfig.showName + '：'"
          :prop="(widgetConfig.relation ? (widgetConfig.relation.variableName + '__') : '') + (widgetConfig.column || {}).columnName">
          <el-input v-if="widgetConfig.widgetType === SysCustomWidgetType.Input"
            class="input-class" clearable
            :type="widgetConfig.type"
            :autosize="{minRows: widgetConfig.minRows || 1, maxRows: widgetConfig.maxRows || 1}"
            :disabled="widgetConfig.disabled"
            :value="value" @input="handlerWidgetInput"
            :placeholder="widgetConfig.placeholder"
          />
          <el-input-number v-if="widgetConfig.widgetType === SysCustomWidgetType.NumberInput"
            class="input-item" clearable
            :disabled="widgetConfig.disabled"
            :min="widgetConfig.min"
            :max="widgetConfig.max"
            :step="widgetConfig.step"
            :precision="widgetConfig.precision"
            :controls="widgetConfig.controlVisible === 1"
            :controls-position="widgetConfig.controlPosition === 1 ? 'right' : undefined"
            :value="value" @input="handlerWidgetInput"
            :placeholder="widgetConfig.placeholder"
          />
          <el-select v-if="widgetConfig.widgetType === SysCustomWidgetType.Select"
            class="input-item" clearable filterable
            :disabled="widgetConfig.disabled"
            :value="value" @input="handlerWidgetInput"
            :placeholder="widgetConfig.placeholder"
            :loading="dropdownWidget.loading"
            @visible-change="(isShow) => dropdownWidget.onVisibleChange(isShow).catch(e => {})"
            @change="handlerWidgetChange"
          >
            <el-option v-for="item in dropdownWidget.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
          <el-cascader v-if="widgetConfig.widgetType === SysCustomWidgetType.Cascader"
            class="input-item" filterable
            :options="dropdownWidget.dropdownList"
            :value="cascaderValue" @input="handlerWidgetInput"
            :clearable="true" :show-all-levels="false"
            :placeholder="widgetConfig.placeholder"
            :props="{value: 'id', label: 'name', checkStrictly: true}"
            @visible-change="(isShow) => dropdownWidget.onVisibleChange(isShow).catch(e => {})"
            @change="handlerWidgetChange"
          />
          <el-date-picker v-if="widgetConfig.widgetType === SysCustomWidgetType.Date"
            :format="widgetConfig.format || 'yyyy-MM-dd'" :value-format="widgetConfig.valueFormat || 'yyyy-MM-dd 00:00:00'"
            class="input-item" clearable :type="widgetConfig.type || 'date'" align="left"
            :disabled="widgetConfig.disabled"
            :value="value" @input="handlerWidgetInput"
            :placeholder="widgetConfig.placeholder"
            @change="handlerWidgetChange"
          />
          <rich-editor v-if="widgetConfig.widgetType === SysCustomWidgetType.RichEditor"
            class="input-item" :value="value" @input="handlerWidgetInput" />
          <el-switch v-if="widgetConfig.widgetType === SysCustomWidgetType.Switch"
            class="input-item" :value="value" @input="handlerWidgetInput" />
        </el-form-item>
      </template>
    </template>
    <!-- 数据展示组件 -->
    <template v-else-if="widgetConfig.widgetKind === SysCustomWidgetKind.Data">
      <el-divider v-if="widgetConfig.widgetType === SysCustomWidgetType.Divider"
        :content-position="widgetConfig.position">
        {{widgetConfig.showName}}
      </el-divider>
      <CustomText v-else-if="widgetConfig.widgetType === SysCustomWidgetType.Text" :widgetConfig="widgetConfig" />
      <CustomImage v-else-if="widgetConfig.widgetType === SysCustomWidgetType.Image" :widgetConfig="widgetConfig" />
    </template>
    <!-- 容器组件 -->
    <template v-else-if="widgetConfig.widgetKind === SysCustomWidgetKind.Container">
      <el-card v-if="widgetConfig.widgetType === SysCustomWidgetType.Card"
        class="base-card" :shadow="widgetConfig.shadow" :body-style="{padding: widgetConfig.padding ? widgetConfig.padding + 'px' : '0px'}"
        :style="{
          height: (widgetConfig.height != null && widgetConfig.height !=='') ? widgetConfig.height + 'px' : undefined,
          'margin-bottom': widgetConfig.supportBottom === 1 ? '20px' : undefined
        }">
        <div slot="header" class="base-card-header">
          <span>{{widgetConfig.showName}}</span>
        </div>
        <el-row :gutter="widgetConfig.gutter">
          <slot></slot>
        </el-row>
      </el-card>
    </template>
  </el-col>
</template>

<script>
import { DropdownWidget } from '@/utils/widget.js';
import { getDictDataList } from '../utils';
import { findItemFromList, findTreeNodePath } from '@/utils';
import CustomText from './customText.vue';
import CustomImage from './customImage.vue';

export default {
  props: {
    formConfig: {
      type: Object,
      required: true
    },
    widgetConfig: {
      type: Object,
      required: true
    },
    value: {
      type: [String, Number, Date, Object, Array, Boolean]
    },
    getDropdownParams: {
      type: Function
    }
  },
  inject: ['preview'],
  components: {
    CustomText,
    CustomImage
  },
  data () {
    return {
      cascaderValue: [],
      dropdownWidget: (this.widgetConfig.widgetType === this.SysCustomWidgetType.Select || this.widgetConfig.widgetType === this.SysCustomWidgetType.Cascader)
        ? new DropdownWidget(this.loadDropdwonList, this.widgetConfig.widgetType === this.SysCustomWidgetType.Cascader) : undefined
    }
  },
  methods: {
    reset () {
      this.handlerWidgetInput(undefined);
      this.cascaderValue = [];
      if (this.dropdownWidget) this.dropdownWidget.dirty = true;
    },
    loadDropdwonList () {
      if (this.preview()) {
        return Promise.resolve([]);
      }
      return new Promise((resolve, reject) => {
        if (this.widgetConfig.column != null && this.widgetConfig.column.dictInfo != null) {
          let params = {};
          let dictInfo = this.widgetConfig.column.dictInfo;
          if (dictInfo.dictType === this.SysOnlineDictType.TABLE ||
            dictInfo.dictType === this.SysOnlineDictType.URL) {
            params = this.getDropdownParams ? this.getDropdownParams(this.widgetConfig) : {};
          }
          if (params == null) {
            reject();
          } else {
            getDictDataList(this, dictInfo, params).then(res => {
              resolve(res);
            }).catch(e => {
              console.log(e);
              reject(e);
            });
          }
        } else {
          reject();
        }
      });
    },
    getDropdownImpl () {
      return this.dropdownWidget;
    },
    onVisibleChange () {
      if (this.dropdownWidget) {
        this.dropdownWidget.onVisibleChange(true).then(res => {
          // 级联组件获取下拉数据，计算组件显示path
          if (this.widgetConfig.widgetType === this.SysCustomWidgetType.Cascader) {
            this.cascaderValue = findTreeNodePath(res, this.value, 'id');
          }
        });
      }
    },
    handlerWidgetInput (value) {
      if (this.widgetConfig.widgetType === this.SysCustomWidgetType.Cascader) {
        this.cascaderValue = value;
        if (Array.isArray(value) && value.length > 0) {
          this.$emit('input', value[value.length - 1], this.widgetConfig);
        } else {
          this.$emit('input', undefined, this.widgetConfig);
        }
      } else {
        this.$emit('input', value, this.widgetConfig);
      }
    },
    handlerWidgetChange (value) {
      if (this.widgetConfig.widgetType === this.SysCustomWidgetType.Cascader) {
        if (Array.isArray(value) && value.length > 0) {
          this.$emit('change', value[value.length - 1], this.widgetConfig);
        } else {
          this.$emit('change', undefined, this.widgetConfig);
        }
      } else {
        this.$emit('change', value, this.widgetConfig);
      }
    },
    // 获得字典值
    getDictValue (id) {
      if (this.dropdownWidget && Array.isArray(this.dropdownWidget.dropdownList)) {
        return (findItemFromList(this.dropdownWidget.dropdownList, id, 'id') || {}).name;
      } else {
        return '';
      }
    }
  },
  mounted () {
  }
}
</script>

<style>
</style>
