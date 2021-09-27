<template>
  <el-col :span="widgetConfig.span" class="editable-widget-item"
    :class="{'draggable-item': draggable, 'has-error': widgetConfig.hasError}"
    :style="{width: widgetConfig.widgetKind === SysCustomWidgetKind.Filter ? (formConfig().labelWidth + 272 + 'px') : undefined}"
    @click.stop.native="onClick">
    <i v-if="canDelete" class="el-icon-delete drag-widget-delete" style="color: #F56C6C;" @click.stop="onDeleteClick" />
    <template v-if="widgetConfig.widgetKind === SysCustomWidgetKind.Filter">
      <CustomFilterWidget class="drag-widget-item" :widgetConfig="{ ...widgetConfig }" :value="widgetConfig.defaultValue" @input="() => {}" />
    </template>
    <template v-else>
      <CustomTable v-if="widgetConfig.widgetType === SysCustomWidgetType.Table"
        class="drag-widget-item" :formType="formConfig().formType" :widgetConfig="{ ...widgetConfig, span: null }" :dictList="{}" />
      <CustomUpload v-else-if="widgetConfig.widgetType === SysCustomWidgetType.Upload"
        class="drag-widget-item" :widgetConfig="{ ...widgetConfig, span: null }"
        :value="widgetConfig.defaultValue" @input="() => {}" />
      <CustomWidget v-else class="drag-widget-item"
        :formConfig="formConfig()"
        :widgetConfig="{ ...widgetConfig, span: null }"
        :value="widgetConfig.defaultValue" @input="() => {}">
        <Draggable draggable=".draggable-item" :list="widgetConfig.childWidgetList" style="min-height: 50px;"
          group="componentsGroup" :animation="340"
        >
          <template v-if="Array.isArray(widgetConfig.childWidgetList) && widgetConfig.childWidgetList.length > 0">
            <drag-widget-item v-for="child in widgetConfig.childWidgetList" :key="child.id"
              :class="{'active-widget': child === current()}"
              @click="onChildClick"
              @delete="onChildDeleteClick"
              :widgetConfig="child" />
          </template>
        </Draggable>
      </CustomWidget>
    </template>
  </el-col>
</template>

<script>
import Draggable from 'vuedraggable';
import CustomWidget from './customWidget.vue';
import CustomTable from './customTable.vue';
import CustomUpload from './customUpload.vue';
import CustomFilterWidget from './customFilterWidget.vue';

export default {
  components: {
    Draggable,
    CustomWidget,
    CustomTable,
    CustomUpload,
    CustomFilterWidget
  },
  inject: ['current', 'isLocked', 'formConfig'],
  props: {
    widgetConfig: {
      type: Object,
      required: true
    },
    canDelete: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      value: this.widgetConfig.defaultValue
    }
  },
  methods: {
    onClick () {
      this.$emit('click', this.widgetConfig);
    },
    onChildClick (element) {
      this.$emit('click', element);
    },
    onDeleteClick () {
      this.$emit('delete', this.widgetConfig);
    },
    onChildDeleteClick (element) {
      this.widgetConfig.childWidgetList = this.widgetConfig.childWidgetList.filter(item => {
        return item.id !== element.id;
      });
      this.$emit('delete', this.widgetConfig, true);
    }
  },
  computed: {
    draggable () {
      return !this.isLocked() || this.widgetConfig.widgetKind !== this.SysCustomWidgetKind.Container;
    }
  },
  mounted () {
  }
}
</script>

<style scoped>
  .editable-widget-item {
    position: relative;
    margin-bottom: 10px;
  }

  .editable-widget-item.active-widget > .drag-widget-item {
    background: #b0d7fd;
    border: 1px dashed #77b9fc;
  }

  .has-error {
    background: #fdd5d5!important;
    border: 1px dashed #F56C6C!important;
  }

  .drag-widget-item {
    cursor: move;
    border: 1px dashed #e2e0e0;
    padding: 13px 10px;
  }

  .drag-widget-item:hover {
    background: #f3f9ff;
  }

  .has-error .drag-widget-item:hover {
    background: #fdd5d5!important;
  }

  .drag-widget-delete {
    border-color: #F56C6C;
    width: 22px;
    height: 22px;
    top: -10px;
    right: 24px;
    line-height: 22px;
    text-align: center;
    border-radius: 50%;
    font-size: 12px;
    border: 1px solid;
    cursor: pointer;
    z-index: 1;
    position: absolute;
    display: none;
  }

  .editable-widget-item:hover > .drag-widget-delete {
    display: block;
  }
</style>

<style>
  .editable-widget-item .el-form-item {
    margin: 0px !important;
  }
</style>
