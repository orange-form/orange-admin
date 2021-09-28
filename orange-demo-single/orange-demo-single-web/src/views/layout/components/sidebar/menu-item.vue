<template>
  <div class="menu-wrapper">
    <el-menu-item ref="item" :index="menu.menuId + ''" :key="menu.menuId" v-if="menu.children == null || menu.children.length <= 0">
      <template slot="title">
        <i v-if="menu.icon" :class="menu.icon" style="margin-right: 5px; font-size: 18px;" :style="getIconStyle(menu.icon)"></i>
        <span slot="title" :style="getTextStyle(!menu.icon)">{{menu.menuName}}</span>
      </template>
    </el-menu-item>
    <el-submenu v-else :index="menu.menuId + ''" :key="menu.menuId">
      <template slot="title">
        <i v-if="menu.icon" :class="menu.icon" style="margin-right: 5px; font-size: 18px;" :style="getIconStyle(menu.icon)"></i>
        <span slot="title" :style="getTextStyle(!menu.icon)" v-show="!getCollapse">{{menu.menuName}}</span>
      </template>
      <template v-for="child in menu.children">
        <menu-item class="nest-menu" :menu="child" :isChild="true" :key="child.menuId" />
      </template>
    </el-submenu>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
export default {
  name: 'menuItem',
  props: {
    menu: {
      type: Object,
      required: true,
      default: undefined
    },
    isChild: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    getIconStyle (isShow) {
      if (isShow && this.isChild) {
        return [
          { 'margin-left': '13px' }
        ]
      }
    },
    getTextStyle (isShow) {
      if (isShow && this.isChild) {
        return [
          { 'padding-left': '13px' }
        ]
      }
    }
  },
  computed: {
    showText () {
      return !this.getCollapse;
    },
    ...mapGetters(['getCollapse'])
  }
}
</script>
