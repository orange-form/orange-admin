<template>
  <el-popover width="510" v-model="showDropdown" @show="onDropdownShow">
    <div class="icon-select-dropdown">
      <el-row type="flex" style="flex-wrap: wrap">
        <el-col :span="3" v-for="icon in getIconList" :key="icon" class="icon-item"
          :class="{active: (value === icon)}" @click.native="onIconClick(icon)">
          <i :class="icon" />
        </el-col>
      </el-row>
      <el-row type="flex" justify="space-between">
        <el-button type="text" @click="onClearClick" style="margin-left: 10px;">清空</el-button>
        <el-pagination
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="prev, pager, next"
          :total="getIconCount">
        </el-pagination>
      </el-row>
    </div>
    <div slot="reference" class="icon-select"
      :style="{width: height + 'px', height: height + 'px', 'line-height': height + 'px', 'font-size': height * 0.5 + 'px'}">
      <i :class="value" />
    </div>
  </el-popover>
</template>

<script>
import iconList from './icon.json';

export default {
  props: {
    /**
     * 绑定字段
     */
    value: String,
    /**
     * 组件高度，单位px
     */
    height: {
      type: Number,
      default: 45
    }
  },
  data () {
    return {
      showDropdown: false,
      currentPage: 1,
      pageSize: 32
    }
  },
  methods: {
    onIconClick (icon) {
      this.$emit('input', icon);
      this.showDropdown = false;
    },
    onClearClick () {
      this.$emit('input');
      this.showDropdown = false;
    },
    onDropdownShow () {
      this.currentPage = 1
      let pos = iconList.indexOf(this.value);
      if (pos >= 0) {
        this.currentPage += Math.floor(pos / this.pageSize);
      }
    }
  },
  computed: {
    getIconCount () {
      return iconList.length;
    },
    getIconList () {
      let beginPos = (this.currentPage - 1) * this.pageSize;
      let endPos = beginPos + this.pageSize;
      return iconList.slice(beginPos, endPos);
    }
  }
}
</script>

<style scoped>
  .icon-select {
    text-align: center;
    color: #5F6266;
    border: 1px solid #DCDFE6;
    border-radius: 4px;
    cursor: pointer;
  }
  .icon-item {
    width: 40px;
    height: 40px;
    line-height: 40px;
    text-align: center;
    font-size: 20px;
    color: #5F6266;
    border-radius: 3px;
    border: 1px solid #DCDFE6;
    margin: 10px;
    cursor: pointer;
  }
  .active {
    color: #EF5E1C;
  }
</style>
